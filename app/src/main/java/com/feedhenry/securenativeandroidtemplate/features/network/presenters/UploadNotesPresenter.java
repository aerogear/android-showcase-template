package com.feedhenry.securenativeandroidtemplate.features.network.presenters;

import android.os.AsyncTask;
import android.util.Log;

import com.datatheorem.android.trustkit.TrustKit;
import com.feedhenry.securenativeandroidtemplate.R;
import com.feedhenry.securenativeandroidtemplate.domain.Constants;
import com.feedhenry.securenativeandroidtemplate.domain.callbacks.Callback;
import com.feedhenry.securenativeandroidtemplate.domain.configurations.ApiServerConfiguration;
import com.feedhenry.securenativeandroidtemplate.domain.configurations.AppConfiguration;
import com.feedhenry.securenativeandroidtemplate.domain.models.Note;
import com.feedhenry.securenativeandroidtemplate.domain.repositories.NoteRepository;
import com.feedhenry.securenativeandroidtemplate.domain.services.AuthStateService;
import com.feedhenry.securenativeandroidtemplate.domain.services.NoteCrudlService;
import com.feedhenry.securenativeandroidtemplate.features.network.views.UploadNotesView;
import com.feedhenry.securenativeandroidtemplate.mvp.components.HttpHelper;
import com.feedhenry.securenativeandroidtemplate.mvp.presenters.BasePresenter;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by weili on 27/10/2017.
 */

public class UploadNotesPresenter extends BasePresenter<UploadNotesView> {

    private static final String TAG = "UploadNotesPresenter";
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private NoteRepository noteRepository;
    private AuthStateService authStateService;
    private ApiServerConfiguration apiServerConfiguration;

    private UploadNotesTask uploadNotesTask;

    @Inject
    public UploadNotesPresenter(NoteRepository noteRepos, AuthStateService authStateService, AppConfiguration appConfiguration){
        this.noteRepository = noteRepos;
        this.authStateService = authStateService;
        this.apiServerConfiguration = appConfiguration.getAPIServerConfiguration();
    }

    public void checkClientStatus() {
        new ClientStatusCheckerTask().execute();
    }

    public void uploadNotes() {
        uploadNotesTask = new UploadNotesTask();
        uploadNotesTask.execute();
    }

    public void cancelUpload() {
        if (uploadNotesTask != null) {
            uploadNotesTask.cancel(true);
        }
    }

    private class ClientStatus {
        private boolean hasPermission;
        private long numberOfNotes;

        ClientStatus(boolean hasPerm, long numberOfNotes) {
            this.hasPermission = hasPerm;
            this.numberOfNotes = numberOfNotes;
        }
    }

    private class ClientStatusCheckerTask extends AsyncTask<Void, Void, ClientStatus> {

        private Exception error;
        String requiredRole = Constants.ACCESS_CONTROL_ROLES.ROLE_API_ACCESS;

        @Override
        protected void onPreExecute() {
            if (view != null) {
                view.showLoading();
            }
        }

        // tag::checkPermission[]
        @Override
        protected ClientStatus doInBackground(Void... voids) {
            ClientStatus status = null;
            try {
                boolean hasPermission = authStateService.hasRole(requiredRole);
                long numberOfNotes = noteRepository.count();
                status = new ClientStatus(hasPermission, numberOfNotes);
                return status;
            } catch (Exception e) {
                Log.e(TAG, "Error - Exception", e);
                this.error = e;
            }
            return status;
        }
        // end::checkPermission[]

        @Override
        protected void onPostExecute(ClientStatus clientStatus) {
            if (view != null) {
                view.hideLoading();
                if (this.error != null) {
                    view.showMessage(this.error.getMessage());
                } else {
                    view.renderPermissionMessage(clientStatus.hasPermission, requiredRole);
                    view.renderNotesMessage(clientStatus.numberOfNotes);
                }
            }
        }
    }

    private class UploadNotesTask extends AsyncTask<Void, Long, Long> {

        private Exception error;

        @Override
        protected void onPreExecute() {
            if (view != null) {
                view.showProgressBar();
            }
        }

        // tag::invokeAPI[]
        @Override
        protected Long doInBackground(Void... voids) {
            String apiUrl = apiServerConfiguration.getNoteAPIUrl();
            String accessToken = authStateService.getAccessToken();
            long uploaded = 0;
            try {
                URL url = new URL(apiUrl);
                String hostname = url.getHost();

                SSLSocketFactory sslSocketFactory = TrustKit.getInstance().getSSLSocketFactory(hostname);
                X509TrustManager trustManager = TrustKit.getInstance().getTrustManager(hostname);

                OkHttpClient httpClient = HttpHelper.getHttpClient()
                        .sslSocketFactory(sslSocketFactory, trustManager)
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .build();

                List<Note> notes = noteRepository.listNotes();
                long totalNumber = notes.size();
                long currentCount = 0;
                for (Note note : notes) {
                    if (isCancelled() || this.error != null) {
                        break;
                    }
                    Note readNote = noteRepository.readNote(note.getId());
                    RequestBody requestBody = RequestBody.create(JSON, readNote.toJson(true).toString());
                    Request request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .addHeader("Authorization", String.format("Bearer %s", accessToken))
                            .build();
                    Response res = httpClient.newCall(request).execute();
                    currentCount++;
                    publishProgress(currentCount, totalNumber);
                    if (res.isSuccessful()) {
                        uploaded++;
                    } else {
                        this.error = new Exception(res.body().string());
                    }
                }
            } catch ( Exception e) {
                Log.e(TAG, "Error - Exception", e);
                this.error = e;
            }
            return uploaded;
        }
        // end::invokeAPI[]

        @Override
        protected void onPostExecute(Long completed) {
            if (view != null) {
                view.hideProgressBar();
                if (this.error != null) {
                    view.updateProgressDesc(this.error.getMessage());
                    view.showMessage(this.error.getMessage());
                } else {
                    view.showMessage(R.string.upload_complete, completed);
                }
            }
        }

        @Override
        protected void onProgressUpdate(Long... progress) {
            long completed = progress[0];
            long total = progress[1];
            if (view != null) {
                view.updateProgress(completed, total);
            }
        }

        @Override
        protected void onCancelled(Long completed) {
            if (view != null) {
                view.hideProgressBar();
                view.showMessage(R.string.upload_cancelled, completed);
            }
        }

    }
}
