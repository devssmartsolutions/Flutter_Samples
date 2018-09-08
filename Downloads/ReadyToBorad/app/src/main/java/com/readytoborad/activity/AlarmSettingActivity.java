package com.readytoborad.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.readytoborad.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmSettingActivity extends BaseActivity {
    @BindView(R.id.title_toolbar)
    TextView titleTextView;
    @BindView(R.id.subtitle)
    TextView subTitleTextView;
    @BindView(R.id.cleartextview)
    TextView clearTextView;
    @BindView(R.id.backbutton)
    ImageView backImageView;
    @BindView(R.id.alarm_setting_list)
    RecyclerView alarmSettingRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_setting);
        ButterKnife.bind(this);
        setToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setToolbar() {

    }
}
