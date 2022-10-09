package com.darpan.project.veggiesadmin.firebaseModal;

public class BannerModal {
    private String bannerImage;
    private String bannerName;

    public BannerModal() {
    }

    public BannerModal(String bannerImageUrl, String bannerImageName) {
        this.bannerImage = bannerImageUrl;
        this.bannerName = bannerImageName;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public String getBannerName() {
        return bannerName;
    }

    public void setBannerName(String bannerName) {
        this.bannerName = bannerName;
    }
}
