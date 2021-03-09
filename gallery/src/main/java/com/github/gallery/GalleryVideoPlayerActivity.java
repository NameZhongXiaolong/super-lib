package com.github.gallery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by ZhongXiaolong on 2021/3/8 3:15 PM.
 * <p>
 * 视频播放
 */
public class GalleryVideoPlayerActivity extends BaseActivity {

    private VideoView mVideoView;

    static void start(Context context, String path) {
        Intent starter = new Intent(context, GalleryVideoPlayerActivity.class);
        starter.putExtra("path", path);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_activity_video_player);

        mVideoView = findViewById(R.id.gallery_video_view);

        final String path = getIntent().getStringExtra("path");

        mVideoView.setVideoPath(path);

        //创建MediaController对象
        MediaController mediaController = new MediaController(this);

        //VideoView与MediaController建立关联
        mVideoView.setMediaController(mediaController);

        //让VideoView获取焦点
        mVideoView.requestFocus();

        mVideoView.start();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.pause();
    }
}
