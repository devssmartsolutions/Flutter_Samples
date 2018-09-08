package com.readytoborad.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.readytoborad.R;
import com.readytoborad.model.BusModel;

import java.util.List;

/**
 * Created by Vicky Garg on 6/15/2017.
 */

public class BusRecycleAdapter extends RecyclerView.Adapter<BusRecycleAdapter.MyViewHolder> {

    private List<BusModel> busModelList;
    int total_items = 0;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView message;
        public RelativeLayout relativeLayout;

        public MyViewHolder(View view) {
            super(view);
            message = (TextView) view.findViewById(R.id.notificationMessage);
            relativeLayout = (RelativeLayout) view.findViewById(R.id.rlt);
        }
    }


    public BusRecycleAdapter(List<BusModel> busModelList) {

        this.busModelList = busModelList;
        total_items = busModelList.size();
    }

    @Override
    public BusRecycleAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_row, parent, false);

        return new BusRecycleAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BusRecycleAdapter.MyViewHolder holder, int position) {
        BusModel busModel = busModelList.get(position);
        holder.message.setText(busModel.getBusNo());
        holder.message.setTag(""+position);
        holder.message.setTextSize(25f);
        holder.message.setGravity(Gravity.LEFT);
        // holder.genre.setText(movie.getGenre());
        //holder.year.setText(movie.getYear());

        if(position == total_items-1)
        {
            holder.relativeLayout.setBackgroundResource(0);
        }else
        {
            holder.relativeLayout.setBackgroundResource(R.drawable.one_side_border);
        }

    }
    @Override
    public int getItemCount() {
        return busModelList.size();
    }
}