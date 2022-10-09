package com.darpan.project.veggiesadmin.projectModal;

import com.darpan.project.veggiesadmin.firebaseModal.ProductModalForeSale;



public class MultiUploadModal {
    private ProductModalForeSale PMS;
    private String productName;
    private String productId;
    private int productPrice;
    private int productQuantity;
    private String fileName;
    private String imageUri;
    private int progress;


    public MultiUploadModal() {
    }

    public MultiUploadModal(ProductModalForeSale PMS,
                            String productName,
                            String productId, int productPrice,
                            int productQuantity,
                            String fileName,
                            String imageUri, int progress) {
        this.PMS = PMS;
        this.productName = productName;
        this.productId = productId;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
        this.fileName = fileName;
        this.imageUri = imageUri;
        this.progress = progress;
    }

    public ProductModalForeSale getPMS() {
        return PMS;
    }

    public void setPMS(ProductModalForeSale PMS) {
        this.PMS = PMS;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
