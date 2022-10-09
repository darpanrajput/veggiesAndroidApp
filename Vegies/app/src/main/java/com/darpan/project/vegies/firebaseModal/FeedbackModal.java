package com.darpan.project.vegies.firebaseModal;

public class FeedbackModal {
    private String Rating;
    private String feedback;
    private String userEmail;
    private String userId;
    private String userName;



    public FeedbackModal() {
    }

    public FeedbackModal(String rating,
                         String feedback,
                         String userEmail,
                         String userId, String userName) {
        Rating = rating;
        this.feedback = feedback;
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

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }
}
