package com.hewgill.android.nzsldict;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkManager {

    protected boolean connected;
    private NetworkCallback callback;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateStatus(context);
        }
    };

    private void updateStatus(Context context) {
        ConnectivityManager conn = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();
        connected = networkInfo != null && networkInfo.isConnected();
        if (callback != null) {
            callback.onConnectionStatusChanged(connected);
        }
    }

    public void registerContext(Context context, NetworkCallback callback) {
        this.callback = callback;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(receiver, filter);
        updateStatus(context);
    }

    public void unregisterContext(Context context) {
        callback = null;
        context.unregisterReceiver(receiver);
    }

    public interface NetworkCallback {

        void onConnectionStatusChanged(boolean connected);
    }
}