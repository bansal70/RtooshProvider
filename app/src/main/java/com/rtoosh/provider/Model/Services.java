package com.rtoosh.provider.Model;

/*
 * Created by rishav on 10/9/2017.
 */

public class Services {
    private String name;
    private boolean isSelected;

    public Services(String name, boolean isSelected) {
        this.name = name;
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
