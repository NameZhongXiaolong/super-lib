package com.github.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * Created by ZhongXiaolong on 2020/7/7 6:51 PM.
 * <p>
 * 1.为drawableStart/ drawableTop/ drawableEnd/ drawableBottom添加宽高属性drawableStartWidth/drawableStartHeight...
 * 2.添加背景半圆和设置圆角,与app:backgroundColor一起使用生效:app:backgroundArc="true"--左右半圆; android:radius="@dimen/dp_10"--设置圆角
 * 3.添加选择器,点击效果/不可点击效果,描边
 *  *  * <!--背景颜色-->
 *  *  * <attr name="backgroundColor" />
 *  *  * <!--不可点击时的背景颜色-->
 *  *  * <attr name="backgroundColorEnabled" />
 *  *  * <!--点击时的背景颜色-->
 *  *  * <attr name="backgroundColorPressed" />
 *  *  * <!--背景描边宽度-->
 *  *  * <attr name="strokeWidth" />
 *  *  * <!--背景描边颜色-->
 *  *  * <attr name="strokeColor" />
 *  *  * <!--背景描边不可点击时的颜色-->
 *  *  * <attr name="strokeColorEnabled" />
 *  *  * <!--背景描边点击时的颜色-->
 *  *  * <attr name="strokeColorPressed" />
 *  *  * <!--文字不可点击时的颜色-->
 *  *  * <attr name="textColorEnabled" />
 *  *  * <!--文字点击时的颜色-->
 *  *  * <attr name="textColorPressed" />
 */
public class SuperTextView extends AppCompatTextView {

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

    public SuperTextView(Context context) {
        this(context, null);
    }

    public SuperTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public SuperTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SuperTextView);
        int drawableStartWidth = a.getDimensionPixelSize(R.styleable.SuperTextView_drawableStartWidth, 0);
        int drawableStartHeight = a.getDimensionPixelSize(R.styleable.SuperTextView_drawableStartHeight, 0);

        Drawable drawableStart = firstNotNull(getCompoundDrawablesRelative()[0], getCompoundDrawables()[0]);
        Drawable drawableTop = getCompoundDrawablesRelative()[1];
        Drawable drawableEnd = firstNotNull(getCompoundDrawablesRelative()[2], getCompoundDrawables()[2]);
        Drawable drawableBottom = getCompoundDrawablesRelative()[3];

        int drawableEndWidth = a.getDimensionPixelSize(R.styleable.SuperTextView_drawableEndWidth, 0);
        int drawableEndHeight = a.getDimensionPixelSize(R.styleable.SuperTextView_drawableEndHeight, 0);

        int drawableTopWidth = a.getDimensionPixelSize(R.styleable.SuperTextView_drawableTopWidth, 0);
        int drawableTopHeight = a.getDimensionPixelSize(R.styleable.SuperTextView_drawableTopHeight, 0);

        int drawableBottomWidth = a.getDimensionPixelSize(R.styleable.SuperTextView_drawableBottomWidth, 0);
        int drawableBottomHeight = a.getDimensionPixelSize(R.styleable.SuperTextView_drawableBottomHeight, 0);

        mBackgroundArc = a.getBoolean(R.styleable.SuperTextView_backgroundArc, false);
        mRadius = a.getDimension(R.styleable.SuperTextView_android_radius, 0);
        mTopLeftRadius = a.getDimension(R.styleable.SuperTextView_android_topLeftRadius, 0);
        mTopRightRadius = a.getDimension(R.styleable.SuperTextView_android_topRightRadius, 0);
        mBottomLeftRadius = a.getDimension(R.styleable.SuperTextView_android_bottomLeftRadius, 0);
        mBottomRightRadius = a.getDimension(R.styleable.SuperTextView_android_bottomRightRadius, 0);

        mBackgroundColor = a.getColor(R.styleable.SuperTextView_backgroundColor, Color.TRANSPARENT);
        mBackgroundColorEnabled = a.getColor(R.styleable.SuperTextView_backgroundColorEnabled, mBackgroundColor);
        mBackgroundColorPressed = a.getColor(R.styleable.SuperTextView_backgroundColorPressed, mBackgroundColor);
        mStrokeWidth = a.getDimensionPixelOffset(R.styleable.SuperTextView_strokeWidth, 0);
        mStrokeColor = a.getColor(R.styleable.SuperTextView_strokeColor, Color.TRANSPARENT);
        mStrokeColorEnabled = a.getColor(R.styleable.SuperTextView_strokeColorEnabled, mStrokeColor);
        mStrokeColorPressed = a.getColor(R.styleable.SuperTextView_strokeColorPressed, mStrokeColor);
        int textColorEnabled = a.getColor(R.styleable.SuperTextView_textColorEnabled, Color.TRANSPARENT);
        int textColorPressed = a.getColor(R.styleable.SuperTextView_textColorPressed, Color.TRANSPARENT);

        a.recycle();

        //重设drawableStart/drawableTop/drawableEnd/drawableBottom  宽高

        //标记
        boolean drawableChange = false;

        //drawableStart
        if (drawableStartWidth > 0 || drawableStartHeight > 0) {
            if (drawableStart != null) {
                if (drawableStartWidth == 0) {
                    drawableStartWidth = drawableStart.getIntrinsicWidth() * drawableStartHeight / drawableStart.getIntrinsicHeight();
                    drawableStart.setBounds(0, 0, drawableStartWidth, drawableStartHeight);
                } else if (drawableStartHeight == 0) {
                    drawableStartHeight = drawableStart.getIntrinsicHeight() * drawableStartWidth / drawableStart.getIntrinsicWidth();
                    drawableStart.setBounds(0, 0, drawableStartWidth, drawableStartHeight);
                } else {
                    drawableStart.setBounds(0, 0, drawableStartWidth, drawableStartHeight);
                }
                drawableChange = true;
            }
        }

        //drawableEnd
        if (drawableEndWidth > 0 || drawableEndHeight > 0) {
            if (drawableEnd != null) {
                if (drawableEndWidth == 0) {
                    drawableEndWidth = drawableEnd.getIntrinsicWidth() * drawableEndHeight / drawableEnd.getIntrinsicHeight();
                    drawableEnd.setBounds(0, 0, drawableEndWidth, drawableEndHeight);
                } else if (drawableEndHeight == 0) {
                    drawableEndHeight = drawableEnd.getIntrinsicHeight() * drawableEndWidth / drawableEnd.getIntrinsicWidth();
                    drawableEnd.setBounds(0, 0, drawableEndWidth, drawableEndHeight);
                } else {
                    drawableEnd.setBounds(0, 0, drawableEndWidth, drawableEndHeight);
                }
                drawableChange = true;
            }
        }

        //drawableTop
        if (drawableTopWidth > 0 || drawableTopHeight > 0) {
            if (drawableTop != null) {
                if (drawableTopWidth == 0) {
                    drawableTopWidth = drawableTop.getIntrinsicWidth() * drawableTopHeight / drawableTop.getIntrinsicHeight();
                    drawableTop.setBounds(0, 0, drawableTopWidth, drawableTopHeight);
                } else if (drawableTopHeight == 0) {
                    drawableTopHeight = drawableTop.getIntrinsicHeight() * drawableTopWidth / drawableTop.getIntrinsicWidth();
                    drawableTop.setBounds(0, 0, drawableTopWidth, drawableTopHeight);
                } else {
                    drawableTop.setBounds(0, 0, drawableTopWidth, drawableTopHeight);
                }
                drawableChange = true;
            }
        }

        //drawableBottom
        if (drawableBottomWidth > 0 || drawableBottomHeight > 0) {
            if (drawableBottom != null) {
                if (drawableBottomWidth == 0) {
                    drawableBottomWidth = drawableBottom.getIntrinsicWidth() * drawableBottomHeight / drawableBottom.getIntrinsicHeight();
                    drawableBottom.setBounds(0, 0, drawableBottomWidth, drawableBottomHeight);
                } else if (drawableBottomHeight == 0) {
                    drawableBottomHeight = drawableBottom.getIntrinsicHeight() * drawableBottomWidth / drawableBottom.getIntrinsicWidth();
                    drawableBottom.setBounds(0, 0, drawableBottomWidth, drawableBottomHeight);
                } else {
                    drawableBottom.setBounds(0, 0, drawableBottomWidth, drawableBottomHeight);
                }
                drawableChange = true;
            }
        }

        if (drawableChange) {
            setCompoundDrawables(drawableStart, drawableTop, drawableEnd, drawableBottom);
        }

        //设置文字选择器
        if (textColorPressed != Color.TRANSPARENT || textColorEnabled != Color.TRANSPARENT) {
            int defaultColor = getTextColors().getDefaultColor();
            int[][] states = {{android.R.attr.state_pressed}, {-android.R.attr.state_enabled}, {}};
            int[] colors = {textColorPressed, textColorEnabled, defaultColor};
            ColorStateList colorStateList = new ColorStateList(states, colors);
            setTextColor(colorStateList);
        }
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
