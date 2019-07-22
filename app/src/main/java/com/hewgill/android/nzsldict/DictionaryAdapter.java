package com.hewgill.android.nzsldict;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
    private int resource;
    private List<DictItem> words;
    private LayoutInflater inflater;
    private Filter filter;
    private Context context;

    DictionaryAdapter(Context context, int resource, List<DictItem> words) {
        this.resource = resource;
        this.words = words;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        View v;
        if (convertView == null) {
            v = inflater.inflate(resource, parent, false);
        } else {
            v = convertView;
        }
        TextView gv = v.findViewById(R.id.item_gloss);
        TextView mv = v.findViewById(R.id.item_minor);
        TextView mtv = v.findViewById(R.id.item_maori);
        ImageView dv = v.findViewById(R.id.diagram);
        if (position >= getCount()) {
            Log.e("filter", "request for item " + position + " in list of size " + getCount());
            return v;
        }
        DictItem item = getItem(position);
        gv.setText(item.gloss);
        mv.setText(item.minor);
        mtv.setText(item.maori);

        try {
            InputStream ims = context.getAssets().open(item.imagePath());
            Drawable d = Drawable.createFromStream(ims, null);
            dv.setImageDrawable(d);
        } catch (IOException e) {
            dv.setImageDrawable(null);
            System.out.println(e.toString());
        }
        return v;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter f) {
        filter = f;
    }
}