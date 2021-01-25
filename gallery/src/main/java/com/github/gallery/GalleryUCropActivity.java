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

    private String mLanguage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ChoiceGallerySetting choiceGallerySetting = new ChoiceGallerySetting(this);
        int theme = choiceGallerySetting.getTheme();
        mLanguage = choiceGallerySetting.getLanguage();

        setTheme(theme);
        super.onCreate(savedInstanceState);
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
                    configuration.setLocale(Locale.ENGLISH);
                    if (!Objects.equals("en", defLocale.getLanguage())) {
                        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
                    }
                }
            }
        }
        return resources;
    }

}
