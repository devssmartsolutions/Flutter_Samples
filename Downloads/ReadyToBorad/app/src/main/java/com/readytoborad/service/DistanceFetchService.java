package com.readytoborad.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.readytoborad.network.JSONParser;
import com.readytoborad.util.Constants;

import org.json.JSONObject;

import java.util.HashMap;


/**
 * Created by harendrasinghbisht on 29/01/17.
 */

public class DistanceFetchService extends IntentService {
    private static final String TAG = "FetchDistanceIS";
    protected ResultReceiver mReceiver;
    private Context context;
    private int track_data;

    public DistanceFetchService() {
        super(TAG);
        context = this;

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        // Check if receiver was properly registered.
        if (mReceiver == null) {
            Log.wtf(TAG, "No receiver received. There is nowhere to send the results.");
            return;
        }
        LatLng sourceLatLng = intent.getParcelableExtra(Constants.LOCATION_SOURCE_EXTRA);
        LatLng destinationLatLng = intent.getParcelableExtra(Constants.LOCATION_DESTINATION_EXTRA);
        track_data = intent.getIntExtra(Constants.DATA_TRACK, 0);
        JSONParser jsonParser = new JSONParser();
        HashMap<String, String> params = new HashMap<>();
        String Url = Constants.MATRIX_API + "units=kms&" + "origins=" + sourceLatLng.latitude + "," + sourceLatLng.longitude + "&destinations=" + destinationLatLng.latitude + "," + destinationLatLng.longitude + "&key=AIzaSyD6OGSdko-75JksEz_ZPFLqXopDazkSx_w";
        JSONObject jsonObject = jsonParser.makeHttpRequest(Url, "GET", params);
        if (jsonObject != null) {
            deliverResultToReceiver(Constants.SUCCESS_RESULT, jsonObject.toString());
        }

    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        bundle.putInt(Constants.DATA_TRACK, track_data);
        mReceiver.send(resultCode, bundle);
    }
}
