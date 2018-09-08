package com.readytoborad.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.readytoborad.R;
import com.readytoborad.adapter.ViewPagerAdapter;
import com.readytoborad.database.ChildData;
import com.readytoborad.database.respository.ChildDataRepository;
import com.readytoborad.interfaces.OnBackPressListener;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;

public class ParentDashBoardFragment extends BaseFragment {
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.pager)
    ViewPager viewPager;
    List<ChildData> childDataArrayList = null;
    @Inject
    ChildDataRepository childDataRepository;
    private ViewPagerAdapter adapter;
    Resources mResources;

    public ParentDashBoardFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_parent_dashboard, container, false);
        mResources=getResources();
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AndroidSupportInjection.inject(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, getView());
        new GetChildData().execute();
    }

    private class GetChildData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            childDataArrayList = childDataRepository.getAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void agentsCount) {
            init();

        }
    }

    private void init() {
      //  mFragManager = getSupportFragmentManager();
        tabLayout.addTab(tabLayout.newTab().setText(mResources.getString(R.string.tab1)).setIcon(R.drawable.home));
        tabLayout.addTab(tabLayout.newTab().setText(mResources.getString(R.string.tab2)).setIcon(R.drawable.settings));
        tabLayout.addTab(tabLayout.newTab().setText(mResources.getString(R.string.tab3)).setIcon(R.drawable.notification));
        /*final PagerAdapter adapter = new PagerAdapter
                (mFragManager, tabLayout.getTabCount());*/
        adapter = new ViewPagerAdapter(getResources(), getChildFragmentManager());
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    /**
     * Retrieve the currently visible Tab Fragment and propagate the onBackPressed callback
     *
     * @return true = if this fragment and/or one of its associates Fragment can handle the backPress
     */
    public boolean onBackPressed() {
        // currently visible tab Fragment
        OnBackPressListener currentFragment = (OnBackPressListener) adapter.getRegisteredFragment(viewPager.getCurrentItem());

        if (currentFragment != null) {
            // lets see if the currentFragment or any of its childFragment can handle onBackPressed
            return currentFragment.onBackPressed();
        }

        // this Fragment couldn't handle the onBackPressed call
        return false;
    }


    public Fragment getCurrentFragment(){
       return adapter.getRegisteredFragment(viewPager.getCurrentItem());
    }
}
