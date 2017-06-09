package com.hewgill.android.nzsldict;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
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
    private Dictionary.DictItem mDictItem;

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
        if (getArguments() != null) {
            mDictItem = (Dictionary.DictItem) getArguments().getSerializable(ARG_DICT_ITEM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sign_video, container, false);
        VideoView video = (VideoView) rootView;
        MediaController mc = new NoHideMediaController(getContext());
        video.setMediaController(mc);
        video.setVideoURI(Uri.parse(mDictItem.video));
        video.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {

            }
        });
        video.start();

        return rootView;
    }

    class NoHideMediaController extends MediaController {
        public NoHideMediaController(Context context) {
            super(context);
        }

        // http://stackoverflow.com/questions/6051825/android-back-button-and-mediacontroller
        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                getActivity().finish();
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
