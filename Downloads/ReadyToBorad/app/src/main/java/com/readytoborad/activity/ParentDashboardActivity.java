package com.readytoborad.activity;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import com.readytoborad.R;
import com.readytoborad.adapter.ViewPagerAdapter;
import com.readytoborad.fragment.BaseFragment;
import com.readytoborad.fragment.ParentDashBoardFragment;

import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

public class ParentDashboardActivity extends BaseActivity {
    Context mContext;
    Resources mResources;
    private ViewPagerAdapter mAdapter;
    ParentDashBoardFragment parentDashBoardFragment;
    /*@BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.title_toolbar)
    TextView titleTextView;
    @BindView(R.id.subtitle)
    TextView subTitleTextView;
    @BindView(R.id.cleartextview)
    TextView clearTextView;
    @BindView(R.id.backbutton)
    ImageView backImageView;*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_dashboard1);
        AndroidInjection.inject(this);
        ButterKnife.bind(this);
        mContext = this;
        mResources = getResources();
       // setToolbar();
        // new GetChildData().execute();
        initScreen();

    }

    private void initScreen() {
        // Creating the ViewPager container fragment once
        parentDashBoardFragment = new ParentDashBoardFragment();

        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, parentDashBoardFragment)
                .commit();
    }

   /* private void setToolbar() {
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.login));
        setTitle(getResources().getString(R.string.app_name));
        titleTextView.setTextColor(ContextCompat.getColor(this, R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }*/


    // Back Button Pressed
    private void backButton() {
        finish();
    }

    public void replaceChild(BaseFragment oldFrg, int position) {
        // mAdapter.replaceChildFragment(oldFrg, position);
    }

    @Override
    public void onBackPressed() {

        if (!parentDashBoardFragment.onBackPressed()) {
            // container Fragment or its associates couldn't handle the back pressed task
            // delegating the task to super class
            super.onBackPressed();

        } else {
            // carousel handled the back pressed task
            // do not call super
        }
    }

   /* public void setToolbarInfo(boolean isBacKButton, String title) {
        if (isBacKButton) {
            backImageView.setVisibility(View.VISIBLE);
            subTitleTextView.setText(title);
        }
        else{
            backImageView.setVisibility(View.INVISIBLE);
            subTitleTextView.setVisibility(View.INVISIBLE);
        }

    }

    public void setTitle(String title) {
        titleTextView.setText(title);
        titleTextView.invalidate();
    }*/

}
