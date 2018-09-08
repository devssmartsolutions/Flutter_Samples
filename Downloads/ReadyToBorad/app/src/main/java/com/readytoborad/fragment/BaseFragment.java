package com.readytoborad.fragment;


import android.content.Context;
import android.support.v4.app.Fragment;

import com.readytoborad.interfaces.OnBackPressListener;
import com.readytoborad.util.BackPressImpl;

import dagger.android.support.AndroidSupportInjection;

public class BaseFragment extends Fragment implements OnBackPressListener {
    private boolean mShowingChild;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AndroidSupportInjection.inject(this);
    }

    @Override
    public boolean onBackPressed() {
        return new BackPressImpl(this).onBackPressed();
    }
}
