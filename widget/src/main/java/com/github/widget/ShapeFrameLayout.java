package com.github.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by ZhongXiaolong on 2021/3/26 9:53 AM.
 * <p>
 * 1.添加背景半圆和设置圆角,与app:backgroundColor一起使用生效:app:backgroundArc="true"--左右半圆; android:radius="@dimen/dp_10"--设置圆角
 * 2.添加选择器,点击效果/不可点击效果,描边
 * *  * <!--背景颜色-->
 * *  * <attr name="backgroundColor" />
 * *  * <!--不可点击时的背景颜色-->
 * *  * <attr name="backgroundColorEnabled" />
 * *  * <!--点击时的背景颜色-->
 * *  * <attr name="backgroundColorPressed" />
 * *  * <!--选中时的背景颜色-->
 * *  * <attr name="backgroundColorChecked" />
 * *  * <!--背景描边宽度-->
 * *  * <attr name="strokeWidth" />
 * *  * <!--背景描边颜色-->
 * *  * <attr name="strokeColor" />
 * *  * <!--背景描边不可点击时的颜色-->
 * *  * <attr name="strokeColorEnabled" />
 * *  * <!--背景描边点击时的颜色-->
 * *  * <attr name="strokeColorPressed" />
 * *  * <!--背景描边选中时的颜色-->
 * *  * <attr name="strokeColorChecked" />
 */
public class ShapeFrameLayout extends FrameLayout {

    private final StateListDrawableHelper mStateListDrawableHelper;

    public ShapeFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public ShapeFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShapeFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ShapeFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShapeFrameLayout);

        boolean backgroundArc = a.getBoolean(R.styleable.ShapeFrameLayout_backgroundArc, false);
        float radius = a.getDimension(R.styleable.ShapeFrameLayout_android_radius, 0);
        float topLeftRadius = a.getDimension(R.styleable.ShapeFrameLayout_android_topLeftRadius, 0);
        float topRightRadius = a.getDimension(R.styleable.ShapeFrameLayout_android_topRightRadius, 0);
        float bottomLeftRadius = a.getDimension(R.styleable.ShapeFrameLayout_android_bottomLeftRadius, 0);
        float bottomRightRadius = a.getDimension(R.styleable.ShapeFrameLayout_android_bottomRightRadius, 0);

        int backgroundColor = a.getColor(R.styleable.ShapeFrameLayout_backgroundColor, Color.TRANSPARENT);
        int backgroundColorEnabled = a.getColor(R.styleable.ShapeFrameLayout_backgroundColorEnabled, StateListDrawableHelper.DEF_COLOR);
        int backgroundColorPressed = a.getColor(R.styleable.ShapeFrameLayout_backgroundColorPressed, StateListDrawableHelper.DEF_COLOR);
        int backgroundColorChecked = a.getColor(R.styleable.ShapeFrameLayout_backgroundColorChecked, StateListDrawableHelper.DEF_COLOR);
        int strokeWidth = a.getDimensionPixelOffset(R.styleable.ShapeFrameLayout_strokeWidth, 0);
        int strokeColor = a.getColor(R.styleable.ShapeFrameLayout_strokeColor, Color.TRANSPARENT);
        int strokeColorEnabled = a.getColor(R.styleable.ShapeFrameLayout_strokeColorEnabled, strokeColor);
        int strokeColorPressed = a.getColor(R.styleable.ShapeFrameLayout_strokeColorPressed, strokeColor);
        int strokeColorChecked = a.getColor(R.styleable.ShapeFrameLayout_strokeColorChecked, strokeColor);
        String backgroundColorsString = a.getString(R.styleable.ShapeFrameLayout_backgroundColors);
        int backgroundOrientation = a.getInt(R.styleable.ShapeFrameLayout_backgroundOrientation, 0);

        a.recycle();

        //渐变的颜色数值
        int[] backgroundColors = ObjUtil.subInt(backgroundColorsString);
        GradientDrawable.Orientation orientation = ObjUtil.int2Orientation(backgroundOrientation);

        mStateListDrawableHelper = new StateListDrawableHelper(backgroundArc, radius, topLeftRadius, topRightRadius,
                bottomLeftRadius, bottomRightRadius, backgroundColor, backgroundColorEnabled, backgroundColorPressed, backgroundColorChecked,
                strokeWidth, strokeColor, strokeColorEnabled, strokeColorPressed, strokeColorChecked, orientation, backgroundColors);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        setBackground(mStateListDrawableHelper.getStateListDrawable(h));
    }
}
