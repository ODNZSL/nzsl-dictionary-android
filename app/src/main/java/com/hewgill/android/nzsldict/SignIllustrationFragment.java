package com.hewgill.android.nzsldict;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignIllustrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignIllustrationFragment extends Fragment {
    private static final String ARG_DICT_ITEM = "dictItem";
    private DictItem mDictItem;

    public SignIllustrationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param dictItem The dictItem to use as the context for the fragment
     * @return A new instance of fragment SignIllustrationFragment.
     */
    public static SignIllustrationFragment newInstance(DictItem dictItem) {
        SignIllustrationFragment fragment = new SignIllustrationFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DICT_ITEM, dictItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDictItem = (DictItem) getArguments().getSerializable(ARG_DICT_ITEM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sign_illustration, container, false);
        ImageView illustrationImageView = (ImageView) rootView.findViewById(R.id.sign_illustration);
        ImageView handshapeImageView    = (ImageView) rootView.findViewById(R.id.sign_handshape);
        ImageView locationImageView     = (ImageView) rootView.findViewById(R.id.sign_location);

        illustrationImageView.setContentDescription(mDictItem.gloss + " illustration");
        handshapeImageView.setContentDescription(mDictItem.handshape);
        locationImageView.setContentDescription(mDictItem.location);
        handshapeImageView.setImageResource(getContext().getResources().getIdentifier(mDictItem.handshapeImage(), "drawable", getContext().getPackageName()));
        locationImageView.setImageResource(getContext().getResources().getIdentifier(mDictItem.locationImage(), "drawable", getContext().getPackageName()));

        try {
            InputStream ims = getActivity().getAssets().open(mDictItem.imagePath());
            Drawable d = Drawable.createFromStream(ims, null);
            illustrationImageView.setImageDrawable(d);
        } catch (IOException e) {
            System.out.println(e.toString());
        }

        return rootView;
    }
}
