package com.readytoborad.fragment;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.readytoborad.R;
import com.readytoborad.customviews.SeekBarHint;

/**
 * Created by anchal.kumar on 10/25/2017.
 */

public class ParentBusHomeFragment extends Fragment implements View.OnClickListener {

    RelativeLayout mToolbar;
    TextView tv_logout;
    TextView toolbar_title;
    ImageView img_refresh,img_map;
    Context mContext;
    Resources mResources;
    ImageView iv;
    SeekBarHint seekBarHint;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_parent_bus_home, container, false);

        init();
        mToolbar = (RelativeLayout) rootView.findViewById(R.id.toolbar);
        tv_logout = (TextView) rootView.findViewById(R.id.tv_logout);
        toolbar_title = (TextView) rootView.findViewById(R.id.toolbar_title);


        iv = (ImageView) rootView.findViewById(R.id.bus);
        mToolbar.setVisibility(View.GONE);
        seekBarHint = (SeekBarHint) rootView.findViewById(R.id.seek_bar);
        img_refresh = (ImageView) rootView.findViewById(R.id.img_refresh);
        img_map = (ImageView) rootView.findViewById(R.id.iv_map);

        seekBarHint.getProgressDrawable().setColorFilter(Color.parseColor("#00ad80"), PorterDuff.Mode.SRC_IN);
        seekBarHint.getThumb().setColorFilter(Color.parseColor("#00ad80"), PorterDuff.Mode.SRC_IN);
        tv_logout.setVisibility(View.GONE);
        toolbar_title.setText(mResources.getString(R.string.app_name));
        mToolbar.setBackgroundColor(mResources.getColor(R.color.login));
        tv_logout.setTextColor(Color.WHITE);
        img_refresh.setVisibility(View.GONE);


        img_map.setOnClickListener(this);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int dpi = metrics.densityDpi;

        System.out.println("dpi "+dpi);
        if(dpi < 230){
            SeekBarHint.setTextYPositionIndent(1);
        } else if (dpi < 310){
            SeekBarHint.setTextYPositionIndent(1);
        } else if (dpi < 470){
            SeekBarHint.setTextYPositionIndent(1);
        }


        return rootView;
    }
    public void init() {
        mContext = getActivity();
        mResources = getResources();

    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.iv_map:

                break;
        }
    }
}