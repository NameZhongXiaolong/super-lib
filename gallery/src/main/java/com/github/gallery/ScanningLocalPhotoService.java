package com.github.gallery;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.JobIntentService;

/**
 * Created by ZhongXiaolong on 2020/4/16 12:16.
 *
 * 扫描本地图片Service
 */
public class ScanningLocalPhotoService extends JobIntentService {

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, ScanningLocalPhotoService.class, 100, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        //检查权限
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            //没有权限
            EventBus.getDefault().post(new LoadResultData(LoadResultData.CODE_NO_PERMISSION));
            stopSelf();
            return;
        }

        EventBus.getDefault().post(new LoadResultData(LoadResultData.CODE_SUCCESS));

        List<PhotoData> data = new ArrayList<>();
        ContentResolver contentResolver = this.getContentResolver();
        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.DATE_MODIFIED
        };
        //只查询jpeg和png的图片
//        String selection = MediaStore.Images.Media.MIME_TYPE + "=? or " +
//                MediaStore.Images.Media.MIME_TYPE + "=? or " +
//                MediaStore.Images.Media.MIME_TYPE + "=? ";

        String selection = "_size >30 and (mime_type = ? or mime_type = ? or mime_type =?) ";
        String[] selectionArgs = {"image/jpeg", "image/jpg", "image/png"};
        String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " desc ";
        Cursor cursor = contentResolver.query(imageUri, projection, selection, selectionArgs, sortOrder);

        if (cursor == null) {
            return;
        }

        data.add(new PhotoData().setParentName(getResources().getString(R.string.gallery_catalog_all)).setPhotoList(new ArrayList<>()));

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));

            //获取uri
            Uri uri = Uri.withAppendedPath(Uri.parse("content://media/external/images/media"), "" + id);

            //获取图片的路径
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

            //图片信息
            MediaImage mediaImage = new MediaImage(uri, path);

            //不显示保存路径图片(太耗性能了,在sql里面加判断)
//            if (path.contains(MainApplication.getFileRoot())) {
//                continue;
//            }

            //往全部里添加
            data.get(0).getPhotoList().add(mediaImage);

            //按路径名添加,获取该图片的父路径名
            File parentFile = new File(path).getParentFile();
            if (parentFile != null) {
                String parentName = parentFile.getName();
                PhotoData photoData = new PhotoData().setParentName(parentName).setCatalog(getCatalog(getResources(), parentName));
                int index = data.indexOf(photoData);
                if (index > 0) {
                    data.get(index).getPhotoList().add(mediaImage);
                }else{
                    photoData.setPhotoList(new ArrayList<>(Collections.singleton(mediaImage)));
                    data.add(photoData);
                }
            }
        }

        Collections.sort(data, (o1, o2) -> Integer.compare(o2.getPhotoList().size(),o1.getPhotoList().size()));

        EventBus.getDefault().post(data);

        cursor.close();

        stopSelf();
    }

    private String getCatalog(Resources resources, String path) {
        if ("WeiXin".equalsIgnoreCase(path)) return resources.getString(R.string.gallery_wei_xin);
        if ("pictures".equalsIgnoreCase(path)) return resources.getString(R.string.gallery_pictures);
        if ("Screenshots".equalsIgnoreCase(path)) return resources.getString(R.string.gallery_screen_shots);
        if ("Download".equalsIgnoreCase(path)) return resources.getString(R.string.gallery_download);
        if ("Camera".equalsIgnoreCase(path)) return resources.getString(R.string.gallery_camera);
        if ("DCIM".equalsIgnoreCase(path)) return resources.getString(R.string.gallery_dcim);
        return path;
    }

    private Resources mResources;

    @Override
    public Resources getResources() {
        if (mResources == null) {
            mResources = ResourcesConfigUtil.create(getBaseContext(), super.getResources());
        }
        return mResources;
    }
}
