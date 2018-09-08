package com.readytoborad.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.readytoborad.R;
import com.readytoborad.database.ChildData;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChildSettingRecyclerAdapter extends RecyclerView.Adapter<ChildSettingRecyclerAdapter.ViewHolder> {
    private Context context;
    private OnEditClick onEditClick;
    List<ChildData> childDataList;
    private int settingType;

    public ChildSettingRecyclerAdapter(Context context, List<ChildData> childDataList, int settingType, OnEditClick onEditClick) {
        this.context = context;
        this.childDataList = childDataList;
        this.onEditClick = onEditClick;
        this.settingType = settingType;

    }

    @Override
    public ChildSettingRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pickup_point_item, parent, false);
        return new ChildSettingRecyclerAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ChildSettingRecyclerAdapter.ViewHolder holder, final int position) {
        ChildData childData = childDataList.get(position);
        String settingString = "";
        if (settingType == 1) {
            holder.pickupLocation.setVisibility(View.VISIBLE);
            holder.pickupLocation.setText(childData.getAddress());
            settingString = "'s " + context.getResources().getString(R.string.pickup_points);
            holder.editTextView.setPaintFlags(holder.editTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        } else {
            settingString = "'s " + context.getResources().getString(R.string.alarm);
            holder.editTextView.setText(childData.getAlarmTime());
            holder.pickupLocation.setVisibility(View.GONE);
        }
        holder.childName.setText(childData.getChildName() + settingString);
        holder.editTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEditClick.editPickupPoint(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return childDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.child_name)
        TextView childName;
        @BindView(R.id.pickup_location)
        TextView pickupLocation;
        @BindView(R.id.edit_textview)
        TextView editTextView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);


        }
    }

    public interface OnEditClick {
        void editPickupPoint(int position);
    }
}