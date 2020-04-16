package com.example.nearbyplaces;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class myitem implements ClusterItem {

    private LatLng latLng;
    private String title;
    private String snippet;

    public myitem(double lat, double lng, String title, String snippet) {
        this.latLng = new LatLng(lat, lng);
        this.title = title;
        this.snippet = snippet;
    }

    @Override
    public LatLng getPosition() {
        return latLng;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }
}
