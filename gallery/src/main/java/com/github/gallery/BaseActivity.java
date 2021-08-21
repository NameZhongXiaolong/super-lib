package com.github.gallery;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;

import java.util.Locale;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Created by ZhongXiaolong on 2020/12/30 2:38 PM.
 */
class BaseActivity extends AppCompatActivity {

    private int mColorAccent;
    private int mColorPrimary;
    private int mColorPrimaryDark;
    private int mBackIcon;
    private Resources mResources;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ChoiceGallerySetting choiceGallerySetting = new ChoiceGallerySetting(this);
        mBackIcon = choiceGallerySetting.getNavigationIcon();
        int theme = choiceGallerySetting.getTheme();

        setTheme(theme);

        TypedArray a = getTheme().obtainStyledAttributes(new int[]{R.attr.colorAccent, R.attr.colorPrimary, R.attr.colorPrimaryDark});
        mColorAccent = a.getColor(0, Color.parseColor("#333333"));
        mColorPrimary = a.getColor(1, Color.WHITE);
        mColorPrimaryDark = a.getColor(2, Color.WHITE);
        a.recycle();
    }

    @Override
    public Resources getResources() {
        if (mResources == null) {
            mResources = ResourcesConfigUtil.create(getBaseContext(), super.getResources());
        }
        return mResources;
    }

    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        if (toolbar != null) {
            toolbar.setNavigationIcon(mBackIcon);
        }
        super.setSupportActionBar(toolbar);
    }

    public int getColorAccent() {
        return mColorAccent;
    }

    public int getColorPrimary() {
        return mColorPrimary;
    }

    public int getColorPrimaryDark() {
        return mColorPrimaryDark;
    }
}
