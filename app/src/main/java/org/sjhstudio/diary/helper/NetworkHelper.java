package org.sjhstudio.diary.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkHelper {
    public static final int TYPE_MOBILE = 1;
    public static final int TYPE_WIFI = 2;
    public static final int TYPE_NOT_CONNECTED = 3;

    public static int getNetworkState(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo != null) {
            int type = networkInfo.getType();

            if(type == ConnectivityManager.TYPE_MOBILE) {
                return TYPE_MOBILE;
            } else if(type == ConnectivityManager.TYPE_WIFI) {
                return TYPE_WIFI;
            }
        }

        return TYPE_NOT_CONNECTED;
    }
}
