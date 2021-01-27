package com.github.gallery;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 * Created by ZhongXiaolong on 2021/1/27 11:05 AM.
 * <p>
 * 裁剪图片
 */
public class GalleryCropFragment extends Fragment {

    public static UCrop.Options mUCropOptions;

    public static void start(FragmentActivity activity, String path) {
        GalleryCropFragment cropFragment = new GalleryCropFragment();
        Bundle args = new Bundle();
        args.putString("path", path);
        cropFragment.setArguments(args);
        activity.getSupportFragmentManager().beginTransaction().add(cropFragment, "crop").commitAllowingStateLoss();
    }

    public static void start(Fragment fragment, String path) {
        GalleryCropFragment cropFragment = new GalleryCropFragment();
        Bundle args = new Bundle();
        args.putString("path", path);
        cropFragment.setArguments(args);
        fragment.getChildFragmentManager().beginTransaction().add(cropFragment, "crop").commitAllowingStateLoss();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String path = getArguments() != null ? getArguments().getString("path", null) : null;
        if (!TextUtils.isEmpty(path)) {
            Uri source = Uri.fromFile(new File(path));
            File outFile = new File(requireContext().getCacheDir(), "gallery_crop.png");
            Uri destination = Uri.fromFile(outFile);

            UCrop.Options options = mUCropOptions;
            if (options == null) {
                options = ChoiceGallery.getDefCropOptions(requireContext());
            }
            Intent intent = UCrop.of(source, destination)
                    .withOptions(options)
                    .getIntent(requireContext());
            intent.setClass(requireContext(), GalleryUCropActivity.class);
            startActivityForResult(intent,UCrop.REQUEST_CROP);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                //完成裁剪
                final Uri resultUri = UCrop.getOutput(data);
                if (resultUri != null) {
                    String path = resultUri.getPath();
                    EventBus.getDefault().post(new CropResult(path));
                }
            }
            remove();
        }
    }

    /**
     * 删除
     */
    private void remove() {
        if (isAdded()) {
            getFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
        }
    }
}