package com.hewgill.android.nzsldict;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignVideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignVideoFragment extends Fragment {
    private static final String ARG_DICT_ITEM = "dictItem";
    private VideoView mVideo;
    private View mRootView;
    private View mAnchorView;
    private boolean mMediaControllerLaidOut = false;
    private DictItem mDictItem;
    private DictItemOfflineAvailability mOfflineAvailability;
    private MediaController mMediaController;
    private View mNoNetworkFrame;
    private IntentFilter mConnectivityIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    private BroadcastReceiver mConnectivityChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SignVideoFragment.this.updateViewForConnectivityStatus();
        }
    };

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
    public static SignVideoFragment newInstance(DictItem dictItem) {
        SignVideoFragment fragment = new SignVideoFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DICT_ITEM, dictItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMediaController = new MediaController(getContext());
        if (getArguments() != null) {
            mDictItem = (DictItem) getArguments().getSerializable(ARG_DICT_ITEM);
            mOfflineAvailability = new DictItemOfflineAvailability(getActivity(), mDictItem);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(mConnectivityChangeReceiver, mConnectivityIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(mConnectivityChangeReceiver);
    }

    public boolean updateViewForConnectivityStatus() {
        boolean videoIsAvailable = isVideoAvailable();

        if (videoIsAvailable) {
            mVideo.setVisibility(View.VISIBLE);
            mNoNetworkFrame.setVisibility(View.GONE);
            mVideo.setVideoURI(mOfflineAvailability.cacheFirstVideoUri());
        } else {
            mVideo.setVisibility(View.GONE);
            mNoNetworkFrame.setVisibility(View.VISIBLE);
        }

        return videoIsAvailable;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         mRootView = inflater.inflate(R.layout.fragment_sign_video, container, false);
         mAnchorView = mRootView.findViewById(R.id.sign_video_anchor);

        mVideo = (VideoView) mRootView.findViewById(R.id.sign_video);
        mNoNetworkFrame = mRootView.findViewById(R.id.sign_video_network_unavailable);


        mVideo.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
        mVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mVideo.setMediaController(mMediaController);
                mMediaController.setAnchorView(mAnchorView);
            }
        });

        // Start loading video if network is available
        if (isVideoAvailable()) updateViewForConnectivityStatus();

        return mRootView;
    }

    private boolean isVideoAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return mOfflineAvailability.availableOffline() || (activeNetworkInfo != null && activeNetworkInfo.isConnected());
    }

    public void showControls() {
        mVideo.setVisibility(View.VISIBLE);
        mMediaController.show();
    }

    public void stop() {
        mVideo.stopPlayback();
        mVideo.setVisibility(View.INVISIBLE);
        mMediaController.hide();
    }

}
