package com.readytoborad.activity;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.readytoborad.R;
import com.readytoborad.adapter.NotificationRecycleAdapter;
import com.readytoborad.model.NotificationModel;
import com.readytoborad.util.Constants;
import com.readytoborad.util.Util;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by harendrasinghbisht on 15/01/17.
 */

public class NotificationActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext;
    private Toolbar mToolbar;
    private TextView mTitle;
    private RelativeLayout notification_layout, alert_layout;
    private RecyclerView notification_list;
    private Button clear_Button;
    private NotificationRecycleAdapter notificationRecycleAdapter;
    private List<NotificationModel> notificationModelList;
    private Resources res;

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.statuBarColor(this, R.color.light, true);
        setContentView(R.layout.notification_layout);
        mContext = this;
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        mTitle = (TextView) findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.notification));
        mTitle.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.common_google_signin_btn_text_light_default)));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        init();
    }

    private void init() {
        notificationModelList = new ArrayList<NotificationModel>();
        prepareMovieData();
        notificationRecycleAdapter = new NotificationRecycleAdapter(notificationModelList, Constants.NOTIFICATION_ID);
        notification_list = (RecyclerView) findViewById(R.id.notification_list);
        notification_layout = (RelativeLayout) findViewById(R.id.notification_layout);
        alert_layout = (RelativeLayout) findViewById(R.id.alert_layout);
        clear_Button = (Button) findViewById(R.id.clear_Button);
        clear_Button.setOnClickListener(this);
        setNotificationAdapter();
        final ImageView image = (ImageView) findViewById(R.id.alert_image);
        final int newColor = ContextCompat.getColor(mContext, R.color.red_btn_bg_color);
        image.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);

    }

    private void setNotificationAdapter() {
        // notification_list.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        notification_list.setLayoutManager(mLayoutManager);
        // notification_list.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        notification_list.setItemAnimator(new DefaultItemAnimator());
        notification_list.setAdapter(notificationRecycleAdapter);
    }

    private void prepareMovieData() {
        for (int i = 0; i < 10; i++) {
            NotificationModel notificationModel = new NotificationModel();
            notificationModel.setMessage("Notification Message " + i);
            notificationModelList.add(notificationModel);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear_Button:
                notificationModelList.clear();
                notificationRecycleAdapter.notifyDataSetChanged();
                alert_layout.setVisibility(View.VISIBLE);
                break;

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
