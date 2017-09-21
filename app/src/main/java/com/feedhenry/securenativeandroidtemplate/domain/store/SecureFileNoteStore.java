package com.feedhenry.securenativeandroidtemplate.domain.store;

import android.content.Context;

import com.feedhenry.securenativeandroidtemplate.domain.crypto.AesGcmCrypto;
import com.feedhenry.securenativeandroidtemplate.domain.models.Note;
import com.feedhenry.securenativeandroidtemplate.domain.utils.StreamUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

/**
 * Implement the note storage using the file system. Each note object is saved in its own file and encrypted with its own secret key.
 */

public class SecureFileNoteStore implements NoteDataStore {

    private static final String NOTES_METADATA_FILENAME = "notes_meta.json";

    Context context;
    AesGcmCrypto aesCrypto;

    private JSONObject notesMetadata = new JSONObject();
    private boolean metadataLoaded = false;

    @Inject
    public SecureFileNoteStore(Context context, AesGcmCrypto aesCrypto) {
        this.context = context;
        this.aesCrypto = aesCrypto;
    }

    @Override
    public Note createNote(Note note) throws Exception {
        return saveNote(note);
    }

    @Override
    public Note updateNote(Note note) throws Exception {
        return saveNote(note);
    }

    private Note saveNote(Note note) throws Exception {
        loadMetadata();
        JSONObject noteJsonNoContent = note.toJson(false);
        notesMetadata.put(note.getId(), noteJsonNoContent);
        writeFileWithEncryption(NOTES_METADATA_FILENAME, notesMetadata.toString());

        JSONObject noteJsonWithContent = note.toJson(true);
        writeFileWithEncryption(note.getId(), noteJsonWithContent.toString());
        return note;
    }

    @Override
    public Note deleteNote(Note note) throws Exception {
        loadMetadata();
        notesMetadata.remove(note.getId());
        writeFileWithEncryption(NOTES_METADATA_FILENAME, notesMetadata.toString());

        removeFile(note.getId());
        aesCrypto.deleteSecretKey(note.getId());
        return note;
    }

    @Override
    public Note readNote(String noteId) throws Exception {
        loadMetadata();
        if (!notesMetadata.has(noteId)) {
            throw new Exception("can not find note with id " + noteId);
        }
        String noteJson = readFileWithDecryption(noteId);
        Note note = Note.fromJSON(new JSONObject(noteJson));
        return note;
    }

    @Override
    public List<Note> listNotes() throws Exception {
        loadMetadata();
        List<Note> notes = convertToList(notesMetadata);
        return notes;
    }

    private void loadMetadata() throws GeneralSecurityException, IOException {
        if (!metadataLoaded) {
            try {
                String content = readFileWithDecryption(NOTES_METADATA_FILENAME);
                if (content.length() > 0 && content.startsWith("{")) {
                    notesMetadata = new JSONObject(content);
                } else {
                    notesMetadata = new JSONObject();
                }
            } catch (FileNotFoundException notFound) {
                //ignore it
            } catch (JSONException je) {

            } finally {
                metadataLoaded = true;
            }
        }
    }

    private void writeFileWithEncryption(String fileName, String fileContent) throws IOException, GeneralSecurityException {
        File outputFile = new File(context.getFilesDir(), fileName);
        if (!outputFile.exists()) {
            outputFile.createNewFile();
        }
        OutputStream outStream = aesCrypto.encryptStream(fileName, new FileOutputStream(outputFile));
        outStream.write(fileContent.getBytes("utf-8"));
        outStream.flush();
        outStream.close();
    }

    private String readFileWithDecryption(String fileName) throws IOException, GeneralSecurityException {
        InputStream inputStream = context.openFileInput(fileName);
        InputStream decryptedStream = aesCrypto.decryptStream(fileName, inputStream);

        return StreamUtils.readStream(decryptedStream);
    }

    private void removeFile(String fileName) {
        File target = new File(context.getFilesDir(), fileName);
        if (target.exists()) {
            target.delete();
        }
    }

    private List<Note> convertToList(JSONObject notesMetaData) throws JSONException {
        List<Note> notes = new ArrayList<Note>();
        Iterator<String> keys = notesMetaData.keys();
        while (keys.hasNext()) {
            String noteId = keys.next();
            JSONObject noteJson = notesMetaData.getJSONObject(noteId);
            notes.add(Note.fromJSON(noteJson));
        }
        return notes;
    }
}
