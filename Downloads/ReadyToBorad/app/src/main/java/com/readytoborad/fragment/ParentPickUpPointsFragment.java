package com.readytoborad.fragment;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.readytoborad.R;
import com.readytoborad.adapter.NotificationRecycleAdapter;
import com.readytoborad.model.NotificationModel;
import com.readytoborad.util.AppUtils;
import com.readytoborad.util.Constants;
import com.readytoborad.util.DividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by anchal.kumar on 10/26/2017.
 */

public class ParentPickUpPointsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {


    Context mContext;
    Resources mResources;
    RecyclerView rv_pick_up_points;
    RelativeLayout mToolbar;
    LinearLayout back_lyt;
    TextView tv_logout,toolbar_title;
    ArrayList<NotificationModel> notificationModelList;
    NotificationRecycleAdapter notificationRecycleAdapter;
    ImageView img_refresh;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_parent_pickup_points, container, false);

        rv_pick_up_points = (RecyclerView) rootView.findViewById(R.id.rv_pickup_points);

        init();
        prepareMovieData();

        notificationRecycleAdapter = new NotificationRecycleAdapter(notificationModelList, Constants.PICKUP_POINTS_ID);

        mToolbar = (RelativeLayout) rootView.findViewById(R.id.toolbar);
        rv_pick_up_points = (RecyclerView) rootView.findViewById(R.id.rv_pickup_points);
        tv_logout = (TextView) rootView.findViewById(R.id.tv_logout);
        toolbar_title = (TextView) rootView.findViewById(R.id.toolbar_title);
        img_refresh = (ImageView) rootView.findViewById(R.id.img_refresh);
        back_lyt = (LinearLayout) rootView.findViewById(R.id.back_lyt);


        toolbar_title.setText(mResources.getString(R.string.pickup_points));
        mToolbar.setBackgroundColor(mResources.getColor(R.color.login));
        tv_logout.setTextColor(Color.WHITE);
        tv_logout.setVisibility(View.GONE);
        img_refresh.setVisibility(View.GONE);
        mToolbar.setVisibility(View.GONE);

        back_lyt.setOnClickListener(this);
        setNotificationAdapter();

        return rootView;
    }

    public void init() {
        mContext = getActivity();
        mResources = getResources();
        notificationModelList = new ArrayList<NotificationModel>();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if(isChecked)
        {
            AppUtils.showToast(mContext,"Hello true" );
        }else
        {
            AppUtils.showToast(mContext,"Hello false");
        }
    }
    private void setNotificationAdapter() {
        // notification_list.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        rv_pick_up_points.setLayoutManager(mLayoutManager);
        rv_pick_up_points.addItemDecoration(new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL));
        rv_pick_up_points.setItemAnimator(new DefaultItemAnimator());
        rv_pick_up_points.setAdapter(notificationRecycleAdapter);
        notificationRecycleAdapter.notifyDataSetChanged();

    }
    private void prepareMovieData() {
        for (int i = 0; i < 10; i++) {
            NotificationModel notificationModel = new NotificationModel();
            notificationModel.setMessage("Notification Message " + i);
            notificationModel.setDesc(" Mohali sector "+i);
            notificationModelList.add(notificationModel);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.back_lyt:

                break;
        }
    }
}


