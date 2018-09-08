package com.readytoborad.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.readytoborad.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


/**
 * Created by Vicky Garg on 1/16/2018.
 */

public class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {

    private ArrayList<String> resultList;
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyANUij-N-zw9NEEJ60T9Nqkfq8lT9VEWSU";
    private static final String LOG_TAG = "GooglePlaces";

    Context mContext;
    public GooglePlacesAutocompleteAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);

        mContext = context ;
    }

    @Override
    public int getCount() {
        return resultList.size();

    }


    @Override
    public String getItem(int index) {
        String str = resultList.get(index);
        return str;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();

                if (constraint != null) {

                    resultList = autocomplete(constraint.toString());

                    filterResults.values = resultList;
                    filterResults.count = resultList.size();

                }
                return filterResults;

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (results != null && results.count > 0) {

                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if(convertView==null){

            convertView = LayoutInflater.from(mContext).inflate(R.layout.google_search_list_item,null);
            holder = new ViewHolder();
            holder.tv_Title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_Id = (TextView) convertView.findViewById(R.id.tv_id);
            String string = resultList.get(position);
            String[] places = string.split("@");
            holder.tv_Title.setText(places[0]);
            holder.tv_Id.setText(places[1]);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
            String string = resultList.get(position);
            String[] places = string.split("@");
            holder.tv_Title.setText(places[0]);
            holder.tv_Id.setText(places[1]);
        }
        return convertView;
    }
    public class ViewHolder {
        public TextView tv_Title,tv_Id;
    }

    public static ArrayList autocomplete(String input) {
        ArrayList resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:in");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            System.out.println("data "+jsonResults.toString() +" data ");
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description")+"@"+predsJsonArray.getJSONObject(i).getString("place_id"));
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

}
