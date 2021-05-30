package com.github.gallery;

import android.content.Context;

/**
 * Created by ZhongXiaolong on 2021/3/8 10:22 AM.
 * <p>
 * 选择视频
 */
public class ChoiceVideo {

    private final Context mContext;
    private int maxChoice;
    private OnMediaVideoCallback onMediaVideoCallback;
    private OnVideoUriCallback onVideoUriCallback;
    private OnVideoPathCallback onVideoPathCallback;
    private long mVideoMaxSize;
    private long mVideoMinSize;

    public ChoiceVideo(Context context) {
        mContext = context;
        mVideoMinSize = 30;
    }

    public ChoiceVideo setMaxChoice(int maxChoice) {
        this.maxChoice = maxChoice;
        return this;
    }

    /**
     * 为方便使用,回调方式结合广播接收者,不用在onActivityResult()中回调,广播{@link ChoiceGalleryReceiver}
     * 设置地址回调,注意android10以上没有设置文件管理权限不可以直接获取file
     * 因此更推荐使用{@link #setOnUriCallback(com.github.gallery.OnVideoUriCallback)}{@link #setOnMediaVideoCallback(com.github.gallery.OnMediaVideoCallback)}
     */
    public ChoiceVideo setOnPathCallback(OnVideoPathCallback onVideoPathCallback) {
        this.onVideoPathCallback = onVideoPathCallback;
        return this;
    }


    /**
     * 为方便使用,回调方式结合广播接收者,不用在onActivityResult()中回调,广播{@link ChoiceGalleryReceiver}
     * 文件Uri回调
     */
    public ChoiceVideo setOnUriCallback(OnVideoUriCallback onVideoUriCallback) {
        this.onVideoUriCallback = onVideoUriCallback;
        return this;
    }

    /**
     * 为方便使用,回调方式结合广播接收者,不用在onActivityResult()中回调,广播{@link ChoiceGalleryReceiver}
     * 文件信息回调
     */
    public ChoiceVideo setOnMediaVideoCallback(OnMediaVideoCallback onMediaVideoCallback) {
        this.onMediaVideoCallback = onMediaVideoCallback;
        return this;
    }

    /**
     * 兼容旧的api
     */
    @Deprecated
    public ChoiceVideo setCallback(OnMediaVideoCallback onMediaVideoCallback) {
        this.onMediaVideoCallback = onMediaVideoCallback;
        return this;
    }

    /**
     * 设置视频的大小
     */
    public ChoiceVideo setVideoSize(int min, int max) {
        mVideoMinSize = min;
        mVideoMaxSize = max;
        return this;
    }

    Context getContext() {
        return mContext;
    }

    int getMaxChoice() {
        return maxChoice;
    }

    long getVideoMaxSize() {
        return mVideoMaxSize;
    }

    long getVideoMinSize() {
        return mVideoMinSize;
    }

    OnMediaVideoCallback getOnMediaVideoCallback() {
        return onMediaVideoCallback;
    }

    OnVideoUriCallback getOnVideoUriCallback() {
        return onVideoUriCallback;
    }

    OnVideoPathCallback getOnVideoPathCallback() {
        return onVideoPathCallback;
    }

    public void start() {
        if (maxChoice <= 0) {
            maxChoice = 1;
        }
        ChoiceVideoActivity.start(this);
    }
}
