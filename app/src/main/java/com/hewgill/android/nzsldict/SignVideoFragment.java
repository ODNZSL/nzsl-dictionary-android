package com.hewgill.android.nzsldict;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import java.io.IOException;
import java.io.InputStream;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignVideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignVideoFragment extends Fragment {
    private static final String ARG_DICT_ITEM = "dictItem";
    private VideoView mVideo;
    private View mRootView;
    private boolean mMediaControllerLaidOut = false;
    private Dictionary.DictItem mDictItem;
    private NoHideMediaController mMediaController;

    public SignVideoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param dictItem The dictItem to use as the context for the fragment
     * @return A new instance of fragment SignIllustrationFragment.
     */
    public static SignVideoFragment newInstance(Dictionary.DictItem dictItem) {
        SignVideoFragment fragment = new SignVideoFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DICT_ITEM, dictItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMediaController = new NoHideMediaController(getContext());
        if (getArguments() != null) {
            mDictItem = (Dictionary.DictItem) getArguments().getSerializable(ARG_DICT_ITEM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         mRootView = inflater.inflate(R.layout.fragment_sign_video, container, false);

        mVideo = (VideoView) mRootView.findViewById(R.id.sign_video);
        mVideo.setVideoURI(Uri.parse(mDictItem.video));
        mVideo.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
        mVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                fixLayoutOfMediaController();
            }
        });

        return mRootView;
    }

    private void fixLayoutOfMediaController() {
        if (mMediaControllerLaidOut) return;
        RelativeLayout parentLayout = (RelativeLayout) mVideo.getParent();
        FrameLayout frameLayout = (FrameLayout) mMediaController.getParent();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, this.getId());

        ((LinearLayout)frameLayout.getParent()).removeView(frameLayout);
        parentLayout.addView(frameLayout, layoutParams);

        mMediaController.setAnchorView(mVideo);
        mVideo.setMediaController(mMediaController);
        mMediaController.hide();
        mMediaControllerLaidOut = true;
    }

    class NoHideMediaController extends MediaController {
        public NoHideMediaController(Context context) {
            super(context);
        }

        // http://stackoverflow.com/questions/6051825/android-back-button-and-mediacontroller
        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                ((Activity) getContext()).finish();
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

}
