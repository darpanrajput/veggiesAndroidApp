package com.darpan.project.vegies.projectModal;

import android.os.Parcel;
import android.os.Parcelable;

import com.darpan.project.vegies.firebaseModal.ProductModalForeSale;

import java.util.List;


public class OrderModal  implements Parcelable  {
    private List<ProductModalForeSale> productModalForeSales;

    private String orderDate;
    private String orderMode;
    private int totalItemCount;
    private int totalAmount;

    /*this below two are not working so manually transferring the array list*/

    public OrderModal() {
    }

    public OrderModal(List<ProductModalForeSale> productModalForeSales, String orderDate, String orderMode, int totalItemCount, int totalAmount) {
        this.productModalForeSales = productModalForeSales;
        this.orderDate = orderDate;
        this.orderMode = orderMode;
        this.totalItemCount = totalItemCount;
        this.totalAmount = totalAmount;
    }

    protected OrderModal(Parcel in) {
        productModalForeSales = in.createTypedArrayList(ProductModalForeSale.CREATOR);
        orderDate = in.readString();
        orderMode = in.readString();
        totalItemCount = in.readInt();
        totalAmount = in.readInt();
    }

    public static final Creator<OrderModal> CREATOR = new Creator<OrderModal>() {
        @Override
        public OrderModal createFromParcel(Parcel in) {
            return new OrderModal(in);
        }

        @Override
        public OrderModal[] newArray(int size) {
            return new OrderModal[size];
        }
    };

    public List<ProductModalForeSale> getProductModalForeSales() {
        return productModalForeSales;
    }

    public void setProductModalForeSales(List<ProductModalForeSale> productModalForeSales) {
        this.productModalForeSales = productModalForeSales;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderMode() {
        return orderMode;
    }

    public void setOrderMode(String orderMode) {
        this.orderMode = orderMode;
    }

    public int getTotalItemCount() {
        return totalItemCount;
    }

    public void setTotalItemCount(int totalItemCount) {
        this.totalItemCount = totalItemCount;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(productModalForeSales);
        dest.writeString(orderDate);
        dest.writeString(orderMode);
        dest.writeInt(totalItemCount);
        dest.writeInt(totalAmount);
    }
}
