package com.github.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.AppCompatRadioButton;

/**
 * Created by ZhongXiaolong on 2020/7/7 6:51 PM.
 * <p>
 * 1.为drawableStart/ drawableTop/ drawableEnd/ drawableBottom添加宽高属性drawableStartWidth/drawableStartHeight...
 * 2.添加背景半圆和设置圆角,与app:backgroundColor一起使用生效:app:backgroundArc="true"--左右半圆; android:radius="@dimen/dp_10"--设置圆角
 * 3.添加选择器,点击效果/不可点击效果,描边
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
 *  *  * <!--文字不可点击时的颜色-->
 *  *  * <attr name="textColorEnabled" />
 *  *  * <!--文字点击时的颜色-->
 *  *  * <attr name="textColorPressed" />
 */
public class SuperRadioButton extends AppCompatRadioButton {

    private final StateListDrawableHelper mStateListDrawableHelper;

    public SuperRadioButton(Context context) {
        this(context, null);
    }

    public SuperRadioButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.radioButtonStyle);
    }

    public SuperRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SuperRadioButton);
        int drawableStartWidth = a.getDimensionPixelSize(R.styleable.SuperRadioButton_drawableStartWidth, 0);
        int drawableStartHeight = a.getDimensionPixelSize(R.styleable.SuperRadioButton_drawableStartHeight, 0);

        Drawable drawableStart = ObjUtil.firstNotNull(getCompoundDrawablesRelative()[0], getCompoundDrawables()[0]);
        Drawable drawableTop = getCompoundDrawablesRelative()[1];
        Drawable drawableEnd = ObjUtil.firstNotNull(getCompoundDrawablesRelative()[2], getCompoundDrawables()[2]);
        Drawable drawableBottom = getCompoundDrawablesRelative()[3];

        int drawableEndWidth = a.getDimensionPixelSize(R.styleable.SuperRadioButton_drawableEndWidth, 0);
        int drawableEndHeight = a.getDimensionPixelSize(R.styleable.SuperRadioButton_drawableEndHeight, 0);

        int drawableTopWidth = a.getDimensionPixelSize(R.styleable.SuperRadioButton_drawableTopWidth, 0);
        int drawableTopHeight = a.getDimensionPixelSize(R.styleable.SuperRadioButton_drawableTopHeight, 0);

        int drawableBottomWidth = a.getDimensionPixelSize(R.styleable.SuperRadioButton_drawableBottomWidth, 0);
        int drawableBottomHeight = a.getDimensionPixelSize(R.styleable.SuperRadioButton_drawableBottomHeight, 0);

        boolean backgroundArc = a.getBoolean(R.styleable.SuperRadioButton_backgroundArc, false);
        float radius = a.getDimension(R.styleable.SuperRadioButton_android_radius, 0);
        float topLeftRadius = a.getDimension(R.styleable.SuperRadioButton_android_topLeftRadius, 0);
        float topRightRadius = a.getDimension(R.styleable.SuperRadioButton_android_topRightRadius, 0);
        float bottomLeftRadius = a.getDimension(R.styleable.SuperRadioButton_android_bottomLeftRadius, 0);
        float bottomRightRadius = a.getDimension(R.styleable.SuperRadioButton_android_bottomRightRadius, 0);

        int backgroundColor = a.getColor(R.styleable.SuperRadioButton_backgroundColor, Color.TRANSPARENT);
        int backgroundColorEnabled = a.getColor(R.styleable.SuperRadioButton_backgroundColorEnabled, StateListDrawableHelper.DEF_COLOR);
        int backgroundColorPressed = a.getColor(R.styleable.SuperRadioButton_backgroundColorPressed, StateListDrawableHelper.DEF_COLOR);
        int backgroundColorChecked = a.getColor(R.styleable.SuperRadioButton_backgroundColorChecked, StateListDrawableHelper.DEF_COLOR);
        int strokeWidth = a.getDimensionPixelOffset(R.styleable.SuperRadioButton_strokeWidth, 0);
        int strokeColor = a.getColor(R.styleable.SuperRadioButton_strokeColor, Color.TRANSPARENT);
        int strokeColorEnabled = a.getColor(R.styleable.SuperRadioButton_strokeColorEnabled, strokeColor);
        int strokeColorPressed = a.getColor(R.styleable.SuperRadioButton_strokeColorPressed, strokeColor);
        int strokeColorChecked = a.getColor(R.styleable.SuperRadioButton_strokeColorChecked, strokeColor);

        int textColorEnabled = a.getColor(R.styleable.SuperRadioButton_textColorEnabled, Color.TRANSPARENT);
        int textColorPressed = a.getColor(R.styleable.SuperRadioButton_textColorPressed, Color.TRANSPARENT);
        String backgroundColorsString = a.getString(R.styleable.SuperRadioButton_backgroundColors);
        int backgroundOrientation = a.getInt(R.styleable.SuperRadioButton_backgroundOrientation, 0);

        a.recycle();

        //渐变的颜色数值
        int[] backgroundColors = ObjUtil.subInt(backgroundColorsString);
        GradientDrawable.Orientation orientation = ObjUtil.int2Orientation(backgroundOrientation);

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

        mStateListDrawableHelper = new StateListDrawableHelper(backgroundArc, radius, topLeftRadius, topRightRadius,
                bottomLeftRadius, bottomRightRadius, backgroundColor, backgroundColorEnabled, backgroundColorPressed, backgroundColorChecked,
                strokeWidth, strokeColor, strokeColorEnabled, strokeColorPressed, strokeColorChecked, orientation, backgroundColors);

        //设置文字选择器
        if (textColorPressed != Color.TRANSPARENT || textColorEnabled != Color.TRANSPARENT) {
            int defaultColor = getTextColors().getDefaultColor();
            int[][] states = {{android.R.attr.state_pressed}, {-android.R.attr.state_enabled}, {}};
            int[] colors = {textColorPressed, textColorEnabled, defaultColor};
            ColorStateList colorStateList = new ColorStateList(states, colors);
            setTextColor(colorStateList);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        setBackground(mStateListDrawableHelper.getStateListDrawable(h));
    }
}
