package com.github.gallery;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Created by ZhongXiaolong on 2021/3/8 10:25 AM.
 * <p>
 * 视频选择器
 */
public class ChoiceVideoActivity extends BaseActivity {

    private Handler mHandler;
    private VideoAdapter mAdapter;
    private int mMaxChoice;
    private Button mBtnComplete;
    private String mTag;
    private boolean mSendChoiceVideoReceiver;
    private SwipeRefreshLayout mRefreshLayout;
    private AlertDialog mPermissionDialog;//权限弹出框
    private boolean mLoadSuccess;
    private long mVideoMinSize;
    private long mVideoMaxSize;

    public static void start(ChoiceVideo choiceVideo) {
        Intent starter = new Intent(choiceVideo.getContext(), ChoiceVideoActivity.class);
        String tag = "tag" + System.currentTimeMillis();
        starter.putExtra("tag", tag);
        starter.putExtra("max_choice", choiceVideo.getMaxChoice());
        starter.putExtra("video_min", choiceVideo.getVideoMinSize());
        starter.putExtra("video_max", choiceVideo.getVideoMaxSize());
        choiceVideo.getContext().startActivity(starter);
        new ChoiceVideoReceiver(choiceVideo.getContext(), tag, choiceVideo.getOnChoiceVideoCallback()).register();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_activity_video);

        setSupportActionBar(findViewById(R.id.gallery_tool_bar));

        mHandler = new Handler(getMainLooper());

        mRefreshLayout = findViewById(R.id.gallery_refresh_layout);
        mRefreshLayout.setColorSchemeColors(getColorAccent());

        mRefreshLayout.setOnRefreshListener(this::getVideoData);

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            //没有权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            }
        }else{
            getVideoData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        getVideoData();
                    } else {
                        showPermissionDialog();
                    }
                    break;
                }
            }
        }
    }

    private void getVideoData() {
        mLoadSuccess = true;
        mVideoMinSize = getIntent().getLongExtra("video_min", 0);
        mVideoMaxSize = getIntent().getLongExtra("video_max", Integer.MAX_VALUE);
        Executors.newCachedThreadPool().submit(() -> {
            List<VideoData> data = new ArrayList<>();

            ContentResolver contentResolver = getContentResolver();
            Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {
                    MediaStore.Video.Media.DATA,
//上级目录           MediaStore.Video.Media.RELATIVE_PATH,
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.SIZE,
                    MediaStore.Video.Media.DATE_ADDED,
                    MediaStore.Video.Media.DATE_MODIFIED
            };
            //只查询视频
            String selection = "_size >30 and (mime_type = ? or mime_type = ? or mime_type =?) ";
            String[] selectionArgs = {"video/mp4", "video/mpg", "video/avi"};
            String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " desc ";
            Cursor cursor = contentResolver.query(videoUri, projection, selection, selectionArgs, sortOrder);

            if (cursor == null) {
                return;
            }


            while (cursor.moveToNext()) {
                //获取视频的路径
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));

                //添加
                data.add(new VideoData(size, path));
            }

            cursor.close();

            mHandler.post(() -> onGetVideoSuccess(data));
        });
    }

    private void onGetVideoSuccess(List<VideoData> data) {
        mRefreshLayout.setRefreshing(false);
        mRefreshLayout.setVisibility(View.VISIBLE);

        mTag = getIntent().getStringExtra("tag");
        mMaxChoice = getIntent().getIntExtra("max_choice", 1);
        mAdapter = new VideoAdapter().setOnCheckedChangeListener(new OnListCheckedChangeListener()).setOnItemClickListener(this::onItemClick);
        mAdapter.setData(data);
        RecyclerView recyclerView = findViewById(R.id.gallery_list);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setAdapter(mAdapter);

        mBtnComplete = findViewById(R.id.gallery_button_complete);

        mBtnComplete.setOnClickListener(this::onCompleteClick);
    }

    /**
     * 完成事件
     */
    private void onCompleteClick(View view) {
        List<String> paths = new ArrayList<>();
        for (VideoData checkedDatum : mAdapter.getCheckedData()) {
            paths.add(checkedDatum.getPath());
        }
        ChoiceVideoReceiver.post(ChoiceVideoActivity.this, mTag, paths);
        mSendChoiceVideoReceiver = true;
        finish();
    }

    /**
     * 列表点击事件
     */
    private void onItemClick(View view, int position, VideoData videoData) {
        GalleryVideoPlayerActivity.start(this, videoData.getPath());
    }

    /**
     * 列表选中变动
     */
    private class OnListCheckedChangeListener implements VideoAdapter.OnCheckedChangeListener {

        @Override
        public boolean onCheckedChangeBefore(View view, int position, boolean isChecked) {
            if (isChecked) {
                if (mAdapter.getCheckedSize() == mMaxChoice) {
                    Toast.makeText(ChoiceVideoActivity.this, getString(R.string.gallery_max_video, mMaxChoice), Toast.LENGTH_SHORT).show();
                    return false;
                }
                long length = mAdapter.getDatum(position).getLength();
                if (mVideoMaxSize > 0 && length > mVideoMaxSize) {
                    float m = (mVideoMaxSize / 1024.0f) / 1024.0f;
                    float g = m / 1024.0f;
                    if (g > 1) {
                        String msg = getString(R.string.gallery_video_size_max, new DecimalFormat("#0.00").format(g) + "G");
                        Toast.makeText(ChoiceVideoActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }else{
                        String msg = getString(R.string.gallery_video_size_max, new DecimalFormat("#0.00").format(m) + "M");
                        Toast.makeText(ChoiceVideoActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
                if (length < mVideoMinSize) {
                    float m = (mVideoMinSize / 1024.0f) / 1024.0f;
                    float g = m / 1024.0f;
                    if (g > 1) {
                        String msg = getString(R.string.gallery_video_size_min, new DecimalFormat("#0.00").format(g) + "G");
                        Toast.makeText(ChoiceVideoActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }else{
                        String msg = getString(R.string.gallery_video_size_min, new DecimalFormat("#0.00").format(m) + "M");
                        Toast.makeText(ChoiceVideoActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            }

            return true;
        }

        @Override
        public void onCheckedChangeAfter(View view, int position, boolean isChecked) {
            if (mAdapter.getCheckedSize() > 0) {
                mBtnComplete.setText(getString(R.string.gallery_catalog_complete, mAdapter.getCheckedSize(), mMaxChoice));
                mBtnComplete.setVisibility(View.VISIBLE);
            } else {
                mBtnComplete.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mPermissionDialog != null && mPermissionDialog.isShowing()) {
            int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission == PackageManager.PERMISSION_GRANTED) {
                //有权限
                getVideoData();
                dismissPermissionDialog();
            }
        }
    }

    /**
     * 显示提示权限弹出框
     */
    private void showPermissionDialog() {
        if (mPermissionDialog != null) {
            mPermissionDialog.show();
            return;
        }
        mPermissionDialog = new AlertDialog
                .Builder(this, R.style.Gallery_AlertDialogTheme)
                .setTitle(R.string.gallery_hint_tip)
                .setMessage(R.string.gallery_hint_need_sd_permission)
                .setCancelable(false)
                .setPositiveButton(R.string.gallery_go_setting, null)
                .setNegativeButton(R.string.gallery_exit, null)
                .show();
        mPermissionDialog.setCanceledOnTouchOutside(false);
        mPermissionDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#E84393"));
        mPermissionDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#999999"));
        mPermissionDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
                    mPermissionDialog.dismiss();
                }
            } else {
                Intent starter = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                String pkg = "com.android.settings";
                String cls = "com.android.settings.applications.InstalledAppDetails";
                starter.setComponent(new ComponentName(pkg, cls));
                starter.setData(Uri.parse("package:" + getPackageName()));
                startActivity(starter);
            }
        });
        mPermissionDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> {
            mPermissionDialog.dismiss();
            new Handler(getMainLooper()).postDelayed(this::finish, 300);
        });
    }

    private void dismissPermissionDialog() {
        if (mPermissionDialog != null && mPermissionDialog.isShowing()) {
            mPermissionDialog.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        if (!mSendChoiceVideoReceiver) {
            ChoiceVideoReceiver.post(ChoiceVideoActivity.this, mTag, new ArrayList<>());
        }
    }
}
