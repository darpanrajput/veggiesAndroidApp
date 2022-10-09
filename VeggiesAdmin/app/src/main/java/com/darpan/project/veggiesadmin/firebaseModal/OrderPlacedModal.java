package com.darpan.project.veggiesadmin.firebaseModal;

import com.google.firebase.firestore.Exclude;

public class OrderPlacedModal {
    /*all are strings*/
    @Exclude
    private String snapId;
    private String UniquePid;
    private String customerAddress;
    private String customerId;
    private String customerName;
    private String dateOfOrder;
    private String deliverTimeSlot;
    private String deliveryCharge;
    private String modeOfPayment;
    private String orderId;
    private String orderImage;
    private String orderName;
    private String orderQuantity;
    private String orderStatus;
    private String orderTiming;
    private String totalPrice;



    public OrderPlacedModal() {
    }

    public OrderPlacedModal(
            String UniquePid,
            String customerAddress,
                            String customerId,
                            String customerName,
                            String dateOfOrder,
                            String deliverTimeSlot,
                            String deliveryCharge,
                            String modeOfPayment,
                            String orderId,
                            String orderName,
                            String orderQuantity,
                            String orderStatus,
                            String orderTiming,
                            String totalPrice) {
        this.UniquePid=UniquePid;
        this.customerAddress = customerAddress;
        this.customerId = customerId;
        this.customerName = customerName;
        this.dateOfOrder = dateOfOrder;
        this.deliverTimeSlot = deliverTimeSlot;
        this.deliveryCharge = deliveryCharge;
        this.modeOfPayment = modeOfPayment;
        this.orderId = orderId;
        this.orderName = orderName;
        this.orderQuantity = orderQuantity;
        this.orderStatus = orderStatus;
        this.orderTiming = orderTiming;
        this.totalPrice = totalPrice;
    }

    public String getUniquePid() {
        return UniquePid;
    }

    public void setUniquePid(String uniquePid) {
        UniquePid = uniquePid;
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

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderImage() {
        return orderImage;
    }

    public void setOrderImage(String orderImage) {
        this.orderImage = orderImage;
    }

    public String getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(String orderQuantity) {
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

    public String getSnapId() {
        return snapId;
    }

    public void setSnapId(String snapId) {
        this.snapId = snapId;
    }
}
