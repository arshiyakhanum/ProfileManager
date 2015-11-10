package com.arshiya.mapsapi;

/**
 * Created by akhanumx on 10/28/2015.
 */
public class SliderListItem {

    public String mTitle;
    public int mIcon;

    public SliderListItem(String title, int icon){
        mTitle = title;
        mIcon = icon;
    }

    public SliderListItem getSliderListItem(){
        return this;
    }
}
