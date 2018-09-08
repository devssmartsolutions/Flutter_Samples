package com.readytoborad.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.readytoborad.fragment.ParentSetPickUpPointsFragment;
import com.readytoborad.model.ChildLocation;

import java.util.ArrayList;

/**
 * Created by anchal.kumar on 11/1/2017.
 */

public class PickUpPointAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    ArrayList<ChildLocation> childLocList;

    public PickUpPointAdapter(FragmentManager fm, ArrayList<ChildLocation> childLocList) {
        super(fm);
        this.childLocList = childLocList;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Fragment fragmentTab1 = new ParentSetPickUpPointsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("child_name", childLocList.get(0).getChild_name());
                fragmentTab1.setArguments(bundle);
                return fragmentTab1;
            case 1:
                Fragment fragmentTab2 = new ParentSetPickUpPointsFragment();
                bundle = new Bundle();
                bundle.putString("child_name", childLocList.get(1).getChild_name());
                fragmentTab2.setArguments(bundle);
                return fragmentTab2;
            case 2:
                Fragment fragmentTab3 = new ParentSetPickUpPointsFragment();
                bundle = new Bundle();
                bundle.putString("child_name", childLocList.get(2).getChild_name());
                fragmentTab3.setArguments(bundle);
                return fragmentTab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return childLocList.size();
    }
}