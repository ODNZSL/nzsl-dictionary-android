package com.hewgill.android.nzsldict;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class WordActivity extends Activity {

    private TextView gloss;
    private TextView minor;
    private TextView maori;
    private ImageView handshape;
    private ImageView location;
    private ImageView diagram;
    private Dictionary.DictItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word);
        gloss = (TextView) findViewById(R.id.gloss);
        minor = (TextView) findViewById(R.id.minor);
        maori = (TextView) findViewById(R.id.maori);
        handshape = (ImageView) findViewById(R.id.handshape);
        location = (ImageView) findViewById(R.id.location);
        diagram = (ImageView) findViewById(R.id.diagram);
        diagram.setBackgroundColor(Color.WHITE);
        Intent intent = getIntent();
        item = (Dictionary.DictItem) intent.getSerializableExtra("item");
        gloss.setText(item.gloss);
        minor.setText(item.minor);
        maori.setText(item.maori);
        handshape.setImageResource(getApplicationContext().getResources().getIdentifier(item.handshapeImage(), "drawable", getPackageName()));
        location.setImageResource(getApplicationContext().getResources().getIdentifier(item.locationImage(), "drawable", getPackageName()));
        int id = getApplicationContext().getResources().getIdentifier(item.imageName(), "drawable", getPackageName());
        diagram.setImageResource(id);
        diagram.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent next = new Intent();
                next.setClass(WordActivity.this, VideoActivity.class);
                next.putExtra("item", item);
                startActivity(next);
            }
        });
    }
}
