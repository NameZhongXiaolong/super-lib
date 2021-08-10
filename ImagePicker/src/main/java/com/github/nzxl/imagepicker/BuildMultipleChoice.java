package com.github.nzxl.imagepicker;

import android.os.Bundle;

/**
 * Created by ZhongXiaolong on 2021/05/31 11:23 AM.
 */
public class BuildMultipleChoice {

    private final ImagePicker mImagePicker;
    private final Bundle mArgs;

    public BuildMultipleChoice(ImagePicker imagePicker, int maxChoice) {
        mImagePicker = imagePicker;
        mArgs = new Bundle();
        mArgs.putInt(ArgKey.MAX_CHOICE, Math.max(maxChoice, 1));
    }

    public ImagePicker setListener(MultipleListener l) {
        return mImagePicker;
    }

    public ImagePicker setPathListener(MultiplePathListener l) {
        return mImagePicker;
    }

    public ImagePicker setUriListener(MultipleUriListener l) {
        return mImagePicker;
    }
}
