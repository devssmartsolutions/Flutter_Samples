package com.readytoborad.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.readytoborad.R;
import com.readytoborad.activity.setting.SettingModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingRecyclerAdapter extends RecyclerView.Adapter<SettingRecyclerAdapter.ViewHolder> {
    private List<SettingModel> settingModelList;
    private Context context;
    private ItemClick itemClick;

    public SettingRecyclerAdapter(Context context, List<SettingModel> settingModelList, ItemClick itemClick) {
        this.context = context;
        this.settingModelList = settingModelList;
        this.itemClick = itemClick;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.settings_item, parent, false);
        return new SettingRecyclerAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        SettingModel settingModel = settingModelList.get(position);
        holder.settingTextView.setText(settingModel.getName());
        holder.settingImageView.setImageResource(settingModel.getIcon());
        if (settingModel.isSwitch()) {
            holder.swtichButton.setVisibility(View.VISIBLE);
            if (settingModel.isToggleOn()) {
                holder.swtichButton.setChecked(true);
            } else {
                holder.swtichButton.setChecked(false);
            }
        } else {
            holder.swtichButton.setVisibility(View.GONE);
        }
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != itemClick) {
                    itemClick.onItemClick(position);
                }
            }
        });
        holder.swtichButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (null != itemClick) {
                    itemClick.onToggle(b);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return settingModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.setting_text)
        TextView settingTextView;
        @BindView(R.id.setting_icon)
        ImageView settingImageView;
        @BindView(R.id.switch_button)
        Switch swtichButton;
        @BindView(R.id.relative_setting)
        RelativeLayout relativeLayout;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);


        }
    }

    public interface ItemClick {
        void onItemClick(int position);

        void onToggle(boolean ischecked);
    }
}
