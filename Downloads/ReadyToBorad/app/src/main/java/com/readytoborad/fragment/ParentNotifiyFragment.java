package com.readytoborad.fragment;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.readytoborad.R;
import com.readytoborad.activity.ParentDashboardActivity;
import com.readytoborad.adapter.NotificationRecycleAdapter;
import com.readytoborad.model.NotificationModel;
import com.readytoborad.util.Constants;
import com.readytoborad.util.DividerItemDecoration;

import java.util.ArrayList;

import dagger.android.support.AndroidSupportInjection;

/**
 * Created by anchal.kumar on 10/25/2017.
 */

public class ParentNotifiyFragment extends BaseFragment {

    TextView tv_logout, toolbar_title;
    RecyclerView rv_notification;
    Context mContext;
    Resources mResources;
    Toolbar mToolbar;
    Button clear_Button;
    ImageView img_refresh;
    ArrayList<NotificationModel> notificationModelList;
    NotificationRecycleAdapter notificationRecycleAdapter;
    private boolean isFragmentLoaded = false;


    /**
     * Create a new instance of the fragment
     */
    public static ParentNotifiyFragment newInstance(int index) {
        ParentNotifiyFragment fragment = new ParentNotifiyFragment();
        Bundle b = new Bundle();
        b.putInt("index", index);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Update action bar menu items?
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AndroidSupportInjection.inject(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isFragmentLoaded) {
            // Load your data here or do network operations here
            isFragmentLoaded = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.notification_layout, container, false);

        init();
        prepareMovieData();

        notificationRecycleAdapter = new NotificationRecycleAdapter(notificationModelList, Constants.PARENT_NOTIFICATION_ID);
        rv_notification = (RecyclerView) rootView.findViewById(R.id.notification_list);
        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        tv_logout = (TextView) rootView.findViewById(R.id.tv_logout);
        toolbar_title = (TextView) rootView.findViewById(R.id.toolbar_title);
        clear_Button = (Button) rootView.findViewById(R.id.clear_Button);
        img_refresh = (ImageView) rootView.findViewById(R.id.img_refresh);

        mToolbar.setVisibility(View.GONE);
        tv_logout.setText(mResources.getString(R.string.clear_all));
        toolbar_title.setText(mResources.getString(R.string.notification));
        mToolbar.setBackgroundColor(mResources.getColor(R.color.login));
        tv_logout.setTextColor(Color.WHITE);
        clear_Button.setVisibility(View.GONE);
        img_refresh.setVisibility(View.GONE);
        setNotificationAdapter();

        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isFragmentLoaded)
            ((ParentDashboardActivity) getActivity()).setToolbarInfo(true, getResources().getString(R.string.notification));

    }

    public void init() {

        mContext = getActivity();
        mResources = getResources();
        notificationModelList = new ArrayList<NotificationModel>();

    }

    private void prepareMovieData() {
        for (int i = 0; i < 10; i++) {
            NotificationModel notificationModel = new NotificationModel();
            notificationModel.setMessage("Notification Message " + i);
            notificationModelList.add(notificationModel);
        }

    }

    private void setNotificationAdapter() {
        // notification_list.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        rv_notification.setLayoutManager(mLayoutManager);
        rv_notification.addItemDecoration(new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL));
        rv_notification.setItemAnimator(new DefaultItemAnimator());
        rv_notification.setAdapter(notificationRecycleAdapter);
        notificationRecycleAdapter.notifyDataSetChanged();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (Build.VERSION.SDK_INT >= 11) {
            inflater.inflate(R.menu.logout_menu, menu);
            SpannableString s = new SpannableString(getString(R.string.clear_all));
            s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);
            menu.getItem(0).setTitle(s);
        }
    }
}