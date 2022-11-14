package com.flippbidd.Model;

/**
 * Created by stratecore on 17/11/16.
 */
public class ModelDrawerLeft {

    private String title, titlecolor, titlechangecolor, icon, iconchange;
    private boolean selected;

    public ModelDrawerLeft(String title, String titlecolor, String titlechangecolor, String icon, String iconchange) {
        this.title = title;
        this.titlecolor = titlecolor;
        this.titlechangecolor = titlechangecolor;
        this.icon = icon;
        this.iconchange = iconchange;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitlecolor() {
        return titlecolor;
    }

    public void setTitlecolor(String titlecolor) {
        this.titlecolor = titlecolor;
    }

    public String getTitlechangecolor() {
        return titlechangecolor;
    }

    public void setTitlechangecolor(String titlechangecolor) {
        this.titlechangecolor = titlechangecolor;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIconchange() {
        return iconchange;
    }

    public void setIconchange(String iconchange) {
        this.iconchange = iconchange;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
