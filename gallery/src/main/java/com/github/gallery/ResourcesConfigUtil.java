package com.github.gallery;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;

import java.util.Locale;
import java.util.Objects;

/**
 * Created by ZhongXiaolong on 2021/8/17 5:13 下午.
 * <p>
 * 资源文件{@link Resources} 配置
 */
class ResourcesConfigUtil {

    private ResourcesConfigUtil() {}

    public static Resources create(Context context, Resources resources) {
        //设置sp不受系统字体调整影响
        if (resources != null) {
            Configuration configuration = resources.getConfiguration();
            if (configuration != null) {
                boolean isChange = false;

                //固定字体大小,不受系统字体缩放影响
                if (configuration.fontScale != 1.0f) {
                    configuration.fontScale = 1.0f;
                    isChange = true;
                }

                //获取配置的语言环境
                String language = new ChoiceGallerySetting(context).getLanguage();

                //判断是否要设置固定的语言环境
                if (!TextUtils.isEmpty(language)) {
                    Locale oldLocale = configuration.locale;
                    Locale newLocale = null;
                    if (ChoiceGallerySetting.LANGUAGE_ZH_TW.equalsIgnoreCase(language)) newLocale = Locale.TAIWAN;
                    else if (ChoiceGallerySetting.LANGUAGE_ZH_CN.equalsIgnoreCase(language)) newLocale = Locale.CHINA;
                    else if (ChoiceGallerySetting.LANGUAGE_EN.equalsIgnoreCase(language)) newLocale = Locale.ENGLISH;

                    if (newLocale != null && unequals(oldLocale, newLocale)) {
                        configuration.setLocale(newLocale);
                        isChange = true;
                    }
                }

                if (isChange) {
                    resources.updateConfiguration(configuration, resources.getDisplayMetrics());
                }
            }
        }
        return resources;
    }

    /**
     * 比较两个对象是否不相同
     */
    private static <T> boolean unequals(T arg1, T arg2) {
        return !Objects.equals(arg1, arg2);
    }
}
