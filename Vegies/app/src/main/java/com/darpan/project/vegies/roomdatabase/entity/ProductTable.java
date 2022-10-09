package com.darpan.project.vegies.roomdatabase.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity(tableName = "product_table")

public class ProductTable {
    @PrimaryKey(autoGenerate = true)
    private int ID;


    private String productCategory;
    private String productId;
    private String productDescription;
    private String productName;
    private String productPrice;//actual price of the product
    private String discountPer;//discount percentage;
    private String stockQuantity;
    private String optionQty;
    private String productStatus;
    private String productType;
    private String productUnit;
    private String productImage;
    private String uniquePid;
    private int savedQty;
    private String originalPrice;
    private String originalQty;
    /*Avialable options list*/
    private ArrayList<String> AvailableOptions=new ArrayList<>();

    public  ProductTable() {
    }

    public ProductTable(String productCategory, String productId,
                        String productDescription, String productName,
                        String productPrice, String discountPer,
                        String stockQuantity, String optionQty,
                        String productStatus, String productType,
                        String productUnit, String productImage,
                        String uniquePid, int savedQty,
                        String originalPrice,String originalQty) {
        this.productCategory = productCategory;
        this.productId = productId;
        this.productDescription = productDescription;
        this.productName = productName;
        this.productPrice = productPrice;
        this.discountPer = discountPer;
        this.stockQuantity = stockQuantity;
        this.optionQty = optionQty;
        this.productStatus = productStatus;
        this.productType = productType;
        this.productUnit = productUnit;
        this.productImage = productImage;
        this.uniquePid = uniquePid;
        this.savedQty = savedQty;
        this.originalPrice=originalPrice;
        this.originalQty=originalQty;
    }

    public int getSavedQty() {
        return savedQty;
    }

    public void setSavedQty(int savedQty) {
        this.savedQty = savedQty;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getDiscountPer() {
        return discountPer;
    }

    public void setDiscountPer(String discountPer) {
        this.discountPer = discountPer;
    }

    public String getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(String stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getOptionQty() {
        return optionQty;
    }

    public void setOptionQty(String optionQty) {
        this.optionQty = optionQty;
    }

    public String getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getProductUnit() {
        return productUnit;
    }

    public void setProductUnit(String productUnit) {
        this.productUnit = productUnit;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getUniquePid() {
        return uniquePid;
    }

    public void setUniquePid(String uniquePid) {
        this.uniquePid = uniquePid;
    }


    @Override
    public String toString() {
        return "ProductTable{" +
                "\nID=" + ID +
                "\n, productCategory='" + productCategory + '\'' +
                "\n, productId='" + productId + '\'' +
                "\n, productDescription='" + productDescription + '\'' +
                "\n, productName='" + productName + '\'' +
                "\n, productPrice='" + productPrice + '\'' +
                "\n, discountPer='" + discountPer + '\'' +
                "\n, stockQuantity='" + stockQuantity + '\'' +
                "\n, optionQty='" + optionQty + '\'' +
                "\n, productStatus='" + productStatus + '\'' +
                "\n, productType='" + productType + '\'' +
                "\n, productUnit='" + productUnit + '\'' +
                "\n, productImage='" + productImage + '\'' +
                "\n, uniquePid='" + uniquePid + '\'' +
                "\n, savedQty=" + savedQty +
                "\n, originalPrice='" + originalPrice + '\'' +
                "\n, originalQty='" + originalQty + '\'' +
                "\n, Available Option='" + AvailableOptions+ '\'' +
                '}';
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getOriginalQty() {
        return originalQty;
    }

    public void setOriginalQty(String originalQty) {
        this.originalQty = originalQty;
    }

    public ArrayList<String> getAvailableOptions() {
        return AvailableOptions;
    }

    public void setAvailableOptions(ArrayList<String> availableOptions) {
        AvailableOptions = availableOptions;
    }
}
