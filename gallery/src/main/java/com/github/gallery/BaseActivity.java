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

/**
 * Created by ZhongXiaolong on 2020/12/30 2:38 PM.
 */
class BaseActivity extends AppCompatActivity {

    private int mColorAccent;
    private String mLanguage;
    private int mColorPrimary;
    private int mColorPrimaryDark;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ChoiceGallerySetting choiceGallerySetting = new ChoiceGallerySetting(this);
        int theme = choiceGallerySetting.getTheme();
        mLanguage = choiceGallerySetting.getLanguage();

        setTheme(theme);

        TypedArray a = getTheme().obtainStyledAttributes(new int[]{R.attr.colorAccent, R.attr.colorPrimary, R.attr.colorPrimaryDark});
        mColorAccent = a.getColor(0, Color.parseColor("#333333"));
        mColorPrimary = a.getColor(1, Color.WHITE);
        mColorPrimaryDark = a.getColor(2, Color.WHITE);
        a.recycle();
    }

    @Override
    public Resources getResources() {
        Resources resources = super.getResources();
        if (resources != null) {
            Configuration configuration = resources.getConfiguration();
            if (configuration != null) {
                Locale defLocale = configuration.locale;

                //简体中文
                if (ChoiceGallerySetting.LANGUAGE_ZH_CN.equals(mLanguage)) {
                    if (!Objects.equals("zh",defLocale.getLanguage()) || !Objects.equals("CN",defLocale.getCountry())) {
                        configuration.setLocale(Locale.SIMPLIFIED_CHINESE);
                        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
                    }
                }

                //繁体中文
                if (ChoiceGallerySetting.LANGUAGE_ZH_TW.equals(mLanguage)) {
                    if (!Objects.equals("zh",defLocale.getLanguage()) || !Objects.equals("TW",defLocale.getCountry())) {
                        configuration.setLocale(Locale.TRADITIONAL_CHINESE);
                        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
                    }
                }

                //英文
                if (ChoiceGallerySetting.LANGUAGE_EN.equals(mLanguage)) {
                    if (!Objects.equals("en", defLocale.getLanguage())) {
                        configuration.setLocale(Locale.ENGLISH);
                        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
                    }
                }
            }
        }
        return resources;
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
