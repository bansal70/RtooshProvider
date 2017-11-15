package com.rtoosh.provider.model.POJO;

/*
 * Created by rishav on 11/10/2017.
 */

public class Portfolio {
    private String id, image;
    private boolean isSelected;
    private boolean isNew;

    public Portfolio(String id, String image, boolean isSelected, boolean isNew) {
        this.id = id;
        this.image = image;
        this.isSelected = isSelected;
        this.isNew = isNew;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
