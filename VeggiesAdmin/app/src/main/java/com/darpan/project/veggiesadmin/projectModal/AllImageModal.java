package com.darpan.project.veggiesadmin.projectModal;

public class AllImageModal {
   private String imageName;
    private String imageUrl;
    private String imageId;

    public AllImageModal() {
    }
    public AllImageModal(String imageName, String imageUrl, String imageId) {
        this.imageName = imageName;
        this.imageUrl = imageUrl;
        this.imageId = imageId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }


}
