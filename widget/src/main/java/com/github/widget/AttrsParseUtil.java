package com.github.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                return 0;
            }
        } else {
            if (value.endsWith("dp")) {
                float arg = ObjUtil.parseFloat(value.substring(0, value.indexOf("dp")));
                return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, arg, context.getResources().getDisplayMetrics());
            } else if (value.endsWith("dip")) {
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
            } else {
                //提取数字
                String regEx = "[^0-9]";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(value);
                return ObjUtil.parseInt(m.replaceAll("").trim());
            }
        }
    }

    public static int getColor(Context context, AttributeSet attrs, int index) {
        String value = attrs.getAttributeValue(index);
        if (value.startsWith("@")) {
            try {
                int resColor = attrs.getAttributeResourceValue(index, 0);
                return resColor == 0 ? DEF_COLOR : ContextCompat.getColor(context, resColor);
            } catch (Exception e) {
                return DEF_COLOR;
            }
        } else {
            return attrs.getAttributeIntValue(index, DEF_COLOR);
        }
    }
}
