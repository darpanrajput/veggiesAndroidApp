package com.darpan.project.veggiesadmin.firebaseModal;

public class FeedbackModal {
    private String feedback;
    private String rating;
    private String userEmail;
    private String userId;
    private String userName;

    public FeedbackModal() {
    }

    public FeedbackModal(String feedback, String rating, String userEmail, String userId, String userName) {
        this.feedback = feedback;
        this.rating = rating;
        this.userEmail = userEmail;
        this.userId = userId;
        this.userName = userName;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
