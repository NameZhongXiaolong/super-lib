package com.github.gallery;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

    interface Code {
        int REQUEST_CODE = 63;
        int RESULT_OK = 167;
    }

    private VideoView mVideoView;

    static void start(Activity activity, MediaVideo mediaVideo, boolean showCheckButton, CharSequence buttonText) {
        Intent intent = new Intent(activity, GalleryVideoPlayerActivity.class);
        intent.putExtra("video_info", mediaVideo);
        intent.putExtra("show_button", showCheckButton);
        intent.putExtra("button_text", buttonText);
        activity.startActivityForResult(intent, Code.REQUEST_CODE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_activity_video_player);

        setSupportActionBar(findViewById(R.id.gallery_tool_bar));

        mVideoView = findViewById(R.id.gallery_video_view);
        Button btnComplete = findViewById(R.id.gallery_button_complete);

        final MediaVideo mediaVideo = getIntent().getParcelableExtra("video_info");
        final boolean showButton = getIntent().getBooleanExtra("show_button", false);
        final String buttonText = getIntent().getStringExtra("button_text");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Uri uri = mediaVideo.getUri();
            if (uri != null) {
                mVideoView.setVideoURI(uri);
            }
        } else {
            mVideoView.setVideoPath(mediaVideo.getAbsolutePath());
        }

        //创建MediaController对象
        MediaController mediaController = new MediaController(this);

        //VideoView与MediaController建立关联
        mVideoView.setMediaController(mediaController);

        //让VideoView获取焦点
        mVideoView.requestFocus();

        mVideoView.start();

        mVideoView.postDelayed(mediaController::show, 500);

        btnComplete.setVisibility(showButton ? View.VISIBLE : View.GONE);
        btnComplete.setText(buttonText);
        btnComplete.setOnClickListener(v -> {
            Intent data = new Intent();
            data.putExtra("video_info", mediaVideo);
            setResult(Code.RESULT_OK, data);
            finish();
        });
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
