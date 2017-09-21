package com.feedhenry.securenativeandroidtemplate.domain.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

/**
 * Created by weili on 08/09/2017.
 */

public class Note {

    private String id;
    private String title;
    private String content;
    protected Date createdAt;

    public Note(String title, String content) {
        this.createdAt = new Date();
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.content = content;
    }

    private Note(String id, String title, String content, long createAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = new Date(createAt);
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getId() {
        return id;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public JSONObject toJson(boolean withContent) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("title", title);
        if (withContent) {
            json.put("content", content);
        }
        json.put("createdAt", createdAt.getTime());
        return json;
    }

    public static Note fromJSON(JSONObject noteJson) throws JSONException {
        String id = noteJson.getString("id");
        String title = noteJson.getString("title");
        String content = noteJson.optString("content", "");
        long createdAt = noteJson.getLong("createdAt");
        Note note = new Note(id, title, content, createdAt);
        return note;
    }
}
