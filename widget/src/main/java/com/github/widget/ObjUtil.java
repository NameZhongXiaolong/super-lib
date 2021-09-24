package com.github.widget;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

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

    public static GradientDrawable.Orientation int2Orientation(int orientation) {
        if (orientation == 1) return GradientDrawable.Orientation.TOP_BOTTOM;
        else if (orientation == 2) return GradientDrawable.Orientation.TR_BL;
        else if (orientation == 3) return GradientDrawable.Orientation.RIGHT_LEFT;
        else if (orientation == 4) return GradientDrawable.Orientation.BR_TL;
        else if (orientation == 5) return GradientDrawable.Orientation.BOTTOM_TOP;
        else if (orientation == 6) return GradientDrawable.Orientation.BL_TR;
        else if (orientation == 7) return GradientDrawable.Orientation.LEFT_RIGHT;
        else return GradientDrawable.Orientation.TL_BR;
    }
}
