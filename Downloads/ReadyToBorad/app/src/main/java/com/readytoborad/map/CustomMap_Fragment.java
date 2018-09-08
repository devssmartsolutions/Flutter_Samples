package com.readytoborad.map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by harendrasinghbisht on 11/02/17.
 */

public class CustomMap_Fragment extends SupportMapFragment {

    TouchableWrapper mTouchView;

    public CustomMap_Fragment() {
        super();
    }

    public static CustomMap_Fragment newInstance() {
        return new CustomMap_Fragment();
    }

    @Override
    public View onCreateView(LayoutInflater arg0, ViewGroup arg1, Bundle arg2) {
        View mapView = super.onCreateView(arg0, arg1, arg2);

        Fragment fragment = getParentFragment();
        if (fragment != null && fragment instanceof OnMapReadyListener) {
            ((OnMapReadyListener) fragment).onMapReady();
        }

        mTouchView = new TouchableWrapper(getActivity());
        mTouchView.addView(mapView);


        return mTouchView;
    }

    public static interface OnMapReadyListener {
        void onMapReady();
    }

}