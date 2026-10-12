package com.boots.firebasetaskapp;

import com.google.firebase.firestore.DocumentId;

public class Task {

    @DocumentId
    private String id;
    private String title;
    private boolean done;
    private String userId;
    private long createdAt;

    // Firestore ke liye khali constructor zaroori hai
    public Task() {
    }

    public Task(String title, String userId) {
        this.title = title;
        this.userId = userId;
        this.done = false;
        this.createdAt = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}