package com.hewgill.android.nzsldict;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

public class VideoActivity extends Activity {

    private TextView gloss;
    private TextView minor;
    private TextView maori;
    private ImageView handshape;
    private ImageView location;
    private VideoView video;
    private Dictionary.DictItem item;

    class NoHideMediaController extends MediaController {
        public NoHideMediaController(Context context) {
            super(context);
        }

        // http://stackoverflow.com/questions/6051825/android-back-button-and-mediacontroller
        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                finish();
                return true;
            }
            return super.dispatchKeyEvent(event);
        }

        // http://stackoverflow.com/questions/6651718/keeping-mediacontroller-on-the-screen-in-a-videoview
        @Override
        public void hide() {
            show(0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video);
        gloss = (TextView) findViewById(R.id.gloss);
        minor = (TextView) findViewById(R.id.minor);
        maori = (TextView) findViewById(R.id.maori);
        handshape = (ImageView) findViewById(R.id.handshape);
        location = (ImageView) findViewById(R.id.location);
        video = (VideoView) findViewById(R.id.video);
        Intent intent = getIntent();
        item = (Dictionary.DictItem) intent.getSerializableExtra("item");
        gloss.setText(item.gloss);
        minor.setText(item.minor);
        maori.setText(item.maori);
        handshape.setImageResource(getApplicationContext().getResources().getIdentifier(item.handshapeImage(), "drawable", getPackageName()));
        location.setImageResource(getApplicationContext().getResources().getIdentifier(item.locationImage(), "drawable", getPackageName()));
        MediaController mc = new NoHideMediaController(this);
        video.setMediaController(mc);
        final ProgressDialog pleaseWait = ProgressDialog.show(this, "", "Loading video...");

        video.setVideoURI(Uri.parse(item.video));
        video.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            public boolean onError(MediaPlayer mp, int what, int extra) {
                pleaseWait.dismiss();
                return false;
            }
        });
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                pleaseWait.dismiss();
            }
        });
        video.start();
    }
}
