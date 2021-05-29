package com.github.gallery;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by ZhongXiaolong on 2020/4/21 11:10.
 */
public class GalleryPreviewFragment extends Fragment {

    static GalleryPreviewFragment newInstance(MediaImage photo) {
        Bundle args = new Bundle();
        args.putParcelable("photo", photo);
        GalleryPreviewFragment fragment = new GalleryPreviewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle bundle) {
        FrameLayout frameLayout = new FrameLayout(requireContext());
        ZoomImageView imageView = new ZoomImageView(requireContext());
        imageView.setId(R.id.image);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        frameLayout.addView(imageView, new FrameLayout.LayoutParams(MATCH_PARENT,MATCH_PARENT, Gravity.CENTER));
        return frameLayout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ZoomImageView imageView = view.findViewById(R.id.image);

        Bundle args = getArguments();
        if (args != null) {
            MediaImage mediaImage = args.getParcelable("photo");
            if (mediaImage != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Uri uri = mediaImage.getUri();
                    if (uri != null) {
                        Glide.with(requireContext()).load(uri).into(imageView);
                    }
                } else {
                    Glide.with(requireContext()).load(mediaImage.getAbsolutePath()).into(imageView);
                }
            }
        }

        imageView.setOnClickListener(v -> {
            if (getActivity() instanceof GalleryPreviewActivity) {
                ((GalleryPreviewActivity) getActivity()).switchFullScreen();
            }
        });
    }
}
