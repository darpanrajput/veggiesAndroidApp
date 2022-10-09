package com.darpan.project.vegies.projectModal;

public class DevModal {
    private String title;
    private String img;
    private String description;
    private String url;

    public DevModal() {
    }

    public DevModal(String title, String img, String description, String url) {
        this.title = title;
        this.img = img;
        this.description = description;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
