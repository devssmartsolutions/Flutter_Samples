package com.readytoborad.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.readytoborad.R;
import com.readytoborad.model.ChildLocation;

import java.util.List;


/**
 * Created by harendrasinghbisht on 26/01/17.
 */

public class ChidlLocationRecycleAdapter extends RecyclerView.Adapter<ChidlLocationRecycleAdapter.MyViewHolder> {

    private List<ChildLocation> childLocationList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView address;

        public MyViewHolder(View view) {
            super(view);
            address = (TextView) view.findViewById(R.id.text_address);

        }
    }


    public ChidlLocationRecycleAdapter(List<ChildLocation> addressModelList) {
        this.childLocationList = addressModelList;
    }

    @Override
    public ChidlLocationRecycleAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.address_row, parent, false);

        return new ChidlLocationRecycleAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ChidlLocationRecycleAdapter.MyViewHolder holder, int position) {
        ChildLocation childLocation = childLocationList.get(position);
        holder.address.setText(childLocation.getChild_name()+"\n "+"city "+childLocation.getCity()
        +"\n address "+childLocation.getAddress());
        // holder.genre.setText(movie.getGenre());
        //holder.year.setText(movie.getYear());
    }

    @Override
    public int getItemCount() {
        return childLocationList.size();
    }
}