package com.darpan.project.veggiesadmin.firebaseModal;

public class AreaBlockModal {
    private String blockName;
    private String blockNumber;

    public AreaBlockModal() {
    }

    public AreaBlockModal(String blockName, String blockNumber) {
        this.blockName = blockName;
        this.blockNumber = blockNumber;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public String getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }
}
