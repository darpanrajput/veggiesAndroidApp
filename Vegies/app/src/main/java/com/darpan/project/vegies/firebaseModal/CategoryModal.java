package com.darpan.project.vegies.firebaseModal;

import com.google.firebase.firestore.Exclude;

import java.util.List;

public class CategoryModal {
    private String categoryName;
    private String categoryImage;
    private String snapId;
    @Exclude
    private List<ProductModalForeSale> PMSList;

    public CategoryModal(String categoryName, String categoryImage) {
        this.categoryName = categoryName;
        this.categoryImage = categoryImage;
    }

    public String getSnapId() {
        return snapId;
    }

    public void setSnapId(String snapId) {
        this.snapId = snapId;
    }

    public CategoryModal() {
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public List<ProductModalForeSale> getPMSList() {
        return PMSList;
    }

    public void setPMSList(List<ProductModalForeSale> PMSList) {
        this.PMSList = PMSList;
    }
}
