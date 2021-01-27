package com.github.gallery;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 * Created by ZhongXiaolong on 2021/1/27 10:02 AM.
 * <p>
 * 调用系统拍照
 * 使用{@link #start(FragmentActivity)},{@link #start(Fragment)}
 */
public class GalleryCameraFragment extends Fragment {

    private static final int REQUEST_CODE_CAMERA = 36;

    private File mFrontFile;

    public static void start(FragmentActivity activity) {
        activity.getSupportFragmentManager().beginTransaction().add(new GalleryCameraFragment(), "camera").commitAllowingStateLoss();
    }

    public static void start(Fragment fragment) {
        fragment.getChildFragmentManager().beginTransaction().add(new GalleryCameraFragment(), "camera").commitAllowingStateLoss();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //检查权限
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
            }
        } else {
            //拍照
            startCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CAMERA) {
            for (int i = 0; i < permissions.length; i++) {
                if (Manifest.permission.CAMERA.equals(permissions[i])) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        startCamera();
                        return;
                    }
                }
            }
            remove();
        }
    }

    /**
     * 启动拍照
     */
    private void startCamera() {
        try {
            File cameraCacheFile = new File(Environment.getExternalStorageDirectory(), "DCIM/Camera");
            if (!cameraCacheFile.exists()) {
                if (!cameraCacheFile.mkdirs()) {
                    return;
                }
            }
            mFrontFile = File.createTempFile("camera", ".jpg", cameraCacheFile);
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(requireContext(), "com.github.gallery.fileprovider", mFrontFile);
            } else {
                uri = Uri.fromFile(mFrontFile);
            }
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            captureIntent.putExtra("return-data", true);
            startActivityForResult(captureIntent, REQUEST_CODE_CAMERA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                //拍照成功
                String[] paths = {mFrontFile.getAbsolutePath()};
                String[] mimeTypes = new String[]{"image/jpeg", "image/jpg", "image/png"};
                MediaScannerConnection.scanFile(requireContext(), paths, mimeTypes, (path, uri) -> {
                    EventBus.getDefault().post(new CameraResult(path));
                    remove();
                });
            } else {
                remove();
            }
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
