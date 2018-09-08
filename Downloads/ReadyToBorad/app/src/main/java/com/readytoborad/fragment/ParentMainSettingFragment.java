package com.readytoborad.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.readytoborad.R;

/**
 * Created by anchal.kumar on 11/1/2017.
 */

public class ParentMainSettingFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {


    Context mContext;
    Resources mResources;
    Switch aSwitch;
    RelativeLayout mToolbar;
    TextView tv_logout, toolbar_title;
    ImageView img_refresh;

    FragmentManager mFragManager = null;
    FragmentTransaction mFragTransaction = null;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_main_setting_layout, container, false);

        init();

        Fragment fragment = new ParentSettingFragment();
        displayFragment(R.id.fragment_container, fragment, mResources.getString(R.string.tag_parent_pass_change_screen), mResources.getString(R.string.fragment_actions_add));
        return rootView;
    }

    public void init() {
        mContext = getActivity();
        mResources = getResources();

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    private void displayFragment(int containerViewId, Fragment fragment, String tag, String fragmentActions) {

        /* ** Instantiate the Fragment Manager ** */
        mFragManager = getChildFragmentManager();

        mFragTransaction = mFragManager.beginTransaction();

        /* ** ** */
        //PosRegister fragRegister = PosRegister.newInstance(null, null);

        /* ** ** */
        //mFragTransaction.add(R.id.fLay_pos_base_right, fragRegister, getResources().getString(R.string.pos_register_screen))
        if (fragmentActions.equalsIgnoreCase(getResources().getString(R.string.fragment_actions_add)))
            mFragTransaction.add(containerViewId, fragment, tag);
        else if (fragmentActions.equalsIgnoreCase(getResources().getString(R.string.fragment_actions_replace)))
            mFragTransaction.replace(containerViewId, fragment, tag);
        else if (fragmentActions.equalsIgnoreCase(getResources().getString(R.string.fragment_actions_replace_with_animation))) {
            mFragTransaction.setCustomAnimations(R.animator.anim_slide_in_up, R.animator.anim_slide_out_up);
            mFragTransaction.replace(containerViewId, fragment, tag);
        }

        /* ** ** */
        /*mFragTransaction.addToBackStack(null);*/
        mFragTransaction.addToBackStack(tag);
        /* ** ** */
        mFragTransaction.commit();
    }
}