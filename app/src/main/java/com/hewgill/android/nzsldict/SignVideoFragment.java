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
public class SignVideoFragment extends Fragment implements NetworkManager.NetworkCallback {
    private static final String ARG_DICT_ITEM = "dictItem";
    private VideoView mVideo;
    private View mRootView;
    private View mAnchorView;
    private DictItem mDictItem;
    private NetworkManager mNetworkManager;
    private DictItemOfflineAvailability mOfflineAvailability;
    private MediaController mMediaController;
    private View mNoNetworkFrame;


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
        mMediaController = new MediaController(getContext()) {
            @Override
            public void show(int timeout) {
                // we want the controller to remain shown until hide() is called by default,
                // which can be done by passing a timeout of 0; however some of the private
                // internals of MediaController call show _and_ they favor doing so with the
                // default value explicitly rather than calling show(), so we have to do this
                // override which just turns any use of the default value into 0.
                super.show(timeout == 3000 ? 0 : timeout);
            }
        };
        mNetworkManager = new NetworkManager();
        if (getArguments() != null) {
            mDictItem = (DictItem) getArguments().getSerializable(ARG_DICT_ITEM);
            mOfflineAvailability = new DictItemOfflineAvailability(getActivity(), mDictItem);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mNetworkManager.registerContext(getContext(), this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mNetworkManager.registerContext(getContext(), this);
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

        mVideo = mRootView.findViewById(R.id.sign_video);
        mNoNetworkFrame = mRootView.findViewById(R.id.sign_video_network_unavailable);


        mVideo.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });

        final boolean[] isPrepared = { false };
        mVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                // avoid resetting the media controller multiple times, because
                // that triggers the controls to be hidden which can be confusing
                if (isPrepared[0]) {
                    mVideo.start();

                    return;
                }

                mVideo.setMediaController(mMediaController);
                mMediaController.setAnchorView(mAnchorView);
                isPrepared[0] = true;

                mVideo.start();
            }
        });


        return mRootView;
    }

    private boolean isVideoAvailable() {
        return mOfflineAvailability.availableOffline() || mNetworkManager.connected;
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

    @Override
    public void onConnectionStatusChanged(boolean connected) {
        this.updateViewForConnectivityStatus();
    }
}
