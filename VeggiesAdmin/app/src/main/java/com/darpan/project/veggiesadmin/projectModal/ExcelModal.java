package com.darpan.project.veggiesadmin.projectModal;

public class ExcelModal {
    private String productId;
    private String totalOrderQty;
    private String orderName;
    private String amount;
    private String orderStatus;

    private String orderTime;
    private String orderDate;
    private String category;

    public ExcelModal() {
    }

    public ExcelModal(String productId, String totalOrderQty,
                      String orderName, String amount,
                      String orderStatus, String orderTime,
                      String orderDate,String category) {
        this.productId = productId;
        this.totalOrderQty = totalOrderQty;
        this.orderName = orderName;
        this.amount = amount;
        this.orderStatus = orderStatus;
        this.orderTime = orderTime;
        this.orderDate = orderDate;
        this.category=category;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getTotalOrderQty() {
        return totalOrderQty;
    }

    public void setTotalOrderQty(String totalOrderQty) {
        this.totalOrderQty = totalOrderQty;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
