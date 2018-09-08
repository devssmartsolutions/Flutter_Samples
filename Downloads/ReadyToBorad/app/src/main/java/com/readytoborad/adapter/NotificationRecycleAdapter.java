package com.readytoborad.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.readytoborad.R;
import com.readytoborad.model.NotificationModel;
import com.readytoborad.util.Constants;
import com.travijuu.numberpicker.library.NumberPicker;

import java.util.List;


/**
 * Created by harendrasinghbisht on 15/01/17.
 */

public class NotificationRecycleAdapter extends RecyclerView.Adapter<NotificationRecycleAdapter.MyViewHolder> {

    private List<NotificationModel> notificationModelList;
    private int screen_id = -1;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView message, tv_header, tv_desc, tv_edit;
        NumberPicker np;
        public MyViewHolder(View view) {
            super(view);

            if (screen_id == Constants.PICKUP_POINTS_ID) {
                tv_header = (TextView) view.findViewById(R.id.tv_header);
                tv_desc = (TextView) view.findViewById(R.id.tv_desc);
                tv_edit = (TextView) view.findViewById(R.id.tv_edit);
                np = (NumberPicker) view.findViewById(R.id.number_picker);

            } else if (screen_id == Constants.ALARM_ID) {
                tv_header = (TextView) view.findViewById(R.id.tv_header);
                tv_desc = (TextView) view.findViewById(R.id.tv_desc);
                tv_edit = (TextView) view.findViewById(R.id.tv_edit);
                np = (NumberPicker) view.findViewById(R.id.number_picker);
            } else
                message = (TextView) view.findViewById(R.id.notificationMessage);

        }
    }


    public NotificationRecycleAdapter(List<NotificationModel> notificationModelList, int screen_id) {
        this.notificationModelList = notificationModelList;
        this.screen_id = screen_id;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if (screen_id == Constants.PARENT_NOTIFICATION_ID) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.parent_notification_row, parent, false);
        } else if (screen_id == Constants.PICKUP_POINTS_ID) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.pickup_points_row, parent, false);
        } else if (screen_id == Constants.ALARM_ID) {

            // for setting alaram row
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.pickup_points_row, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.notification_row, parent, false);
        }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NotificationModel notificationModel = notificationModelList.get(position);

        if (screen_id == Constants.PICKUP_POINTS_ID) {

            holder.tv_header.setText(notificationModel.getMessage());
            holder.tv_desc.setText(notificationModel.getDesc());
            holder.np.setVisibility(View.GONE);

        } else if (screen_id == Constants.ALARM_ID) {
            holder.tv_header.setText(notificationModel.getMessage());
            holder.tv_desc.setVisibility(View.GONE);
            holder.tv_desc.setHeight(20);
            holder.tv_edit.setVisibility(View.GONE);
            holder.np.setMax(20);
            holder.np.setMin(0);
            holder.np.setUnit(1);
            holder.np.setValue(5);
            ((Button)holder.np.findViewById(R.id.increment)).setTextColor(Color.BLACK);
            ((Button)holder.np.findViewById(R.id.decrement)).setTextColor(Color.BLACK);
            ((TextView)holder.np.findViewById(R.id.display)).setMaxWidth(30);
            RelativeLayout.LayoutParams layoutParams =
                    (RelativeLayout.LayoutParams)holder.tv_header.getLayoutParams();
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            holder.tv_header.setLayoutParams(layoutParams);

        } else {
            holder.message.setText(notificationModel.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return notificationModelList.size();
    }
}