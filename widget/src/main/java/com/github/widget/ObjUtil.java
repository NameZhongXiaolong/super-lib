package com.github.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;

/**
 * Created by ZhongXiaolong on 2021/9/23 3:52 下午.
 */
class ObjUtil {

    private ObjUtil() {}

    @SafeVarargs
    public static <T> T firstNotNull(final T... objects) {
        for (T object : objects) {
            if (object != null) {
                return object;
            }
        }
        return null;
    }

    /**
     * string根据逗号(,)截取int集合
     */
    public static int[] subInt(String str) {
        List<Integer> list = new ArrayList<>();
        if (!TextUtils.isEmpty(str)) {
            int index;
            while ((index = str.indexOf(",")) > 0) {
                try {
                    String colorString = str.substring(0, index).trim();
                    list.add(Color.parseColor(colorString));
                    str = str.substring(index + 1);
                } catch (Exception e) {
                    str = str.substring(index + 1);
                }
            }

            try {
                list.add(Color.parseColor(str));
            } catch (Exception e) {
                Log.d("SuperButton", "e:" + e);
            }
        }

        int[] arr = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }

        return arr;
    }

    public static int parseInt(String intValue) {
        try {
            return Integer.parseInt(intValue);
        } catch (Exception e) {
            return 0;
        }
    }

    public static float parseFloat(String intValue) {
        try {
            return Float.parseFloat(intValue);
        } catch (Exception e) {
            return 0;
        }
    }
}
