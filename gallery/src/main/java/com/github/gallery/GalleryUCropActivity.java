package com.github.gallery;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import com.yalantis.ucrop.UCropActivity;

import java.util.Locale;
import java.util.Objects;

/**
 * Created by ZhongXiaolong on 2021/1/25 1:15 PM.
 */
public class GalleryUCropActivity extends UCropActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ChoiceGallerySetting choiceGallerySetting = new ChoiceGallerySetting(this);
        int theme = choiceGallerySetting.getTheme();

        setTheme(theme);
        super.onCreate(savedInstanceState);
    }

    private Resources mResources;

    @Override
    public Resources getResources() {
        if (mResources == null) {
            mResources = ResourcesConfigUtil.create(getBaseContext(), super.getResources());
        }
        return mResources;
    }

}
