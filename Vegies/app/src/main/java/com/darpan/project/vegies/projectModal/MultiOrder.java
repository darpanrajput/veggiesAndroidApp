package com.darpan.project.vegies.projectModal;

import java.util.List;

public class MultiOrder {
    List<Integer> eachItemQtyList;
    List<Integer>echItemPriceList;

    public MultiOrder(List<Integer> eachItemQtyList, List<Integer> echItemPriceList) {
        this.eachItemQtyList = eachItemQtyList;
        this.echItemPriceList = echItemPriceList;
    }

    public MultiOrder() {
    }

    public List<Integer> getEachItemQtyList() {
        return eachItemQtyList;
    }

    public void setEachItemQtyList(List<Integer> eachItemQtyList) {
        this.eachItemQtyList = eachItemQtyList;
    }

    public List<Integer> getEchItemPriceList() {
        return echItemPriceList;
    }

    public void setEchItemPriceList(List<Integer> echItemPriceList) {
        this.echItemPriceList = echItemPriceList;
    }
}
