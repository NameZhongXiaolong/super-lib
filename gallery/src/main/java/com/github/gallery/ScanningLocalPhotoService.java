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

        data.add(new PhotoData().setCatalog(getResources().getString(R.string.gallery_catalog_all)).setPhotoList(new ArrayList<>()));

        while (cursor.moveToNext()) {
            //获取图片的路径
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

            //不显示保存路径图片(太耗性能了,在sql里面加判断)
//            if (path.contains(MainApplication.getFileRoot())) {
//                continue;
//            }

            //往全部里添加
            data.get(0).getPhotoList().add(path);

            //按路径名添加,获取该图片的父路径名
            File parentFile = new File(path).getParentFile();
            if (parentFile != null) {
                String parentName = parentFile.getName();
                PhotoData photoData = new PhotoData().setCatalog(parentName);
                int index = data.indexOf(photoData);
                if (index > 0) {
                    data.get(index).getPhotoList().add(path);
                }else{
                    photoData.setPhotoList(new ArrayList<>(Collections.singleton(path)));
                    data.add(photoData);
                }
            }
        }

        Collections.sort(data, (o1, o2) -> Integer.compare(o2.getPhotoList().size(),o1.getPhotoList().size()));

        EventBus.getDefault().post(data);

        cursor.close();

        stopSelf();
    }

    @Override
    public Resources getResources() {
        Resources resources = super.getResources();
        if (resources != null) {
            Configuration configuration = resources.getConfiguration();
            if (configuration != null) {
                String language = new ChoiceGallerySetting(getBaseContext()).getLanguage();
                Locale defLocale = configuration.locale;

                //简体中文
                if (ChoiceGallerySetting.LANGUAGE_ZH_CN.equals(language)) {
                    if (!Objects.equals("zh",defLocale.getLanguage()) || !Objects.equals("CN",defLocale.getCountry())) {
                        configuration.setLocale(Locale.SIMPLIFIED_CHINESE);
                        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
                    }
                }

                //繁体中文
                if (ChoiceGallerySetting.LANGUAGE_ZH_TW.equals(language)) {
                    if (!Objects.equals("zh",defLocale.getLanguage()) || !Objects.equals("TW",defLocale.getCountry())) {
                        configuration.setLocale(Locale.TRADITIONAL_CHINESE);
                        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
                    }
                }

                //英文
                if (ChoiceGallerySetting.LANGUAGE_EN.equals(language)) {
                    configuration.setLocale(Locale.ENGLISH);
                    if (!Objects.equals("en", defLocale.getLanguage())) {
                        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
                    }
                }
            }
        }
        return resources;
    }

}
