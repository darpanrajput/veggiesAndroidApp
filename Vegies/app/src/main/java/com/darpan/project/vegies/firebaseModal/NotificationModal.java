package com.darpan.project.vegies.firebaseModal;

import com.google.firebase.firestore.Exclude;

public  class NotificationModal {

    @Exclude
    private String snapId;
    private String message;
    private String time;
    private String title;

    public NotificationModal() {
    }

    public NotificationModal(String message, String time, String title) {
        this.message = message;
        this.time = time;
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnapId() {
        return snapId;
    }

    public void setSnapId(String snapId) {
        this.snapId = snapId;
    }
}
