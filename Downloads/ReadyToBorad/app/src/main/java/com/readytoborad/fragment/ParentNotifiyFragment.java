package com.readytoborad.fragment;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.readytoborad.R;
import com.readytoborad.adapter.NotificationRecycleAdapter;
import com.readytoborad.model.NotificationModel;
import com.readytoborad.util.Constants;
import com.readytoborad.util.DividerItemDecoration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;

/**
 * Created by anchal.kumar on 10/25/2017.
 */

public class ParentNotifiyFragment extends BaseFragment {

    TextView tv_logout, toolbar_title;

    Context mContext;
    Resources mResources;
    Button clear_Button;
    ImageView img_refresh;
    ArrayList<NotificationModel> notificationModelList;
    NotificationRecycleAdapter notificationRecycleAdapter;
    private boolean isFragmentLoaded = false;
    @BindView(R.id.title_toolbar)
    TextView titleTextView;
    @BindView(R.id.subtitle)
    TextView subTitleTextView;
    @BindView(R.id.cleartextview)
    TextView clearTextView;
    @BindView(R.id.backbutton)
    ImageView backImageView;
    @BindView(R.id.notification_list)
    RecyclerView notificatinRecylerView;


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.notification_layout, container, false);

        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this,getView());
        titleTextView.setText(getResources().getString(R.string.notification));
        clearTextView.setText(getResources().getString(R.string.clear_all));
        clearTextView.setPaintFlags(clearTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        clearTextView.setVisibility(View.VISIBLE);
        init();


    }

    public void init() {

        mContext = getActivity();
        mResources = getResources();
        notificationModelList = new ArrayList<NotificationModel>();
        prepareMovieData();

    }

    private void prepareMovieData() {
        for (int i = 0; i < 10; i++) {
            NotificationModel notificationModel = new NotificationModel();
            notificationModel.setMessage("Notification Message " + i);
            notificationModelList.add(notificationModel);
        }
        setNotificationAdapter();

    }

    private void setNotificationAdapter() {
        notificationRecycleAdapter = new NotificationRecycleAdapter(notificationModelList, Constants.PARENT_NOTIFICATION_ID);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        notificatinRecylerView.setLayoutManager(mLayoutManager);
        notificatinRecylerView.addItemDecoration(new DividerItemDecoration(notificatinRecylerView.getContext(), LinearLayoutManager.VERTICAL));
        notificatinRecylerView.setAdapter(notificationRecycleAdapter);
        notificationRecycleAdapter.notifyDataSetChanged();

    }

}