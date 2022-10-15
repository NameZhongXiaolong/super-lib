package com.github.gallery;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import static android.Manifest.permission.CAMERA;

/**
 * Created by ZhongXiaolong on 2021/1/27 10:02 AM.
 * <p>
 * 调用系统拍照
 * 使用{@link #start(FragmentActivity)},{@link #start(Fragment)}
 */
public class GalleryCameraFragment extends Fragment {

    private static final int REQUEST_CODE_CAMERA = 36;

    private File mFrontFile;

    private AlertDialog mPermissionDialog;

    private boolean mStartedCameraTag;

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
        if (ContextCompat.checkSelfPermission(requireContext(), CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{CAMERA}, REQUEST_CODE_CAMERA);
            } else {
                showPermissionDialog();
            }
        } else {
            //拍照
            startCamera();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mStartedCameraTag && mPermissionDialog != null) {
            //请求权限后重新进入应用
            if (ContextCompat.checkSelfPermission(requireContext(), CAMERA) == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                remove();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CAMERA) {
            for (int i = 0; i < permissions.length; i++) {
                if (CAMERA.equals(permissions[i])) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        startCamera();
                        return;
                    }
                }
            }
            showPermissionDialog();
        }
    }

    /**
     * 显示需要权限弹窗
     */
    private void showPermissionDialog() {
        if (mPermissionDialog != null) {
            mPermissionDialog.show();
            return;
        }
        mPermissionDialog = new AlertDialog
                .Builder(requireContext(), R.style.Gallery_AlertDialogTheme)
                .setTitle(R.string.gallery_hint_tip)
                .setMessage(R.string.gallery_hint_need_camera_permission)
                .setCancelable(false)
                .setPositiveButton(R.string.gallery_go_setting, (dialog, which) -> {
                    final boolean canShowRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), CAMERA);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && canShowRequestPermission) {
                        requestPermissions(new String[]{CAMERA}, REQUEST_CODE_CAMERA);
                    } else {
                        Intent starter = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                        String pkg = "com.android.settings";
                        String cls = "com.android.settings.applications.InstalledAppDetails";
                        starter.setComponent(new ComponentName(pkg, cls));
                        starter.setData(Uri.parse("package:" + requireContext().getPackageName()));
                        startActivity(starter);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.gallery_cancel, (dialog, which) -> {
                    dialog.dismiss();
                    remove();
                })
                .show();
        mPermissionDialog.setCanceledOnTouchOutside(false);
        mPermissionDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#E84393"));
        mPermissionDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#999999"));
    }

    /**
     * 启动拍照
     */
    private void startCamera() {
        mStartedCameraTag = true;
        try {
            File cameraCacheFile = new File(Environment.getExternalStorageDirectory(), "DCIM/Camera");
            if (!cameraCacheFile.exists()) {
                if (!cameraCacheFile.mkdirs()) {
                    return;
                }
            }
            mFrontFile = new File(cameraCacheFile, System.currentTimeMillis() + ".png");
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                PackageManager packageManager = requireContext().getPackageManager();
                PackageInfo packageInfo = packageManager.getPackageInfo(requireActivity().getApplicationContext().getPackageName(), 0);
                String authority = packageInfo.packageName + ".gallery.fileprovider";
                uri = FileProvider.getUriForFile(requireContext(), authority, mFrontFile);
            } else {
                uri = Uri.fromFile(mFrontFile);
            }
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            captureIntent.putExtra("return-data", true);
            startActivityForResult(captureIntent, REQUEST_CODE_CAMERA);
        } catch (Exception e) {
            remove();
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
                    EventBus.getDefault().post(new CameraResult(path, uri));
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
            requireFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
        }
    }
}
