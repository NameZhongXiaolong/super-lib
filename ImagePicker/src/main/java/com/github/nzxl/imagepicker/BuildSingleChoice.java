package com.github.nzxl.imagepicker;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;

/**
 * Created by ZhongXiaolong on 2021/05/31 11:24 AM.
 */
public class BuildSingleChoice {

    private final ImagePicker mImagePicker;
    private final Bundle mArgs;
    private SingleListener mSingleListener;
    private UCrop.Options mUCropOptions;

    public BuildSingleChoice(ImagePicker imagePicker) {
        mImagePicker = imagePicker;
        mArgs = new Bundle();
    }

    public BuildSingleChoice setCrop(){
        mArgs.putBoolean(ArgKey.CROP, true);
        return this;
    }

    /**
     * 设置单选模式时候,UCrop相关参数
     */
    public BuildSingleChoice setCrop(UCrop.Options uCropOptions) {
        mUCropOptions = uCropOptions;
        mArgs.putBoolean(ArgKey.CROP, true);
        return this;
    }

    public BuildSingleChoice setListener(SingleListener singleListener) {
        mSingleListener = singleListener;
        return this;
    }

    public void execute(){
        mImagePicker.executeSingle(mArgs,mSingleListener);
    }
}
