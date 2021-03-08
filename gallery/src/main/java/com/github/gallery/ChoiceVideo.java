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
    private OnChoiceVideoCallback mOnChoiceVideoCallback;
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

    public ChoiceVideo setCallback(OnChoiceVideoCallback onChoiceVideoCallback) {
        mOnChoiceVideoCallback = onChoiceVideoCallback;
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

    OnChoiceVideoCallback getOnChoiceVideoCallback() {
        return mOnChoiceVideoCallback;
    }

    public void start() {
        if (maxChoice <= 0) {
            maxChoice = 1;
        }
        ChoiceVideoActivity.start(this);
    }
}
