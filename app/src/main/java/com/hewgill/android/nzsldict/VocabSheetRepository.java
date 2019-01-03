package com.hewgill.android.nzsldict;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class VocabSheetRepository {
    private final Context mContext;
    private final SharedPreferences mPrefsStore;
    private final String KEY = "NZSL_VOCAB_SHEET";
    private final String PREF_KEY = "VOCAB_SHEET_ITEMS";

    public VocabSheetRepository(Context ctx) {
        mContext = ctx;
        mPrefsStore = ctx.getSharedPreferences(KEY, Context.MODE_PRIVATE);
    }

    public List<DictItem> all() {
        List<DictItem> allItems = new ArrayList<>();
        Dictionary dict = new Dictionary(mContext);
        for (String key : allKeys()) {
            DictItem item = dict.getWord(key);
            if (item != null) { allItems.add(item); }
        }

        return allItems;
    }

    public void add(DictItem item) {
        SharedPreferences.Editor edit = mPrefsStore.edit();
        Set<String> newKeys = allKeys();
        newKeys.add(item.uniqueKey());
        edit.putStringSet(PREF_KEY, newKeys);
        edit.apply();
    }

    public void remove(DictItem item) {
        SharedPreferences.Editor edit = mPrefsStore.edit();
        Set<String> newKeys = allKeys();
        newKeys.remove(item.uniqueKey());
        (new DictItemOfflineAvailability(mContext, item)).unavailableOffline();
        edit.putStringSet(PREF_KEY, newKeys);
        edit.apply();
    }

    public boolean contains(DictItem item) {
        return allKeys().contains(item.uniqueKey());
    }

    private Set<String> allKeys() {
        Set<String> defaultValue = new TreeSet<>();
        return mPrefsStore.getStringSet(PREF_KEY, defaultValue);
    }
}
