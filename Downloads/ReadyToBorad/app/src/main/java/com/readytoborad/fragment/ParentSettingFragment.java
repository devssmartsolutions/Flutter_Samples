package com.readytoborad.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.readytoborad.R;
import com.readytoborad.activity.ParentDashboardActivity;
import com.readytoborad.activity.setting.SettingModel;
import com.readytoborad.adapter.SettingRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;

/**
 * Created by anchal.kumar on 10/25/2017.
 */

public class ParentSettingFragment extends BaseFragment implements SettingRecyclerAdapter.ItemClick {


    Context mContext;
    @BindView(R.id.setting_recyclerview)
    RecyclerView setting_recycler;
    List<SettingModel> settingModelList;
    private boolean isFragmentLoaded = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_parent_setting, container, false);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AndroidSupportInjection.inject(this);
    }


    public ParentSettingFragment() {
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, getView());
        if(isFragmentLoaded)
        ((ParentDashboardActivity) getActivity()).setToolbarInfo(true, getResources().getString(R.string.settings));
        settingModelList = new ArrayList<>();
        settingModelList.add(getModelSetting(getResources().getString(R.string.sound), R.drawable.sound, true, false));
        settingModelList.add(getModelSetting(getResources().getString(R.string.alarm_setting), R.drawable.alarm, false, false));
        settingModelList.add(getModelSetting(getResources().getString(R.string.pickup_point_setting), R.drawable.alarm, false, false));
        settingModelList.add(getModelSetting(getResources().getString(R.string.update_profile), R.drawable.alarm, false, false));
        settingModelList.add(getModelSetting(getResources().getString(R.string.change_pwd), R.drawable.change_password, false, false));
        setRecyclerAdapter();
    }

    private SettingModel getModelSetting(String name, int icon, boolean isSwitch, boolean isToogleOn) {
        SettingModel settingModel = new SettingModel();
        settingModel.setName(name);
        settingModel.setIcon(icon);
        settingModel.setSwitch(isSwitch);
        settingModel.setToggleOn(isToogleOn);
        return settingModel;

    }

    private SettingRecyclerAdapter getAdapter() {
        return new SettingRecyclerAdapter(mContext, settingModelList, this);
    }

    private void setRecyclerAdapter() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        setting_recycler.setLayoutManager(mLayoutManager);
        setting_recycler.addItemDecoration(new DividerItemDecoration(setting_recycler.getContext(), LinearLayoutManager.VERTICAL));
        setting_recycler.setAdapter(getAdapter());
    }


    @Override
    public void onItemClick(int position) {
        Fragment fragment = null;
        switch (position) {
            case 1:
                fragment = new AlarmSettingFragment();
                break;
            case 2:
                fragment = new PickupPointFragment();
                break;
            case 3:
                break;
            case 4:
                fragment = new ChangePasswordFragment();
                break;
            default:
                break;

        }
        if (null != fragment) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.fragment_mainLayout, fragment).commit();

        }

    }

    @Override
    public void onToggle(boolean ischecked) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ViewGroup parent = (ViewGroup) getView().getParent();
        if (parent != null) {
            parent.removeAllViews();
        }
    }


}

