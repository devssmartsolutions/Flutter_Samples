package com.readytoborad.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.readytoborad.R;
import com.readytoborad.adapter.NotificationRecycleAdapter;
import com.readytoborad.adapter.PickUpPointAdapter;
import com.readytoborad.model.ChildLocation;
import com.readytoborad.model.NotificationModel;

import java.util.ArrayList;

/**
 * Created by Vicky Garg on 12/27/2017.
 */

public class ParentPickUpPointActivity extends AppCompatActivity {


    Context mContext;
    Resources mResources;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private  final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    RelativeLayout mToolbar;
    LinearLayout back_lyt;
    private GoogleMap googleMap;
    TextView tv_logout, toolbar_title;
    ArrayList<NotificationModel> notificationModelList;
    NotificationRecycleAdapter notificationRecycleAdapter;
    ImageView img_refresh;
    AutoCompleteTextView autoCompleteTextView;
    ArrayList<String> arrayList;
    Button btn_save;
    SupportMapFragment mapFragment;
    TextView tvChildName;
    TabLayout tabLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_loc_dashboard);


        mContext = this;
        mResources = getResources();
        if(getIntent() !=null) {
            Intent intent = getIntent();
            ArrayList<ChildLocation> locationArrayList = intent.getParcelableArrayListExtra("childObj");
            for(ChildLocation childLocation: locationArrayList)
                System.out.println(childLocation.getChild_name());

            final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
            final PickUpPointAdapter adapter = new PickUpPointAdapter
                    (getSupportFragmentManager(), locationArrayList);
            viewPager.setAdapter(adapter);
        }




        /*viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
        });*/

    }

}



