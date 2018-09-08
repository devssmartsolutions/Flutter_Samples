package com.readytoborad.activity.setting;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.readytoborad.R;
import com.readytoborad.activity.BaseActivity;
import com.readytoborad.activity.ChangePasswordActivity;
import com.readytoborad.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by harendrasinghbisht on 21/01/17.
 */

public class SettingsActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_sound, rl_alarm, rl_change_password;
    @BindView(R.id.setting_recyclerview)
    RecyclerView settingRecyclerView;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Util.statuBarColor(this, R.color.light, true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
         setToolbar();


    }



    private void setToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        TextView mTitle = (TextView) findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.settings));
        mTitle.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.dashcolor)));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_change_password:
                startActivityForResult(new Intent(SettingsActivity.this, ChangePasswordActivity.class), 0);
                break;
        }
    }
}
