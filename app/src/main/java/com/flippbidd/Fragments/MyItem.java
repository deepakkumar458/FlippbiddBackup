package com.flippbidd.Fragments;

import com.flippbidd.Model.Response.CommonList.CommonData;
import com.flippbidd.Model.Response.DealData;
import com.flippbidd.Model.Response.RecentlySearchResponse;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyItem implements ClusterItem {
    private final LatLng position;
    private final String title;
    private final String snippet;
    private final String tagView;
    private final String viewType;
    private final String viewId;
    private final BitmapDescriptor bitmapDescriptorFactory;
    private final CommonData propertyData;
    private final DealData dealData;

    public MyItem(LatLng lng, String title, String snippet, String tag,String viewtype,String viewID, BitmapDescriptor bitmap,CommonData propertyData,DealData dealData) {
        this.position = lng;
        this.title = title;
        this.snippet = snippet;
        this.tagView = tag;
        this.viewType = viewtype;
        this.viewId = viewID;
        this.bitmapDescriptorFactory = bitmap;
        this.propertyData = propertyData;
        this.dealData = dealData;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public String getTagView() {
        return tagView;
    }

    public BitmapDescriptor getBitmapDescriptorFactory() {
        return bitmapDescriptorFactory;
    }

    public String getViewType() {
        return viewType;
    }

    public String getViewId() {
        return viewId;
    }

    public CommonData getPropertyData() {
        return propertyData;
    }

    public DealData getDealData() {
        return dealData;
    }
}
