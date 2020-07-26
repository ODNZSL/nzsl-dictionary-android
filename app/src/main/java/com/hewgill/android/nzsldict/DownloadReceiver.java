package com.hewgill.android.nzsldict;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class DownloadReceiver {
    private DownloadCallback callback;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(action)) {
                Intent startIntent = new Intent(context, FavouritesActivity.class);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                context.startActivity(startIntent);
                return;
            }

            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                if (callback != null) callback.onDownloadCompleted();
            }
        }
    };



    public void registerContext(Context context, DownloadCallback callback) {
        this.callback = callback;
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        context.registerReceiver(receiver, filter);
    }

    public void unregisterContext(Context context) {
        callback = null;
        context.unregisterReceiver(receiver);
    }

    public interface DownloadCallback {
        void onDownloadCompleted();
    }
}
