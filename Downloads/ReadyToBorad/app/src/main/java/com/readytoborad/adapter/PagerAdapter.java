package com.readytoborad.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.readytoborad.fragment.ParentHomeFragment;
import com.readytoborad.fragment.ParentNotifiyFragment;
import com.readytoborad.fragment.ParentSettingFragment;

/**
 * Created by anchal.kumar on 11/1/2017.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Fragment fragmentTab1 = new ParentHomeFragment();
                return fragmentTab1;
            case 1:
                Fragment fragmentTab2 = new ParentSettingFragment();
                return fragmentTab2;
            case 2:
                Fragment fragmentTab3 = new ParentNotifiyFragment();
                return fragmentTab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}