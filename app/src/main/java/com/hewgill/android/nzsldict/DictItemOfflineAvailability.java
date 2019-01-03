package com.hewgill.android.nzsldict;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.net.URI;

public class DictItemOfflineAvailability {
    private final Context mContext;
    private final DictItem mItem;

    public DictItemOfflineAvailability(Context ctx, DictItem item) {
        mContext = ctx;
        mItem = item;
    }

    public boolean availableOffline() {
        File videoFile = new File(pathToStoredVideo());
        return videoFile.exists();
    }

    public Uri cacheFirstVideoUri() {
        if (availableOffline()) { return Uri.parse(pathToStoredVideo()); }
        else { return Uri.parse(mItem.video); }
    }

    public String pathToStoredVideo() {
        return mContext.getExternalFilesDir("videos") + "/" + mItem.videoFilename();
    }

    public void unavailableOffline() {
        File videoFile = new File(pathToStoredVideo());
        if (!videoFile.exists()) { return; }
        videoFile.delete();
    }
}
