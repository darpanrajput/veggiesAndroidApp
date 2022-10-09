package com.darpan.project.vegies.firebaseModal;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class OrderPlacedModal implements Parcelable{
    /*all are strings*/
    private List<String> UniquePid;
    private List<String> optionQty;
    private String customerAddress;
    private String customerId;
    private String customerName;
    private String dateOfOrder;
    private String deliverTimeSlot;
    private String deliveryCharge;
    private String modeOfPayment;
    private String orderId;
    private List<String> orderImage;
    private List<String> orderName;
    private List<String> orderQuantity;
    private String orderStatus;
    private String orderTiming;
    private String totalPrice;
    private List<String>orderDescription;
    private String totalItem;


    public OrderPlacedModal() {
    }

    public OrderPlacedModal(List<String> UniquePid,
                            List<String> optionQty,
                            String customerAddress,
                            String customerId,
                            String customerName,
                            String dateOfOrder,
                            String deliverTimeSlot,
                            String deliveryCharge,
                            String modeOfPayment,
                            String orderId,
                            List<String> orderImage, List<String> orderName,
                            List<String> orderQuantity, String orderStatus,
                            String orderTiming, String totalPrice,
                            List<String> orderDescription, String totalItem) {
        this.UniquePid=UniquePid;
        this.optionQty = optionQty;
        this.customerAddress = customerAddress;
        this.customerId = customerId;
        this.customerName = customerName;
        this.dateOfOrder = dateOfOrder;
        this.deliverTimeSlot = deliverTimeSlot;
        this.deliveryCharge = deliveryCharge;
        this.modeOfPayment = modeOfPayment;
        this.orderId = orderId;
        this.orderImage = orderImage;
        this.orderName = orderName;
        this.orderQuantity = orderQuantity;
        this.orderStatus = orderStatus;
        this.orderTiming = orderTiming;
        this.totalPrice = totalPrice;
        this.orderDescription = orderDescription;
        this.totalItem = totalItem;
    }

    protected OrderPlacedModal(Parcel in) {
        UniquePid = in.createStringArrayList();
        optionQty = in.createStringArrayList();
        customerAddress = in.readString();
        customerId = in.readString();
        customerName = in.readString();
        dateOfOrder = in.readString();
        deliverTimeSlot = in.readString();
        deliveryCharge = in.readString();
        modeOfPayment = in.readString();
        orderId = in.readString();
        orderImage = in.createStringArrayList();
        orderName = in.createStringArrayList();
        orderQuantity = in.createStringArrayList();
        orderStatus = in.readString();
        orderTiming = in.readString();
        totalPrice = in.readString();
        orderDescription = in.createStringArrayList();
        totalItem = in.readString();
    }

    public static final Creator<OrderPlacedModal> CREATOR = new Creator<OrderPlacedModal>() {
        @Override
        public OrderPlacedModal createFromParcel(Parcel in) {
            return new OrderPlacedModal(in);
        }

        @Override
        public OrderPlacedModal[] newArray(int size) {
            return new OrderPlacedModal[size];
        }
    };

    public List<String> getUniquePid() {
        return UniquePid;
    }

    public void setUniquePid(List<String> UniquePid) {
        this.UniquePid = UniquePid;
    }

    public List<String> getOptionQty() {
        return optionQty;
    }

    public void setOptionQty(List<String> optionQty) {
        this.optionQty = optionQty;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDateOfOrder() {
        return dateOfOrder;
    }

    public void setDateOfOrder(String dateOfOrder) {
        this.dateOfOrder = dateOfOrder;
    }

    public String getDeliverTimeSlot() {
        return deliverTimeSlot;
    }

    public void setDeliverTimeSlot(String deliverTimeSlot) {
        this.deliverTimeSlot = deliverTimeSlot;
    }

    public String getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(String deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public String getModeOfPayment() {
        return modeOfPayment;
    }

    public void setModeOfPayment(String modeOfPayment) {
        this.modeOfPayment = modeOfPayment;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public List<String> getOrderImage() {
        return orderImage;
    }

    public void setOrderImage(List<String> orderImage) {
        this.orderImage = orderImage;
    }

    public List<String> getOrderName() {
        return orderName;
    }

    public void setOrderName(List<String> orderName) {
        this.orderName = orderName;
    }

    public List<String> getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(List<String> orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderTiming() {
        return orderTiming;
    }

    public void setOrderTiming(String orderTiming) {
        this.orderTiming = orderTiming;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<String> getOrderDescription() {
        return orderDescription;
    }

    public void setOrderDescription(List<String> orderDescription) {
        this.orderDescription = orderDescription;
    }

    public String getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(String totalItem) {
        this.totalItem = totalItem;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(UniquePid);
        dest.writeStringList(optionQty);
        dest.writeString(customerAddress);
        dest.writeString(customerId);
        dest.writeString(customerName);
        dest.writeString(dateOfOrder);
        dest.writeString(deliverTimeSlot);
        dest.writeString(deliveryCharge);
        dest.writeString(modeOfPayment);
        dest.writeString(orderId);
        dest.writeStringList(orderImage);
        dest.writeStringList(orderName);
        dest.writeStringList(orderQuantity);
        dest.writeString(orderStatus);
        dest.writeString(orderTiming);
        dest.writeString(totalPrice);
        dest.writeStringList(orderDescription);
        dest.writeString(totalItem);
    }
}
