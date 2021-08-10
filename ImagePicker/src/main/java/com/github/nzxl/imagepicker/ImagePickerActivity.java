package com.github.nzxl.imagepicker;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.collection.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.nzxl.imagepicker.BaseActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.widget.AbsListView.CHOICE_MODE_SINGLE;

/**
 * Created by ZhongXiaolong on 2021/06/24 7:38 PM.
 * <p>
 * 图库列表
 */
public class ImagePickerActivity extends BaseActivity {

    private static final int REQUEST_PERMISSION_CODE = 81;
    private View mTextHint;
    private View mLlHint;
    private ImageAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_picker_activity_main);

        mLlHint = findViewById(R.id.ll_hint);
        mTextHint = findViewById(R.id.text_hint);


        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            //获取权限
            mLlHint.setVisibility(View.VISIBLE);
            Executors.newCachedThreadPool().submit(this::loadImageRun);
        } else {
            //没获取权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
            } else {
                mTextHint.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    //获取权限
                    mLlHint.setVisibility(View.VISIBLE);
                    Executors.newCachedThreadPool().submit(this::loadImageRun);
                    return;
                }
            }
            mTextHint.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 加载图片
     */
    private void loadImageRun() {
        ArrayMap<String, List<MediaImage>> dataMap = new ArrayMap<>();

        List<MediaImage> data = new ArrayList<>();
        dataMap.put("all", data);

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

        String selection = "_size >30 and (mime_type = ? or mime_type = ? or mime_type =?) ";
        String[] selectionArgs = {"image/jpeg", "image/jpg", "image/png"};
        String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " desc ";
        Cursor cursor = contentResolver.query(imageUri, projection, selection, selectionArgs, sortOrder);

        if (cursor == null) {
            return;
        }

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
            long dateAdded = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
            long dateModified = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
            String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));

            //添加数据到列表
            MediaImage mediaImage = new MediaImage(id, path, size, dateAdded, dateModified, displayName);

            data.add(mediaImage);

            List<MediaImage> mediaImages = dataMap.get(mediaImage.getCanonicalFolder());
            if (dataMap.containsKey(mediaImage.getCanonicalFolder()) && mediaImages != null) {
                mediaImages.add(mediaImage);
            } else {
                dataMap.put(mediaImage.getCanonicalFolder(), new ArrayList<>(Collections.singleton(mediaImage)));
            }
        }


        runOnUiThread(() -> onLoadImageRes(dataMap));
        cursor.close();
    }

    private void onLoadImageRes(final ArrayMap<String, List<MediaImage>> dataMap) {
        mLlHint.setVisibility(View.GONE);
        ListView lvCatalog = findViewById(R.id.lv_catalog);
        lvCatalog.setChoiceMode(CHOICE_MODE_SINGLE);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this,4));
        mAdapter = new ImageAdapter(this, 4);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setData(dataMap.get("all"));

        List<ImageCatalog> catalogs = ImageCatalog.obtain(dataMap);
        lvCatalog.setAdapter(new ArrayAdapter<>(this, R.layout.image_picker_item_catalog, R.id.ip_text_catalog, catalogs));

        lvCatalog.setOnItemClickListener((parent, view, position, id) -> {
            mAdapter.setData(dataMap.get(catalogs.get(position).getCanonicalFolder()));
            lvCatalog.setVisibility(View.GONE);
        });

        findViewById(R.id.view_test).setOnClickListener((View.OnClickListener) v -> lvCatalog.setVisibility(View.VISIBLE));

    }
}
