package com.darpan.project.veggiesadmin.firebaseModal;

public class UserModal {
    private String blockName;
    private String blockNo;
    private String email;
    private String fullAddress;
    private String landmark;
    private String mobile;
    private String name;
    private String photoUrl;
    private String pin;
    private boolean status;
    private String userId;

    public UserModal() {
    }

    public UserModal(String blockName,
                     String blockNo,
                     String email,
                     String fullAddress,
                     String landmark,
                     String mobile,
                     String name,
                     String photoUrl,
                     String pin,
                     boolean status,
                     String userId) {
        this.blockName = blockName;
        this.blockNo = blockNo;
        this.email = email;
        this.fullAddress = fullAddress;
        this.landmark = landmark;
        this.mobile = mobile;
        this.name = name;
        this.photoUrl = photoUrl;
        this.pin = pin;
        this.status = status;
        this.userId = userId;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public String getBlockNo() {
        return blockNo;
    }

    public void setBlockNo(String blockNo) {
        this.blockNo = blockNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
