package com.hewgill.android.nzsldict;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

public class FavouritesActivity extends BaseActivity implements DictionaryAdapter.Presenter,
        NetworkManager.NetworkCallback,
        DownloadReceiver.DownloadCallback,
        AdapterView.OnItemClickListener {

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
        mListView.setAdapter(adapter);
        mListView.setEmptyView(findViewById(R.id.empty_favourites));
        mNetworkManager = new NetworkManager();
        mDownloadManager = (DownloadManager) this.getSystemService(DOWNLOAD_SERVICE);
        mDownloadReceiver = new DownloadReceiver();
        mListView.setOnItemClickListener(this);
        ((WebView) findViewById(R.id.empty_favourites_webview))
                .loadUrl("file:///android_asset/html/favourites.html");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNetworkManager.registerContext(getContext(), this);
        mDownloadReceiver.registerContext(getContext(), this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDownloadReceiver.unregisterContext(getContext());
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
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        for (DictItem item : repo.all()) {
                            removeRequested(item);
                        }
                        adapter.setWords(new ArrayList<DictItem>());
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();

    }


    private void enqueueFavouritesDownload() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to make all of your favourites available offline?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        for (DictItem item : repo.all()) {
                            downloadRequested(item);
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }


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
        adapter.notifyDataSetChanged();
    }


    @Override
    public View getControlView(final DictItem item, ViewGroup parent) {
        DictItemOfflineAvailability availability = new DictItemOfflineAvailability(this, item);
        if (availability.availableOffline()) return null;

        boolean inProgress = item.downloadStatus == DownloadManager.STATUS_PENDING ||
                item.downloadStatus == DownloadManager.STATUS_PAUSED ||
                item.downloadStatus == DownloadManager.STATUS_RUNNING;

        if (inProgress) {
            TextView tv = new TextView(this);
            tv.setText("...");
            return tv;
        }

        View downloadButton = getLayoutInflater().inflate(
                R.layout.activity_favourites__download_button,
                parent,
                false);

        if (mNetworkManager.connected) {
            ((ImageButton) downloadButton).clearColorFilter();
        } else {
            ((ImageButton) downloadButton).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
            return downloadButton;
        }

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
        item.downloadStatus = downloadStatus;
        mListView.invalidateViews();

        return downloadStatus == DownloadManager.STATUS_SUCCESSFUL ||
                downloadStatus == DownloadManager.STATUS_FAILED;
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
                            int status = result.getInt(result.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
                            isDownloading = FavouritesActivity.this.notifyDownloadProgress(item, status);
                        }
                    });
                }

            }
        }).start();
    }

    @Override
    public void onDownloadCompleted() {
        mListView.invalidateViews();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        DictItem item = adapter.getItem(position);
        Log.d("list", item.gloss);
        Intent next = new Intent();
        next.setClass(this, WordActivity.class);
        next.putExtra("item", item);
        startActivity(next);
    }

}
