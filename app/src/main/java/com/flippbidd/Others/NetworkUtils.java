package com.flippbidd.Others;

import android.content.Context;
import android.net.ConnectivityManager;

public class NetworkUtils {

    /**
     * Checks if the Internet connection is available.
     *
     * @return Returns true if the Internet connection is available. False otherwise.
     * *
     */
    public static boolean isInternetAvailable(Context ctx) {
        if (ctx == null) {
            return true;
        }
        ConnectivityManager mConMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isInternetAvailable = mConMgr.getActiveNetworkInfo() != null
                && mConMgr.getActiveNetworkInfo().isAvailable()
                && mConMgr.getActiveNetworkInfo().isConnected();
       /* if (!isInternetAvailable)
            Toast.makeText(ctx, ctx.getString(R.string.default_no_internet_connection_message), Toast.LENGTH_SHORT).show();*/
        return isInternetAvailable;
    }
}