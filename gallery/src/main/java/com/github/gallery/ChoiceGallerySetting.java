package com.github.gallery;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.DrawableRes;

/**
 * Created by ZhongXiaolong on 2020/12/29 8:42 PM.
 * <p>
 * 设置
 */
public class ChoiceGallerySetting {

    public static final String LANGUAGE_ZH_CN = "zh_CN";
    public static final String LANGUAGE_ZH_TW = "zh_TW";
    public static final String LANGUAGE_EN = "en";

    private static final String SP_NAME = "ChoiceGallerySetting";
    private final SharedPreferences mSharedPrefs;
    private String mLanguage;
    private int mTheme = -1;
    private int mNavigationIcon = R.drawable.gallery_ic_back;

    public ChoiceGallerySetting(Context context) {
        mSharedPrefs = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public ChoiceGallerySetting setLanguage(String language) {
        mLanguage = language;
        return this;
    }

    public ChoiceGallerySetting setMilaChatTheme() {
        mTheme = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? R.style.Gallery_AppTheme_MilaChat_Normal : R.style.Gallery_AppTheme_MilaChat_M;
        return this;
    }

    public ChoiceGallerySetting setMeStoreTheme() {
        mTheme = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? R.style.Gallery_AppTheme_MeStore_Normal : R.style.Gallery_AppTheme_MeStore_M;
        return this;
    }

    public ChoiceGallerySetting setNavigationIcon(@DrawableRes int drawable) {
        mNavigationIcon = drawable;
        return this;
    }

    public void build(){
        mSharedPrefs.edit().clear().apply();
        if (!TextUtils.isEmpty(mLanguage)) {
            mSharedPrefs.edit().putString("language", mLanguage).apply();
        }
        if (mTheme != -1) {
            mSharedPrefs.edit().putInt("theme", mTheme).apply();
        }
        mSharedPrefs.edit().putInt("back_icon", mNavigationIcon).apply();
    }

    String getLanguage() {
        return mSharedPrefs.getString("language", "");
    }

    int getTheme() {
        int defValue = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? R.style.Gallery_AppTheme_Light_StatusBar : R.style.Gallery_AppTheme_Light;
        return mSharedPrefs.getInt("theme", defValue);
    }

    int getNavigationIcon(){
        return mSharedPrefs.getInt("back_icon", R.drawable.gallery_ic_back);
    }
}
