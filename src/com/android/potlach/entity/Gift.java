package com.android.potlach.entity;

import com.google.common.base.Objects;

import java.util.Date;

public class Gift {
    private long id;
    private String title;
    private String description;
    private String uploader;
    private long dateAdded;
    private long parent;
    private long touches;
    private boolean obscene;


    public Gift(String title) {
        this.title = title;
        this.dateAdded = new Date().getTime();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public long getParent() {
        return parent;
    }

    public void setParent(long parent) {
        this.parent = parent;
    }

    public long getTouches() {
        return touches;
    }

    public void setTouches(long touches) {
        this.touches = touches;
    }

    public boolean isObscene() {
        return obscene;
    }

    public void setObscene(boolean obscene) {
        this.obscene = obscene;
    }

    /**
     * Two Videos will generate the same hashcode if they have exactly the same
     * values for their name, url, and duration.
     *
     */
    @Override
    public int hashCode() {
        // Google Guava provides great utilities for hashing
        return Objects.hashCode(id, title, uploader);
    }

    /**
     * Two Videos are considered equal if they have exactly the same values for
     * their name, url, and duration.
     *
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Gift) {
            Gift other = (Gift) obj;
            // Google Guava provides great utilities for equals too!
            return Objects.equal(id, other.id) &&
                    Objects.equal(title, other.title) &&
                    Objects.equal(uploader, other.uploader);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Gift[" + id + "] " + title;
    }
}
