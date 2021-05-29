package com.github.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

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
 * *  * <!--背景描边宽度-->
 * *  * <attr name="strokeWidth" />
 * *  * <!--背景描边颜色-->
 * *  * <attr name="strokeColor" />
 * *  * <!--背景描边不可点击时的颜色-->
 * *  * <attr name="strokeColorEnabled" />
 * *  * <!--背景描边点击时的颜色-->
 * *  * <attr name="strokeColorPressed" />
 */
public class ShapeFrameLayout extends FrameLayout {

    private final boolean mBackgroundArc;
    private final float mRadius;
    private final float mTopLeftRadius;
    private final float mTopRightRadius;
    private final float mBottomLeftRadius;
    private final float mBottomRightRadius;
    private final int mBackgroundColor;
    private final int mBackgroundColorEnabled;
    private final int mBackgroundColorPressed;
    private final int mStrokeWidth;
    private final int mStrokeColor;
    private final int mStrokeColorEnabled;
    private final int mStrokeColorPressed;

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

        mBackgroundArc = a.getBoolean(R.styleable.ShapeFrameLayout_backgroundArc, false);
        mRadius = a.getDimension(R.styleable.ShapeFrameLayout_android_radius, 0);
        mTopLeftRadius = a.getDimension(R.styleable.ShapeFrameLayout_android_topLeftRadius, 0);
        mTopRightRadius = a.getDimension(R.styleable.ShapeFrameLayout_android_topRightRadius, 0);
        mBottomLeftRadius = a.getDimension(R.styleable.ShapeFrameLayout_android_bottomLeftRadius, 0);
        mBottomRightRadius = a.getDimension(R.styleable.ShapeFrameLayout_android_bottomRightRadius, 0);

        mBackgroundColor = a.getColor(R.styleable.ShapeFrameLayout_backgroundColor, Color.TRANSPARENT);
        mBackgroundColorEnabled = a.getColor(R.styleable.ShapeFrameLayout_backgroundColorEnabled, mBackgroundColor);
        mBackgroundColorPressed = a.getColor(R.styleable.ShapeFrameLayout_backgroundColorPressed, mBackgroundColor);
        mStrokeWidth = a.getDimensionPixelOffset(R.styleable.ShapeFrameLayout_strokeWidth, 0);
        mStrokeColor = a.getColor(R.styleable.ShapeFrameLayout_strokeColor, Color.TRANSPARENT);
        mStrokeColorEnabled = a.getColor(R.styleable.ShapeFrameLayout_strokeColorEnabled, mStrokeColor);
        mStrokeColorPressed = a.getColor(R.styleable.ShapeFrameLayout_strokeColorPressed, mStrokeColor);

        a.recycle();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        if (mBackgroundColor == Color.TRANSPARENT && mBackgroundColorPressed == Color.TRANSPARENT && mBackgroundColorEnabled == Color.TRANSPARENT && mStrokeColor == Color.TRANSPARENT && mStrokeColorPressed == Color.TRANSPARENT && mStrokeColorEnabled == Color.TRANSPARENT) {
            return;
        }

        if (mBackgroundArc) {
            StateListDrawable stateListDrawable = new StateListDrawable();

            //点击状态
            GradientDrawable pressedDrawable = getGradientDrawable(mBackgroundColorPressed, h * 0.5f, null, mStrokeWidth, mStrokeColorPressed);
            stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);

            //不可点击状态
            GradientDrawable enabledDrawable = getGradientDrawable(mBackgroundColorEnabled, h * 0.5f, null, mStrokeWidth, mStrokeColorEnabled);
            stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, enabledDrawable);

            //默认背景要放最后
            GradientDrawable normalDrawable = getGradientDrawable(mBackgroundColor, h * 0.5f, null, mStrokeWidth, mStrokeColor);
            stateListDrawable.addState(new int[]{}, normalDrawable);

            setBackground(stateListDrawable);
            return;
        }

        if (mBottomLeftRadius > 0 || mBottomRightRadius > 0 || mTopLeftRadius > 0 || mTopRightRadius > 0) {
            float[] radii = {mTopLeftRadius, mTopLeftRadius, mTopRightRadius, mTopRightRadius,
                    mBottomRightRadius, mBottomRightRadius, mBottomLeftRadius, mBottomLeftRadius};

            StateListDrawable stateListDrawable = new StateListDrawable();

            //点击状态
            GradientDrawable pressedDrawable = getGradientDrawable(mBackgroundColorPressed, 0, radii, mStrokeWidth, mStrokeColorPressed);
            stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);

            //不可点击状态
            GradientDrawable enabledDrawable = getGradientDrawable(mBackgroundColorEnabled, 0, radii, mStrokeWidth, mStrokeColorEnabled);
            stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, enabledDrawable);

            //默认背景要放最后
            GradientDrawable normalDrawable = getGradientDrawable(mBackgroundColor, 0, radii, mStrokeWidth, mStrokeColor);
            stateListDrawable.addState(new int[]{}, normalDrawable);

            setBackground(stateListDrawable);

            return;
        }

        StateListDrawable stateListDrawable = new StateListDrawable();

        //点击状态
        GradientDrawable pressedDrawable = getGradientDrawable(mBackgroundColorPressed, mRadius, null, mStrokeWidth, mStrokeColorPressed);
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);

        //不可点击状态
        GradientDrawable enabledDrawable = getGradientDrawable(mBackgroundColorEnabled, mRadius, null, mStrokeWidth, mStrokeColorEnabled);
        stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, enabledDrawable);

        //默认背景要放最后
        GradientDrawable normalDrawable = getGradientDrawable(mBackgroundColor, mRadius, null, mStrokeWidth, mStrokeColor);
        stateListDrawable.addState(new int[]{}, normalDrawable);

        setBackground(stateListDrawable);
    }

    @SafeVarargs
    private final <T> T firstNotNull(final T... objects) {
        for (T object : objects) {
            if (object != null) {
                return object;
            }
        }
        return null;
    }

    private GradientDrawable getGradientDrawable(int color, float radius, float[] radii, int strokeWidth, int strokeColor) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(color);
        if (radius > 0) {
            gradientDrawable.setCornerRadius(radius);
        } else if (radii != null && radii.length >= 8) {
            gradientDrawable.setCornerRadii(radii);
        }

        if (strokeWidth > 0) {
            gradientDrawable.setStroke(strokeWidth, strokeColor);
        }
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        return gradientDrawable;
    }
}