package com.readytoborad.fragment;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.readytoborad.R;
import com.readytoborad.util.AppUtils;

/**
 * Created by anchal.kumar on 10/26/2017.
 */

public class ParentChangePassowrdFragment extends Fragment implements View.OnClickListener {


    Context mContext;
    Resources mResources;
    Button btnParentSavePass;
    RelativeLayout mToolbar;
    TextView tv_logout, toolbar_title;

    ImageView img_refresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_parent_change_pass, container, false);

        init();

        mToolbar = (RelativeLayout) rootView.findViewById(R.id.toolbar);
        tv_logout = (TextView) rootView.findViewById(R.id.tv_logout);
        toolbar_title = (TextView) rootView.findViewById(R.id.toolbar_title);

        img_refresh = (ImageView) rootView.findViewById(R.id.img_refresh);

        toolbar_title.setText(mResources.getString(R.string.change_pass));
        mToolbar.setBackgroundColor(mResources.getColor(R.color.login));
        tv_logout.setTextColor(Color.WHITE);

        mToolbar.setVisibility(View.GONE);
        img_refresh.setVisibility(View.GONE);
        tv_logout.setVisibility(View.GONE);
        btnParentSavePass = (Button) rootView.findViewById(R.id.btn_parent_save_pass);
        btnParentSavePass.setOnClickListener(this);
        return rootView;
    }

    public void init() {
        mContext = getActivity();
        mResources = getResources();
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_parent_save_pass:
                break;
        }
    }
}


