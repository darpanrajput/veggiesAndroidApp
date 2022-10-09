package com.darpan.project.veggiesadmin.activity.order;

import java.util.List;

public class modal {
    private List<String>qty;
    private List<String>names;
    private List<String>units;
    private List<String>ids;

    public modal() {
    }

    public List<String> getQty() {
        return qty;
    }

    public void setQty(List<String> qty) {
        this.qty = qty;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public List<String> getUnits() {
        return units;
    }

    public void setUnits(List<String> units) {
        this.units = units;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    @Override
    public String toString() {
        return "Qty:"+qty.toString()+"\n"+
                "name:"+names.toString()+"\n"+
                "units:"+units.toString()+"\n"+
                "id:"+ids.toString()+"\n";
    }
}
