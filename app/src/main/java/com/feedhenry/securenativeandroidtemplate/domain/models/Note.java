package com.feedhenry.securenativeandroidtemplate.domain.models;

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

}
