package com.hewgill.android.nzsldict;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class NZSLDictionary extends ListActivity
{
    private Dictionary dictionary;
    private EditText filterText;
    private TextWatcher filterTextWatcher;
    private View handshapeHeader;
    private View wotd;
    private DictAdapter adapter;
    private String handshapeFilter;
    private String locationFilter;

    class DictAdapter extends BaseAdapter {
        private int resource;
        private List<Dictionary.DictItem> words;
        private LayoutInflater inflater;
        private Filter filter;

        public DictAdapter(Context context, int resource, List<Dictionary.DictItem> words) {
            this.resource = resource;
            this.words = words;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return words.size();
        }

        @Override
        public Dictionary.DictItem getItem(int position) {
            return words.get(position);
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
            TextView gv = (TextView) v.findViewById(R.id.item_gloss);
            TextView mv = (TextView) v.findViewById(R.id.item_minor);
            TextView mtv = (TextView) v.findViewById(R.id.item_maori);
            ImageView dv = (ImageView) v.findViewById(R.id.diagram);
            if (position >= getCount()) {
                Log.e("filter", "request for item " + position + " in list of size " + getCount());
                return v;
            }
            Dictionary.DictItem item = getItem(position);
            gv.setText(item.gloss);
            mv.setText(item.minor);
            mtv.setText(item.maori);

            try {
                InputStream ims = getAssets().open(item.imagePath());
                Drawable d = Drawable.createFromStream(ims, null);
                dv.setImageDrawable(d);
            }
            catch(IOException e) {
                System.out.println(e.toString());
            }
            return v;
        }

        public Filter getFilter() {
            if (filter == null) {
                filter = new HandshapeFilter();
            }
            return filter;
        }

        private class HandshapeFilter extends Filter {
            @Override
            protected Filter.FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                final String hf = handshapeFilter;
                final String lf = locationFilter;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException x) {
                }
                if (hf != handshapeFilter || lf != locationFilter) {
                    // something has changed, abandon and we will get rerun
                    return null;
                }

                List<Dictionary.DictItem> r;
                if (constraint == null) {
                    r = dictionary.getWords();
                } else {
                    String target = constraint.toString();
                    int i = target.indexOf('|');
                    if (i >= 0) {
                        String hs = target.substring(0, i);
                        String ls = target.substring(i+1, target.length());
                        r = dictionary.getWordsByHandshape(hs, ls);
                    } else {
                        r = dictionary.getWords(target);
                    }
                }
                results.values = r;
                results.count = r.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
                if (results == null) {
                    return;
                }
                words = (List<Dictionary.DictItem>) results.values;
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        }
    }

    static class HandshapeInfo {
        final int resource_id;
        final String value;

        HandshapeInfo(int resource_id, String value) {
            this.resource_id = resource_id;
            this.value = value;
        }
    }

    static HandshapeInfo[] Handshapes = new HandshapeInfo[] {
        new HandshapeInfo(0, null),
        new HandshapeInfo(R.drawable.handshape_1_1_1, "1.1.1"),
        new HandshapeInfo(R.drawable.handshape_1_1_2, "1.1.2"),
        new HandshapeInfo(R.drawable.handshape_1_1_3, "1.1.3"),
        new HandshapeInfo(R.drawable.handshape_1_2_1, "1.2.1"),
        new HandshapeInfo(R.drawable.handshape_1_2_2, "1.2.2"),
        new HandshapeInfo(R.drawable.handshape_1_3_1, "1.3.1"),
        new HandshapeInfo(R.drawable.handshape_1_3_2, "1.3.2"),
        new HandshapeInfo(R.drawable.handshape_1_4_1, "1.4.1"),
        new HandshapeInfo(R.drawable.handshape_2_1_1, "2.1.1"),
        new HandshapeInfo(R.drawable.handshape_2_1_2, "2.1.2"),
        new HandshapeInfo(R.drawable.handshape_2_2_1, "2.2.1"),
        new HandshapeInfo(R.drawable.handshape_2_2_2, "2.2.2"),
        new HandshapeInfo(R.drawable.handshape_2_3_1, "2.3.1"),
        new HandshapeInfo(R.drawable.handshape_2_3_2, "2.3.2"),
        new HandshapeInfo(R.drawable.handshape_2_3_3, "2.3.3"),
        new HandshapeInfo(R.drawable.handshape_3_1_1, "3.1.1"),
        new HandshapeInfo(R.drawable.handshape_3_2_1, "3.2.1"),
        new HandshapeInfo(R.drawable.handshape_3_3_1, "3.3.1"),
        new HandshapeInfo(R.drawable.handshape_3_4_1, "3.4.1"),
        new HandshapeInfo(R.drawable.handshape_3_4_2, "3.4.2"),
        new HandshapeInfo(R.drawable.handshape_3_5_1, "3.5.1"),
        new HandshapeInfo(R.drawable.handshape_3_5_2, "3.5.2"),
        new HandshapeInfo(R.drawable.handshape_4_1_1, "4.1.1"),
        new HandshapeInfo(R.drawable.handshape_4_1_2, "4.1.2"),
        new HandshapeInfo(R.drawable.handshape_4_2_1, "4.2.1"),
        new HandshapeInfo(R.drawable.handshape_4_2_2, "4.2.2"),
        new HandshapeInfo(R.drawable.handshape_4_3_1, "4.3.1"),
        new HandshapeInfo(R.drawable.handshape_4_3_2, "4.3.2"),
        new HandshapeInfo(R.drawable.handshape_5_1_1, "5.1.1"),
        new HandshapeInfo(R.drawable.handshape_5_1_2, "5.1.2"),
        new HandshapeInfo(R.drawable.handshape_5_2_1, "5.2.1"),
        new HandshapeInfo(R.drawable.handshape_5_3_1, "5.3.1"),
        new HandshapeInfo(R.drawable.handshape_5_3_2, "5.3.2"),
        new HandshapeInfo(R.drawable.handshape_5_4_1, "5.4.1"),
        new HandshapeInfo(R.drawable.handshape_6_1_1, "6.1.1"),
        new HandshapeInfo(R.drawable.handshape_6_1_2, "6.1.2"),
        new HandshapeInfo(R.drawable.handshape_6_1_3, "6.1.3"),
        new HandshapeInfo(R.drawable.handshape_6_1_4, "6.1.4"),
        new HandshapeInfo(R.drawable.handshape_6_2_1, "6.2.1"),
        new HandshapeInfo(R.drawable.handshape_6_2_2, "6.2.2"),
        new HandshapeInfo(R.drawable.handshape_6_2_3, "6.2.3"),
        new HandshapeInfo(R.drawable.handshape_6_2_4, "6.2.4"),
        new HandshapeInfo(R.drawable.handshape_6_3_1, "6.3.1"),
        new HandshapeInfo(R.drawable.handshape_6_3_2, "6.3.2"),
        new HandshapeInfo(R.drawable.handshape_6_4_1, "6.4.1"),
        new HandshapeInfo(R.drawable.handshape_6_4_2, "6.4.2"),
        new HandshapeInfo(R.drawable.handshape_6_5_1, "6.5.1"),
        new HandshapeInfo(R.drawable.handshape_6_5_2, "6.5.2"),
        new HandshapeInfo(R.drawable.handshape_6_6_1, "6.6.1"),
        new HandshapeInfo(R.drawable.handshape_6_6_2, "6.6.2"),
        new HandshapeInfo(R.drawable.handshape_7_1_1, "7.1.1"),
        new HandshapeInfo(R.drawable.handshape_7_1_2, "7.1.2"),
        new HandshapeInfo(R.drawable.handshape_7_1_3, "7.1.3"),
        new HandshapeInfo(R.drawable.handshape_7_1_4, "7.1.4"),
        new HandshapeInfo(R.drawable.handshape_7_2_1, "7.2.1"),
        new HandshapeInfo(R.drawable.handshape_7_3_1, "7.3.1"),
        new HandshapeInfo(R.drawable.handshape_7_3_2, "7.3.2"),
        new HandshapeInfo(R.drawable.handshape_7_3_3, "7.3.3"),
        new HandshapeInfo(R.drawable.handshape_7_4_1, "7.4.1"),
        new HandshapeInfo(R.drawable.handshape_7_4_2, "7.4.2"),
        new HandshapeInfo(R.drawable.handshape_8_1_1, "8.1.1"),
        new HandshapeInfo(R.drawable.handshape_8_1_2, "8.1.2"),
        new HandshapeInfo(R.drawable.handshape_8_1_3, "8.1.3"),
    };

    static HandshapeInfo[] Locations = new HandshapeInfo[] {
        new HandshapeInfo(0, null),
        new HandshapeInfo(R.drawable.location_1_1_in_front_of_body,   "in front of body"),
        new HandshapeInfo(R.drawable.location_2_2_in_front_of_face,   "in front of face"),
        new HandshapeInfo(R.drawable.location_3_3_head,               "head"),
        new HandshapeInfo(R.drawable.location_3_4_top_of_head,        "top of head"),
        new HandshapeInfo(R.drawable.location_3_5_eyes,               "eyes"),
        new HandshapeInfo(R.drawable.location_3_6_nose,               "nose"),
        new HandshapeInfo(R.drawable.location_3_7_ear,                "ear"),
        new HandshapeInfo(R.drawable.location_3_8_cheek,              "cheek"),
        new HandshapeInfo(R.drawable.location_3_9_lower_head,         "lower head"),
        new HandshapeInfo(R.drawable.location_4_10_neck_throat,       "neck/throat"),
        new HandshapeInfo(R.drawable.location_4_11_shoulders,         "shoulders"),
        new HandshapeInfo(R.drawable.location_4_12_chest,             "chest"),
        new HandshapeInfo(R.drawable.location_4_13_abdomen,           "abdomen"),
        new HandshapeInfo(R.drawable.location_4_14_hips_pelvis_groin, "hips/pelvis/groin"),
        new HandshapeInfo(R.drawable.location_4_15_upper_leg,         "upper leg"),
        new HandshapeInfo(R.drawable.location_5_16_upper_arm,         "upper arm"),
        new HandshapeInfo(R.drawable.location_5_17_elbow,             "elbow"),
        new HandshapeInfo(R.drawable.location_5_18_lower_arm,         "lower arm"),
        new HandshapeInfo(R.drawable.location_6_19_wrist,             "wrist"),
        new HandshapeInfo(R.drawable.location_6_20_fingers_thumb,     "fingers/thumb"),
        new HandshapeInfo(R.drawable.location_6_22_back_of_hand,      "back of hand"),
    };

    static class HandshapeAdapter extends ArrayAdapter<HandshapeInfo> {
        HandshapeInfo[] icons;

        public HandshapeAdapter(Context context, int resource, int textViewResourceId, HandshapeInfo[] icons) {
            super(context, resource, textViewResourceId, icons);
            this.icons = icons;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            TextView tv = (TextView) v.findViewById(R.id.handshape_label);
            ImageView iv = (ImageView) v.findViewById(R.id.handshape_item);
            if (position == 0) {
                tv.setText("(any)");
                tv.setVisibility(View.VISIBLE);
                iv.setVisibility(View.GONE);
            } else {
                iv.setImageResource(this.icons[position].resource_id);
                iv.setVisibility(View.VISIBLE);
                tv.setVisibility(View.GONE);
            }
            return v;
        }
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        dictionary = new Dictionary(getApplicationContext());
        // following based on http://stackoverflow.com/questions/1737009/how-to-make-a-nice-looking-listview-filter-on-android
        setContentView(R.layout.main);

        View header = LayoutInflater.from(this).inflate(R.layout.handshape, null);
        getListView().addHeaderView(header, null, false);
        Gallery hsv = (Gallery) findViewById(R.id.handshape);
        hsv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handshapeFilter = Handshapes[position].value;
                updateHandshapeList();
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        hsv.setAdapter(new HandshapeAdapter(this, R.layout.handshape_item, R.id.handshape_label, Handshapes));
        Gallery lcv = (Gallery) findViewById(R.id.location);
        lcv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                locationFilter = Locations[position].value;
                updateHandshapeList();
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        lcv.setAdapter(new HandshapeAdapter(this, R.layout.handshape_item, R.id.handshape_label, Locations));
        handshapeHeader = header.findViewById(R.id.handshape_header);
        handshapeHeader.setVisibility(View.GONE);

        adapter = new DictAdapter(this, R.layout.list_item, dictionary.getWords());
        setListAdapter(adapter);
        filterText = (EditText) findViewById(R.id.building_list_search_box);
        filterTextWatcher = new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int before, int count) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
                getListView().setVisibility(s.length() != 0 ? View.VISIBLE : View.INVISIBLE);
                wotd.setVisibility(s.length() == 0 ? View.VISIBLE : View.INVISIBLE);
            }
        };
        filterText.addTextChangedListener(filterTextWatcher);
        //filterText.requestFocus();
        getListView().setVisibility(View.INVISIBLE);
        wotd = (View) findViewById(R.id.building_list_wotd);
        ImageView wotdImage = (ImageView) findViewById(R.id.building_list_wotd_image);
        wotd.setBackgroundColor(Color.WHITE);
        TextView wotdGloss = (TextView) findViewById(R.id.building_list_wotd_gloss);
        {
            final Dictionary.DictItem item = dictionary.getWordOfTheDay();

            try {
                InputStream ims = getAssets().open(item.imagePath());
                Drawable d = Drawable.createFromStream(ims, null);
                wotdImage.setImageDrawable(d);
            }
            catch(IOException e) {
                System.out.println(e.toString());
            }

            wotdImage.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    Intent next = new Intent();
                    next.setClass(NZSLDictionary.this, VideoActivity.class);
                    next.putExtra("item", item);
                    startActivity(next);
                }
            });
            wotdGloss.setText(item.gloss);
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        filterText.removeTextChangedListener(filterTextWatcher);
    }

    public void toggleHandshapeMode(View button)
    {
        boolean on = ((ToggleButton) button).isChecked();
        if (on) {
            filterText.setText("(handshape search)");
            filterText.setEnabled(false);
            handshapeHeader.setVisibility(View.VISIBLE);
            updateHandshapeList();
        } else {
            filterText.setText("");
            filterText.setEnabled(true);
            handshapeHeader.setVisibility(View.GONE);
            adapter.getFilter().filter(null);
        }
    }

    private void updateHandshapeList()
    {
        String hf = handshapeFilter != null ? handshapeFilter : "";
        String lf = locationFilter != null ? locationFilter : "";
        adapter.getFilter().filter(hf + "|" + lf);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        Dictionary.DictItem item = (Dictionary.DictItem) getListView().getItemAtPosition(position);
        Log.d("list", item.gloss);
        Intent next = new Intent();
        next.setClass(this, WordActivity.class);
        next.putExtra("item", item);
        startActivity(next);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
