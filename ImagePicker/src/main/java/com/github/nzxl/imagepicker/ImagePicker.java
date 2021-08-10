package com.github.nzxl.imagepicker;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 * Created by ZhongXiaolong on 2021/05/31 11:14 AM.
 * <p>
 * 图片选取器
 */
public class ImagePicker {

    private FragmentActivity mActivity;
    private Fragment mFragment;

    private ImagePicker() {}

    public static ImagePicker with(FragmentActivity fragmentActivity) {
        ImagePicker imagePicker = new ImagePicker();
        imagePicker.mActivity = fragmentActivity;
        return imagePicker;
    }

    public static ImagePicker with(Fragment fragment) {
        ImagePicker imagePicker = new ImagePicker();
        imagePicker.mFragment = fragment;
        return imagePicker;
    }

    public BuildSingleChoice single() {
        return new BuildSingleChoice(this);
    }

    public BuildMultipleChoice multiple(int maxChoice) {
        return new BuildMultipleChoice(this, maxChoice);
    }

    /**
     * 执行单选
     */
    void executeSingle(Bundle arg, SingleListener listener) {
        mActivity.startActivity(new Intent(mActivity, ImagePickerActivity.class));
    }
}
