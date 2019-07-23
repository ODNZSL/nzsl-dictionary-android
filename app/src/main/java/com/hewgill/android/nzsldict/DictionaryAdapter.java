package com.hewgill.android.nzsldict;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;



class DictionaryAdapter extends BaseAdapter {
    interface Presenter {
        View getControlView(DictItem item, ViewGroup parent);
        Context getContext();
    }

    private static final int LIST_ITEM_CONTROLS = 100100;
    private int itemLayout;
    private List<DictItem> words;
    private Filter filter;
    private Presenter presenter;

    DictionaryAdapter(int itemLayout, Presenter presenter, List<DictItem> words) {
        this.itemLayout = itemLayout;
        this.presenter = presenter;
        this.words = words;
    }

    @Override
    public int getCount() {
        return words.size();
    }

    @Override
    public DictItem getItem(int position) {
        return words.get(position);
    }

    public void setWords(List<DictItem> newWords) {
        this.words = newWords;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewGroup v;
        final DictItem item = getItem(position);

        if (convertView == null) {
            v = (ViewGroup) ((LayoutInflater) presenter
                    .getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(itemLayout, parent, false);
        } else {
            v = (ViewGroup) convertView;
        }


        TextView gv = v.findViewById(R.id.item_gloss);
        TextView mv = v.findViewById(R.id.item_minor);
        TextView mtv = v.findViewById(R.id.item_maori);
        ImageView dv = v.findViewById(R.id.diagram);
        if (position >= getCount()) {
            Log.e("filter", "request for item " + position + " in list of size " + getCount());
            return v;
        }

        gv.setText(item.gloss);
        mv.setText(item.minor);
        mtv.setText(item.maori);

        View controlView = presenter.getControlView(item, v);
        if (controlView != null && v.findViewWithTag(LIST_ITEM_CONTROLS) == null) {
            controlView.setTag(LIST_ITEM_CONTROLS);
            v.addView(controlView);
        }


        try {
            InputStream ims = presenter.getContext().getAssets().open(item.imagePath());
            Drawable d = Drawable.createFromStream(ims, null);
            dv.setImageDrawable(d);
        } catch (IOException e) {
            dv.setImageDrawable(null);
            System.out.println(e.toString());
        }
        return v;
    }

    Filter getFilter() {
        return filter;
    }

    void setFilter(Filter f) {
        filter = f;
    }
}