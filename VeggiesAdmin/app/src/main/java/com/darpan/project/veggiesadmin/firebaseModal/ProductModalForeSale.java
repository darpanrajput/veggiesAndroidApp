package com.darpan.project.veggiesadmin.firebaseModal;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ProductModalForeSale implements Parcelable {
    private String UniquePid;
    private boolean isPublished;
    private List<String> optionQty;
    private String productCategory;
    private String productId;
    private String productDescription;
    private String productName;
    private int productPrice;
    private int productQuantity;
    private int stockQuantity;

    private String productStatus;
    private String productType;
    private String productUnit;

    private String productImage;
    private int productDiscount;

    public ProductModalForeSale() {
    }

    public ProductModalForeSale(String UniquePid, boolean isPublished,
                                List<String> optionQty,
                                String productCategory,
                                String productId,
                                String productDescription,
                                String productName,
                                int productPrice,
                                int productQuantity,
                                int stockQuantity,
                                String productStatus,
                                String productType,
                                String productUnit,
                                String productImage,
                                int productDiscount) {
        this.UniquePid = UniquePid;
        this.isPublished = isPublished;
        this.optionQty = optionQty;
        this.productCategory = productCategory;
        this.productId = productId;
        this.productDescription = productDescription;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
        this.stockQuantity = stockQuantity;
        this.productStatus = productStatus;
        this.productType = productType;
        this.productUnit = productUnit;
        this.productImage = productImage;
        this.productDiscount = productDiscount;
    }

    protected ProductModalForeSale(Parcel in) {
        UniquePid = in.readString();
        isPublished = in.readByte() != 0;
        optionQty = in.createStringArrayList();
        productCategory = in.readString();
        productId = in.readString();
        productDescription = in.readString();
        productName = in.readString();
        productPrice = in.readInt();
        productQuantity = in.readInt();
        stockQuantity = in.readInt();
        productStatus = in.readString();
        productType = in.readString();
        productUnit = in.readString();
        productImage = in.readString();
        productDiscount = in.readInt();
    }

    public static final Creator<ProductModalForeSale> CREATOR = new Creator<ProductModalForeSale>() {
        @Override
        public ProductModalForeSale createFromParcel(Parcel in) {
            return new ProductModalForeSale(in);
        }

        @Override
        public ProductModalForeSale[] newArray(int size) {
            return new ProductModalForeSale[size];
        }
    };

    public String getUniquePid() {
        return UniquePid;
    }

    public void setUniquePid(String UniquePid) {
        this.UniquePid = UniquePid;
    }

    public boolean getisPublished() {
        return isPublished;
    }

    public void setisPublished(boolean isPublished) {
        this.isPublished = isPublished;
    }

    public List<String> getOptionQty() {
        return optionQty;
    }

    public void setOptionQty(List<String> optionQty) {
        this.optionQty = optionQty;
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

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
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

    public int getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(int productDiscount) {
        this.productDiscount = productDiscount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(UniquePid);
        dest.writeByte((byte) (isPublished ? 1 : 0));
        dest.writeStringList(optionQty);
        dest.writeString(productCategory);
        dest.writeString(productId);
        dest.writeString(productDescription);
        dest.writeString(productName);
        dest.writeInt(productPrice);
        dest.writeInt(productQuantity);
        dest.writeInt(stockQuantity);
        dest.writeString(productStatus);
        dest.writeString(productType);
        dest.writeString(productUnit);
        dest.writeString(productImage);
        dest.writeInt(productDiscount);
    }
}
