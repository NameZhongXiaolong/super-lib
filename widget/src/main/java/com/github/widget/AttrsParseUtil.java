package com.github.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.core.content.ContextCompat;

/**
 * Created by ZhongXiaolong on 2021/10/8 2:25 下午.
 * <p>
 * 属性解析工具类
 */
class AttrsParseUtil {

    public final static int DEF_COLOR = -999;

    private AttrsParseUtil() {}

    public static int getDimensionPixelOffset(Context context, AttributeSet attrs, int index) {
        String value = attrs.getAttributeValue(index);
        if (value.startsWith("@")) {
            try {
                return context.getResources().getDimensionPixelOffset(attrs.getAttributeResourceValue(index, 0));
            } catch (Exception e) {
                return attrs.getAttributeIntValue(index, 0);
            }
        } else {
            if (value.endsWith("dip")) {
                float arg = ObjUtil.parseFloat(value.substring(0, value.indexOf("dip")));
                return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, arg, context.getResources().getDisplayMetrics());
            } else if (value.endsWith("px")) {
                float arg = ObjUtil.parseFloat(value.substring(0, value.indexOf("px")));
                return (int) arg;
            } else if (value.endsWith("sp")) {
                float arg = ObjUtil.parseFloat(value.substring(0, value.indexOf("dip")));
                return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, arg, context.getResources().getDisplayMetrics());
            } else if (value.endsWith("pt")) {
                float arg = ObjUtil.parseFloat(value.substring(0, value.indexOf("pt")));
                return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, arg, context.getResources().getDisplayMetrics());
            } else if (value.endsWith("in")) {
                float arg = ObjUtil.parseFloat(value.substring(0, value.indexOf("in")));
                return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, arg, context.getResources().getDisplayMetrics());
            } else if (value.endsWith("mm")) {
                float arg = ObjUtil.parseFloat(value.substring(0, value.indexOf("mm")));
                return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, arg, context.getResources().getDisplayMetrics());
            }
            return attrs.getAttributeIntValue(index, 0);
        }
    }

    public static int getColor(Context context, AttributeSet attrs, int index) {
        String value = attrs.getAttributeValue(index);
        if (value.startsWith("@")) {
            try {
                int resColor = attrs.getAttributeResourceValue(index, 0);
                return resColor == 0 ? DEF_COLOR : ContextCompat.getColor(context, resColor);
            } catch (Exception e) {
                return attrs.getAttributeIntValue(index, DEF_COLOR);
            }
        } else {
            return attrs.getAttributeIntValue(index, DEF_COLOR);
        }
    }
}
