package com.darpan.project.veggiesadmin.modal;

import android.os.Parcel;
import android.os.Parcelable;

public class UserDataModal implements Parcelable {
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

    public UserDataModal() {
    }

    public UserDataModal(String blockName,
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

    protected UserDataModal(Parcel in) {
        blockName = in.readString();
        blockNo = in.readString();
        email = in.readString();
        fullAddress = in.readString();
        landmark = in.readString();
        mobile = in.readString();
        name = in.readString();
        photoUrl = in.readString();
        pin = in.readString();
        status = in.readByte() != 0;
        userId = in.readString();
    }

    public static final Creator<UserDataModal> CREATOR = new Creator<UserDataModal>() {
        @Override
        public UserDataModal createFromParcel(Parcel in) {
            return new UserDataModal(in);
        }

        @Override
        public UserDataModal[] newArray(int size) {
            return new UserDataModal[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(blockName);
        dest.writeString(blockNo);
        dest.writeString(email);
        dest.writeString(fullAddress);
        dest.writeString(landmark);
        dest.writeString(mobile);
        dest.writeString(name);
        dest.writeString(photoUrl);
        dest.writeString(pin);
        dest.writeByte((byte) (status ? 1 : 0));
        dest.writeString(userId);
    }
}
