package com.darpan.project.vegies.firebaseModal.offer;

import com.google.firebase.firestore.Exclude;

public class OfferModal {
    private String offerText;
    @Exclude
    private String offerFrequency;
    private boolean offerVisibility;
    @Exclude
    private String offerDuration;

    public OfferModal(String offerText, boolean offerVisibility) {
        this.offerText = offerText;
        this.offerVisibility = offerVisibility;
    }

    public OfferModal() {
    }

    public String getOfferText() {
        return offerText;
    }

    public void setOfferText(String offerText) {
        this.offerText = offerText;
    }
    @Exclude
    public String getOfferFrequency() {
        return offerFrequency;
    }

    public void setOfferFrequency(String offerFrequency) {
        this.offerFrequency = offerFrequency;
    }

    public boolean getOfferVisibility() {
        return offerVisibility;
    }

    public void setOfferVisibility(boolean offerVisibility) {
        this.offerVisibility = offerVisibility;
    }

    @Exclude
    public String getOfferDuration() {
        return offerDuration;
    }

    public void setOfferDuration(String offerDuration) {
        this.offerDuration = offerDuration;
    }
}
