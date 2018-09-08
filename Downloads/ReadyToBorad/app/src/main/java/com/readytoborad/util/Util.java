package com.readytoborad.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.readytoborad.R;
import com.readytoborad.activity.LoginActivity;
import com.readytoborad.session.SessionManager;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * Created by harendrasinghbisht on 11/01/17.
 */
public class Util {
    public static void setTaskBarColored(Activity context, int color, boolean change_icon) {
        Rect rect = new Rect();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = context.getWindow();
            w.getDecorView().getWindowVisibleDisplayFrame(rect);
            if (change_icon)
                w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //status bar height
            int statusBarHeight = rect.top;
            View view = new View(context);
            view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.getLayoutParams().height = statusBarHeight;
            ((ViewGroup) w.getDecorView()).addView(view);
            view.setBackgroundColor(ContextCompat.getColor(context, color));
        }
    }

    public static void statuBarColor(Activity context, int color, boolean change_icon) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = context.getWindow();
            if (change_icon)
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(context, color));
        }
    }

    public static void getUserLogout(Activity activity) {
        String user_type = new SessionManager(activity).getKeyUserType();
        new SessionManager(activity).logoutUser();
        Intent it = new Intent(activity, LoginActivity.class);
        it.putExtra("user_type", user_type);
        activity.startActivity(it);
        activity.finish();

    }

    public static String checkNull(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }

    public static void showNetworkAlertDialog(Context mContext) {
        Resources mResources = mContext.getResources();
       /* AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mResources.getString(R.string.alert));
        builder.setMessage(mResources.getString(R.string.internet_alert_msg));
        builder.setPositiveButton(AlertDialog.BUTTON_POSITIVE,null);
        builder.show();*/

        new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Alert")
                .setContentText(mResources.getString(R.string.internet_alert_msg))
                .setConfirmText("OK")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        sDialog.dismiss();
                    }
                })
                .show();


    }

    public static final boolean isInternetOn(Context context) {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {

            // if connected with internet

            // Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {

            // Toast.makeText(this, " Not Connected ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }


}
