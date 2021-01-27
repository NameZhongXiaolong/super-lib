package com.github.gallery;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.view.ContextThemeWrapper;
import android.view.View;

import com.yalantis.ucrop.UCrop;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;

/**
 * Created by ZhongXiaolong on 2019/12/30 17:16.
 *
 * 图库选择,使用时只要把这个模块复制过去就能用了
 * 链式结构
 * new ChoiceGallery(this)
 *         .setMaxChoice(9)//最大选择数
 *         .setChoiceList(new ArrayList<String>())//已经选择图片
 *         .setCallback(OnChoiceGalleryCallback)//回调
 *         .start()//启动
 *
 * 选则图片页面{@link ChoiceGalleryActivity}
 * 图片预览界面{@link GalleryPreviewActivity}
 *
 * 图片多的情况下,查询图片速度会下降,可以采用预加载方式
 */
public class ChoiceGallery {

    private final Context mContext;
    private int maxChoice;
    private List<String> choiceList;
    private OnChoiceGalleryCallback onChoiceGalleryCallback;
    //单选裁剪
    private boolean cropWhiteSingle;
    private UCrop.Options uCropOptions;
    private boolean showCamera;

    public ChoiceGallery(Context context) {
        mContext = context;
    }

    public ChoiceGallery(Fragment fragment) {
        mContext = fragment.requireContext();
    }

    public ChoiceGallery(View view) {
        mContext = view.getContext();
    }

    public ChoiceGallery setMaxChoice(int maxChoice) {
        this.maxChoice = maxChoice;
        return this;
    }

    public ChoiceGallery setChoiceList(List<String> choiceList) {
        this.choiceList = choiceList;
        return this;
    }

    /**
     * 设置单选模式时候({@link #setMaxChoice}==1)裁剪
     */
    public ChoiceGallery setCropWhiteSingle() {
        this.cropWhiteSingle = true;
        return this;
    }

    /**
     * 设置单选模式时候,UCrop相关参数
     */
    public ChoiceGallery setCropWhiteSingle(UCrop.Options uCropOptions) {
        this.cropWhiteSingle = true;
        this.uCropOptions = uCropOptions;
        return this;
    }

    /**
     * 是否显示拍照按钮
     */
    public ChoiceGallery setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
        return this;
    }

    /**
     * 为方便使用,回调方式结合广播接收者,不用在onActivityResult()中回调,广播{@link ChoiceGalleryReceiver}
     */
    public ChoiceGallery setCallback(OnChoiceGalleryCallback onChoiceGalleryCallback) {
        this.onChoiceGalleryCallback = onChoiceGalleryCallback;
        return this;
    }

    public static UCrop.Options getDefCropOptions(Context context) {
        ChoiceGallerySetting choiceGallerySetting = new ChoiceGallerySetting(context);
        int theme = choiceGallerySetting.getTheme();
        TypedArray a = new ContextThemeWrapper(context, theme).obtainStyledAttributes(new int[]{R.attr.colorAccent, R.attr.colorPrimary, R.attr.colorPrimaryDark});
        int colorAccent = a.getColor(0, Color.parseColor("#333333"));
        int colorPrimary = a.getColor(1, Color.WHITE);
        int colorPrimaryDark = a.getColor(2, Color.WHITE);
        a.recycle();
        UCrop.Options options = new UCrop.Options();
        options.setStatusBarColor(colorPrimaryDark);
        options.setToolbarColor(colorPrimary);
        options.setActiveControlsWidgetColor(colorAccent);
        options.setFreeStyleCropEnabled(false);
        options.withAspectRatio(1, 1);
        return options;
    }

    Context getContext() {
        return mContext;
    }

    int getMaxChoice() {
        return maxChoice;
    }

    List<String> getChoiceList() {
        return choiceList;
    }

    OnChoiceGalleryCallback getCallback() {
        return onChoiceGalleryCallback;
    }

    boolean isCropWhiteSingle() {
        return cropWhiteSingle;
    }

    boolean isShowCamera() {
        return showCamera;
    }

    UCrop.Options getCropOptions() {
        return uCropOptions;
    }

    public void start() {
        if (choiceList == null) {
            choiceList = new ArrayList<>();
        }
        if (maxChoice <= 0) {
            maxChoice = cropWhiteSingle ? 1 : 9;
        }
        ChoiceGalleryActivity.start(this);
    }
}
