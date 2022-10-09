package com.darpan.project.vegies.firebaseModal;

public class AreaModal {
private String areaName;
private String deliveryCharge;
private boolean publishStatus;

    public AreaModal() {
    }

    public AreaModal(String areaName, String deliveryCharge, boolean publishStatus) {
        this.areaName = areaName;
        this.deliveryCharge = deliveryCharge;
        this.publishStatus = publishStatus;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(String deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public boolean isPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(boolean publishStatus) {
        this.publishStatus = publishStatus;
    }
}
