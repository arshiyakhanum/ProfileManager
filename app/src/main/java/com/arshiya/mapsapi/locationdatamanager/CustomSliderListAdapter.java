package com.arshiya.mapsapi.locationdatamanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.arshiya.mapsapi.R;
import com.arshiya.mapsapi.mapactivities.SliderListItem;
import com.arshiya.mapsapi.common.Fonts;

import java.util.ArrayList;

/**
 * Created by akhanumx on 10/28/2015.
 */
public class CustomSliderListAdapter extends BaseAdapter{

    private ArrayList<SliderListItem> mSliderListItemList;
    private Context mContext;

    public CustomSliderListAdapter(Context context, ArrayList<SliderListItem> sliderListItems){
        mContext = context;
        mSliderListItemList = sliderListItems;
    }
    @Override
    public int getCount() {
        return mSliderListItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return mSliderListItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mSliderListItemList.indexOf(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.slide_bar_item, null);
        }

        SliderListItem sliderListItem = mSliderListItemList.get(position);
        ImageView icon = (ImageView) convertView.findViewById(R.id.slider_item_icon);
        TextView title = (TextView) convertView.findViewById(R.id.slider_item_name);

        title.setTypeface(Fonts.ROBOTOMEDIUM);
        icon.setImageResource(sliderListItem.mIcon);
        title.setText(sliderListItem.mTitle);

        return convertView;

    }
}
