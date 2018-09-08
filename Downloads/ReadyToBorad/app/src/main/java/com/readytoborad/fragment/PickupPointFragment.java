package com.readytoborad.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.readytoborad.R;
import com.readytoborad.activity.SetLocationActivity;
import com.readytoborad.adapter.ChildSettingRecyclerAdapter;
import com.readytoborad.database.ChildData;
import com.readytoborad.database.respository.ChildDataRepository;
import com.readytoborad.util.Constants;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;

import static com.readytoborad.util.Constants.SET_LOCATION_REQUEST;

public class PickupPointFragment extends BaseFragment implements ChildSettingRecyclerAdapter.OnEditClick {
    @Inject
    ChildDataRepository childDataRepository;

    public PickupPointFragment() {
    }

    @BindView(R.id.alarm_setting_list)
    RecyclerView alarmSettingRecyclerView;
    List<ChildData> childDataArrayList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AndroidSupportInjection.inject(this);
    }

    private class GetChildData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            childDataArrayList = childDataRepository.getAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void agentsCount) {
            setAdapter();

        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_alarm_setting, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, getView());
        new GetChildData().execute();

    }

    private void setAdapter() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        alarmSettingRecyclerView.setLayoutManager(mLayoutManager);
        alarmSettingRecyclerView.addItemDecoration(new DividerItemDecoration(alarmSettingRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        alarmSettingRecyclerView.setAdapter(getAdapter());
    }

    private ChildSettingRecyclerAdapter getAdapter() {
        return new ChildSettingRecyclerAdapter(getActivity(), childDataArrayList, 1,this);
    }

    @Override
    public void editPickupPoint(int position) {
        Intent it = new Intent(getActivity(), SetLocationActivity.class);
        it.putExtra(Constants.CHILD_DATA, (Serializable) childDataArrayList.get(position));
        startActivityForResult(it, SET_LOCATION_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==SET_LOCATION_REQUEST&&resultCode==SET_LOCATION_REQUEST){
            new GetChildData().execute();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}