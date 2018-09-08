package com.readytoborad.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.readytoborad.R;
import com.readytoborad.model.BusModel;

import java.util.List;




/**
 * Created by harendrasinghbisht on 26/01/17.
 */

public class AddressRecycleAdapter extends RecyclerView.Adapter<AddressRecycleAdapter.MyViewHolder> {

    private List<BusModel> addressModelList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView address;

        public MyViewHolder(View view) {
            super(view);
            address = (TextView) view.findViewById(R.id.text_address);

        }
    }


    public AddressRecycleAdapter(List<BusModel> addressModelList) {
        this.addressModelList = addressModelList;
    }

    @Override
    public AddressRecycleAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.address_row, parent, false);

        return new AddressRecycleAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AddressRecycleAdapter.MyViewHolder holder, int position) {
        BusModel routeModel = addressModelList.get(position);
        holder.address.setText(routeModel.getBusNo());
        // holder.genre.setText(movie.getGenre());
        //holder.year.setText(movie.getYear());
    }

    @Override
    public int getItemCount() {
        return addressModelList.size();
    }
}