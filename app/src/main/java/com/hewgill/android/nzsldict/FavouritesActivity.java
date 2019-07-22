package com.hewgill.android.nzsldict;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Network;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class FavouritesActivity extends BaseActivity implements DictionaryAdapter.Presenter, NetworkManager.NetworkCallback {
    private ListView mListView;
    private DictionaryAdapter adapter;
    private FavouritesRepository repo;
    private DownloadReceiver mDownloadReceiver;
    private DownloadManager mDownloadManager;
    private NetworkManager mNetworkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Favourites");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_favourites);
        mListView = (ListView) findViewById(R.id.favourites);
        repo = new FavouritesRepository(this);
        adapter = new DictionaryAdapter(R.layout.list_item, this, repo.all());
        getListView().setAdapter(adapter);
        mListView.setEmptyView(findViewById(R.id.empty_favourites));
        mNetworkManager = new NetworkManager();
        mDownloadManager = (DownloadManager) this.getSystemService(DOWNLOAD_SERVICE);

        findViewById(R.id.finish_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNetworkManager.registerContext(getContext(), this);
        registerDownloadReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterDownloadReceiver();
        mNetworkManager.unregisterContext(getContext());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favourites_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favourites_clear:
                clearFavourites();
                break;
            case R.id.action_favourites_download:
                enqueueFavouritesDownload();
                break;
        }

        invalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }

    private void clearFavourites() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to clear your favourites?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        for (DictItem item : repo.all()) {
                            removeRequested(item);
                        }
                        ((DictionaryAdapter) getListView().getAdapter()).setWords(new ArrayList<DictItem>());
                        ((DictionaryAdapter) getListView().getAdapter()).notifyDataSetChanged();
                    }})
                .setNegativeButton(android.R.string.no, null).show();

    }


    private void enqueueFavouritesDownload() {
        Toast.makeText(this, "Downloading favourites...", Toast.LENGTH_LONG).show();

        for (DictItem item : repo.all()) {
            downloadRequested(item);
        }
    }

    private void registerDownloadReceiver() {
        mDownloadReceiver = new DownloadReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        this.registerReceiver(mDownloadReceiver, intentFilter);
    }

    private void unregisterDownloadReceiver() {
        this.unregisterReceiver(mDownloadReceiver);
    }

    private ListView getListView() { return mListView; }

    private void downloadRequested(DictItem item) {
        if (new DictItemOfflineAvailability(this, item).availableOffline()) {
            Log.d(getLocalClassName(), item.video + " was already downloaded.");
            return;
        }
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(item.video));
        Log.d(getLocalClassName(), "Enqueuing download for " + item.video);
        request.setDestinationInExternalFilesDir(this, "videos", item.videoFilename());
        request.setTitle("Sign video: " + item.gloss);
        request.setVisibleInDownloadsUi(false);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        monitorDownloadProgress(mDownloadManager.enqueue(request), item);
    }


    private void removeRequested(DictItem item) {
        repo.remove(item);
        ((DictionaryAdapter) getListView().getAdapter()).notifyDataSetChanged();

    }

    @Override
    public void listItemClicked(DictItem item) {
        Log.d("list", item.gloss);
        Intent next = new Intent();
        next.setClass(this, WordActivity.class);
        next.putExtra("item", item);
        startActivity(next);
    }



    @Override
    public View getControlView(final DictItem item, ViewGroup parent) {
        View downloadButton =  getLayoutInflater().inflate(
                R.layout.activity_favourites__download_button,
                parent,
                false);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadRequested(item);
            }
        });

        return downloadButton;
    }

    @Override
    public Context getContext() {
        return this;
    }

    private boolean notifyDownloadProgress(DictItem item, int downloadStatus) {
        switch (downloadStatus) {
            case DownloadManager.STATUS_SUCCESSFUL:
                Toast.makeText(this, "Download of " + item.gloss + " completed.", Toast.LENGTH_SHORT).show();
                return true; // Complete
            case DownloadManager.STATUS_PENDING:
                case DownloadManager.STATUS_PAUSED:
                Toast.makeText(this, "Download of " + item.gloss + " is waiting to start.", Toast.LENGTH_SHORT).show();
                return false; // Incomplete
            case DownloadManager.STATUS_FAILED:
                Toast.makeText(this, "Download of " + item.gloss + " failed. Please try again.", Toast.LENGTH_SHORT).show();
                return true; // Complete
        }

        return false;
    }

    @Override
    public void onConnectionStatusChanged(boolean connected) {
        mListView.invalidateViews();
    }

    private void monitorDownloadProgress(final long downloadId, final DictItem item) {
        new Thread(new Runnable() {
            boolean isDownloading = true;

            @Override
            public void run() {
                while (isDownloading) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(downloadId);
                    final Cursor result = mDownloadManager.query(query);
                    result.moveToFirst();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            int status = result.getInt(result.getColumnIndex(DownloadManager.COLUMN_STATUS));
                            isDownloading = FavouritesActivity.this.notifyDownloadProgress(item, status);
                        }
                    });
                }

            }
        }).start();
    }
}
