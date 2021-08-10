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
public class SuperRadioButton extends AppCompatRadioButton {

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
    private GradientDrawable.Orientation mBackgroundOrientation;
    private int[] mBackgroundColors;

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

        Drawable drawableStart = firstNotNull(getCompoundDrawablesRelative()[0], getCompoundDrawables()[0]);
        Drawable drawableTop = getCompoundDrawablesRelative()[1];
        Drawable drawableEnd = firstNotNull(getCompoundDrawablesRelative()[2], getCompoundDrawables()[2]);
        Drawable drawableBottom = getCompoundDrawablesRelative()[3];

        int drawableEndWidth = a.getDimensionPixelSize(R.styleable.SuperRadioButton_drawableEndWidth, 0);
        int drawableEndHeight = a.getDimensionPixelSize(R.styleable.SuperRadioButton_drawableEndHeight, 0);

        int drawableTopWidth = a.getDimensionPixelSize(R.styleable.SuperRadioButton_drawableTopWidth, 0);
        int drawableTopHeight = a.getDimensionPixelSize(R.styleable.SuperRadioButton_drawableTopHeight, 0);

        int drawableBottomWidth = a.getDimensionPixelSize(R.styleable.SuperRadioButton_drawableBottomWidth, 0);
        int drawableBottomHeight = a.getDimensionPixelSize(R.styleable.SuperRadioButton_drawableBottomHeight, 0);

        mBackgroundArc = a.getBoolean(R.styleable.SuperRadioButton_backgroundArc, false);
        mRadius = a.getDimension(R.styleable.SuperRadioButton_android_radius, 0);
        mTopLeftRadius = a.getDimension(R.styleable.SuperRadioButton_android_topLeftRadius, 0);
        mTopRightRadius = a.getDimension(R.styleable.SuperRadioButton_android_topRightRadius, 0);
        mBottomLeftRadius = a.getDimension(R.styleable.SuperRadioButton_android_bottomLeftRadius, 0);
        mBottomRightRadius = a.getDimension(R.styleable.SuperRadioButton_android_bottomRightRadius, 0);

        mBackgroundColor = a.getColor(R.styleable.SuperRadioButton_backgroundColor, Color.TRANSPARENT);
        mBackgroundColorEnabled = a.getColor(R.styleable.SuperRadioButton_backgroundColorEnabled, mBackgroundColor);
        mBackgroundColorPressed = a.getColor(R.styleable.SuperRadioButton_backgroundColorPressed, mBackgroundColor);
        mStrokeWidth = a.getDimensionPixelOffset(R.styleable.SuperRadioButton_strokeWidth, 0);
        mStrokeColor = a.getColor(R.styleable.SuperRadioButton_strokeColor, Color.TRANSPARENT);
        mStrokeColorEnabled = a.getColor(R.styleable.SuperRadioButton_strokeColorEnabled, mStrokeColor);
        mStrokeColorPressed = a.getColor(R.styleable.SuperRadioButton_strokeColorPressed, mStrokeColor);
        int textColorEnabled = a.getColor(R.styleable.SuperRadioButton_textColorEnabled, Color.TRANSPARENT);
        int textColorPressed = a.getColor(R.styleable.SuperRadioButton_textColorPressed, Color.TRANSPARENT);
        String backgroundColorsString = a.getString(R.styleable.SuperRadioButton_backgroundColors);
        int backgroundOrientation = a.getInt(R.styleable.SuperRadioButton_backgroundOrientation, 0);

        a.recycle();

        //渐变的颜色数值
        List<Integer> backgroundColors = new ArrayList<>();
        if (!TextUtils.isEmpty(backgroundColorsString)) {
            int index;
            while ((index = backgroundColorsString.indexOf(",")) > 0) {
                try {
                    String colorString = backgroundColorsString.substring(0, index).trim();
                    backgroundColors.add(Color.parseColor(colorString));
                    backgroundColorsString = backgroundColorsString.substring(index + 1);
                } catch (Exception e) {
                    backgroundColorsString = backgroundColorsString.substring(index + 1);
                }
            }

            try {
                backgroundColors.add(Color.parseColor(backgroundColorsString));
            } catch (Exception e) {
                Log.d("SuperButton", "e:" + e);
            }

            if (backgroundColors.size() > 0) {
                mBackgroundColors = new int[backgroundColors.size()];
                for (int i = 0; i < backgroundColors.size(); i++) {
                    mBackgroundColors[i] = backgroundColors.get(i);
                }
            }

            if (backgroundOrientation == 1) mBackgroundOrientation = GradientDrawable.Orientation.TOP_BOTTOM;
            else if (backgroundOrientation == 2) mBackgroundOrientation = GradientDrawable.Orientation.TR_BL;
            else if (backgroundOrientation == 3) mBackgroundOrientation = GradientDrawable.Orientation.RIGHT_LEFT;
            else if (backgroundOrientation == 4) mBackgroundOrientation = GradientDrawable.Orientation.BR_TL;
            else if (backgroundOrientation == 5) mBackgroundOrientation = GradientDrawable.Orientation.BOTTOM_TOP;
            else if (backgroundOrientation == 6) mBackgroundOrientation = GradientDrawable.Orientation.BL_TR;
            else if (backgroundOrientation == 7) mBackgroundOrientation = GradientDrawable.Orientation.LEFT_RIGHT;
            else mBackgroundOrientation = GradientDrawable.Orientation.TL_BR;
        }

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
        if (mBackgroundColors == null && mBackgroundColor == Color.TRANSPARENT && mBackgroundColorPressed == Color.TRANSPARENT && mBackgroundColorEnabled == Color.TRANSPARENT && mStrokeColor == Color.TRANSPARENT && mStrokeColorPressed == Color.TRANSPARENT && mStrokeColorEnabled == Color.TRANSPARENT) {
            return;
        }

        if (mBackgroundArc) {
            StateListDrawable stateListDrawable = new StateListDrawable();

            GradientDrawable normalDrawable = getGradientDrawable(mBackgroundColors, mBackgroundColor, h * 0.5f, null, mStrokeWidth, mStrokeColor);

            //点击状态
            if (mBackgroundColorPressed == 0) {
                stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, normalDrawable);
            } else {
                GradientDrawable pressedDrawable = getGradientDrawable(mBackgroundColorPressed, h * 0.5f, null, mStrokeWidth, mStrokeColorPressed);
                stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);
            }

            //不可点击状态
            if (mBackgroundColorEnabled == 0) {
                stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, normalDrawable);
            } else {
                GradientDrawable enabledDrawable = getGradientDrawable(mBackgroundColorEnabled, h * 0.5f, null, mStrokeWidth, mStrokeColorEnabled);
                stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, enabledDrawable);
            }

            //默认背景要放最后
            stateListDrawable.addState(new int[]{}, normalDrawable);

            setBackground(stateListDrawable);
            return;
        }

        if (mBottomLeftRadius > 0 || mBottomRightRadius > 0 || mTopLeftRadius > 0 || mTopRightRadius > 0) {
            float[] radii = {mTopLeftRadius, mTopLeftRadius, mTopRightRadius, mTopRightRadius,
                    mBottomRightRadius, mBottomRightRadius, mBottomLeftRadius, mBottomLeftRadius};

            StateListDrawable stateListDrawable = new StateListDrawable();

            GradientDrawable normalDrawable = getGradientDrawable(mBackgroundColors, mBackgroundColor, 0, radii, mStrokeWidth, mStrokeColor);

            //点击状态
            if (mBackgroundColorPressed == 0) {
                stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, normalDrawable);
            } else {
                GradientDrawable pressedDrawable = getGradientDrawable(mBackgroundColorPressed, 0, radii, mStrokeWidth, mStrokeColorPressed);
                stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);
            }

            //不可点击状态
            if (mBackgroundColorEnabled == 0) {
                stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, normalDrawable);
            } else {
                GradientDrawable enabledDrawable = getGradientDrawable(mBackgroundColorEnabled, 0, radii, mStrokeWidth, mStrokeColorEnabled);
                stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, enabledDrawable);
            }

            //默认背景要放最后
            stateListDrawable.addState(new int[]{}, normalDrawable);

            setBackground(stateListDrawable);

            return;
        }

        StateListDrawable stateListDrawable = new StateListDrawable();

        GradientDrawable normalDrawable = getGradientDrawable(mBackgroundColors, mBackgroundColor, mRadius, null, mStrokeWidth, mStrokeColor);

        //点击状态
        if (mBackgroundColorPressed == 0) {
            stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, normalDrawable);
        } else {
            GradientDrawable pressedDrawable = getGradientDrawable(mBackgroundColorPressed, mRadius, null, mStrokeWidth, mStrokeColorPressed);
            stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);
        }

        //不可点击状态
        GradientDrawable enabledDrawable = getGradientDrawable(mBackgroundColorEnabled, mRadius, null, mStrokeWidth, mStrokeColorEnabled);
        stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, enabledDrawable);

        //默认背景要放最后
        stateListDrawable.addState(new int[]{}, normalDrawable);

        setBackground(stateListDrawable);

    }

    private GradientDrawable getGradientDrawable(int color, float radius, float[] radii, int strokeWidth, int strokeColor) {
        return getGradientDrawable(null, color, radius, radii, strokeWidth, strokeColor);
    }

    private GradientDrawable getGradientDrawable(int[] colors, int color, float radius, float[] radii, int strokeWidth, int strokeColor) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        if (colors != null && colors.length > 0) {
            if (colors.length == 1) {
                gradientDrawable.setColor(colors[0]);
            } else if (colors.length > 2) {
                gradientDrawable.setColors(colors);
                gradientDrawable.setOrientation(mBackgroundOrientation);
            } else {
                int[] newColors = new int[3];
                newColors[0] = colors[0];
                newColors[1] = colors[0];
                newColors[2] = colors[1];
                gradientDrawable.setColors(newColors);
                gradientDrawable.setOrientation(mBackgroundOrientation);
            }
        } else {
            gradientDrawable.setColor(color);
        }

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
