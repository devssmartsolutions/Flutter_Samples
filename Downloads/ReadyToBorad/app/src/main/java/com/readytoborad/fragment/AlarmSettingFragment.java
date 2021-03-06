package com.readytoborad.fragment;


import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.readytoborad.R;
import com.readytoborad.adapter.ChildSettingRecyclerAdapter;
import com.readytoborad.database.ChildData;
import com.readytoborad.database.respository.ChildDataRepository;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.AndroidSupportInjection;



public class AlarmSettingFragment extends BaseFragment implements ChildSettingRecyclerAdapter.OnEditClick {
    @Inject
    ChildDataRepository childDataRepository;

    public AlarmSettingFragment() {
    }

    @BindView(R.id.alarm_setting_list)
    RecyclerView alarmSettingRecyclerView;
    @BindView(R.id.title_toolbar)
    TextView titleTextView;
    @BindView(R.id.subtitle)
    TextView subTitleTextView;
    @BindView(R.id.cleartextview)
    TextView clearTextView;
    @BindView(R.id.backbutton)
    ImageView backImageView;
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
        setToolbar();
        new GetChildData().execute();

    }

    private void setToolbar() {
        titleTextView.setText(getResources().getString(R.string.alarm));
        subTitleTextView.setText(getResources().getString(R.string.settings));
        subTitleTextView.setPaintFlags(subTitleTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        subTitleTextView.setVisibility(View.VISIBLE);
        backImageView.setVisibility(View.VISIBLE);
        backImageView.setColorFilter(ContextCompat.getColor(getActivity(), android.R.color.white),
                PorterDuff.Mode.MULTIPLY);
    }

    private void setAdapter() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        alarmSettingRecyclerView.setLayoutManager(mLayoutManager);
        alarmSettingRecyclerView.addItemDecoration(new DividerItemDecoration(alarmSettingRecyclerView.getContext(), LinearLayoutManager.VERTICAL));
        alarmSettingRecyclerView.setAdapter(getAdapter());
    }

    private ChildSettingRecyclerAdapter getAdapter() {
        return new ChildSettingRecyclerAdapter(getActivity(), childDataArrayList, 2, this);
    }

    @Override
    public void editPickupPoint(int position) {

    }

    @OnClick({R.id.backbutton, R.id.subtitle})
    public void backClick(View view) {
        switch (view.getId()) {
            case R.id.backbutton:
            case R.id.subtitle:
                getActivity().onBackPressed();
                break;
        }

    }
}
