package com.github.widget;

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

/**
 * Created by ZhongXiaolong on 2021/9/23 3:17 下午.
 * <p>
 * 设置颜色帮助类
 */
class StateListDrawableHelper {

    public final static int DEF_COLOR = -912;
    private boolean mBackgroundArc;
    private float mRadius;
    private float mTopLeftRadius;
    private float mTopRightRadius;
    private float mBottomLeftRadius;
    private float mBottomRightRadius;
    private int mBackgroundColor;
    private int mBackgroundColorEnabled;
    private int mBackgroundColorPressed;
    private int mBackgroundColorChecked;
    private int mStrokeWidth;
    private int mStrokeColor;
    private int mStrokeColorEnabled;
    private int mStrokeColorPressed;
    private int mStrokeColorChecked;
    private GradientDrawable.Orientation mBackgroundOrientation;
    private int[] mBackgroundColors;

    public StateListDrawableHelper(boolean backgroundArc, float radius, float topLeftRadius, float topRightRadius, float bottomLeftRadius,
                                   float bottomRightRadius, int backgroundColor, int backgroundColorEnabled, int backgroundColorPressed,
                                   int backgroundColorChecked, int strokeWidth, int strokeColor, int strokeColorEnabled, int strokeColorPressed,
                                   int strokeColorChecked, GradientDrawable.Orientation backgroundOrientation, int[] backgroundColors) {
        mBackgroundArc = backgroundArc;
        mRadius = radius;
        mTopLeftRadius = topLeftRadius;
        mTopRightRadius = topRightRadius;
        mBottomLeftRadius = bottomLeftRadius;
        mBottomRightRadius = bottomRightRadius;
        mBackgroundColor = backgroundColor;
        mBackgroundColorEnabled = backgroundColorEnabled;
        mBackgroundColorPressed = backgroundColorPressed;
        mBackgroundColorChecked = backgroundColorChecked;
        mStrokeWidth = strokeWidth;
        mStrokeColor = strokeColor;
        mStrokeColorEnabled = strokeColorEnabled;
        mStrokeColorPressed = strokeColorPressed;
        mStrokeColorChecked = strokeColorChecked;
        mBackgroundOrientation = backgroundOrientation;
        mBackgroundColors = backgroundColors;
    }

    public StateListDrawableHelper() {
    }

    public StateListDrawableHelper setBackgroundArc(boolean backgroundArc) {
        mBackgroundArc = backgroundArc;
        return this;
    }

    public StateListDrawableHelper setRadius(float radius) {
        mRadius = radius;
        return this;
    }

    public StateListDrawableHelper setTopLeftRadius(float topLeftRadius) {
        mTopLeftRadius = topLeftRadius;
        return this;
    }

    public StateListDrawableHelper setTopRightRadius(float topRightRadius) {
        mTopRightRadius = topRightRadius;
        return this;
    }

    public StateListDrawableHelper setBottomLeftRadius(float bottomLeftRadius) {
        mBottomLeftRadius = bottomLeftRadius;
        return this;
    }

    public StateListDrawableHelper setBottomRightRadius(float bottomRightRadius) {
        mBottomRightRadius = bottomRightRadius;
        return this;
    }

    public StateListDrawableHelper setBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
        return this;
    }

    public StateListDrawableHelper setBackgroundColorEnabled(int backgroundColorEnabled) {
        mBackgroundColorEnabled = backgroundColorEnabled;
        return this;
    }

    public StateListDrawableHelper setBackgroundColorPressed(int backgroundColorPressed) {
        mBackgroundColorPressed = backgroundColorPressed;
        return this;
    }

    public StateListDrawableHelper setBackgroundColorChecked(int backgroundColorChecked) {
        mBackgroundColorChecked = backgroundColorChecked;
        return this;
    }

    public StateListDrawableHelper setStrokeWidth(int strokeWidth) {
        mStrokeWidth = strokeWidth;
        return this;
    }

    public StateListDrawableHelper setStrokeColor(int strokeColor) {
        mStrokeColor = strokeColor;
        return this;
    }

    public StateListDrawableHelper setStrokeColorEnabled(int strokeColorEnabled) {
        mStrokeColorEnabled = strokeColorEnabled;
        return this;
    }

    public StateListDrawableHelper setStrokeColorPressed(int strokeColorPressed) {
        mStrokeColorPressed = strokeColorPressed;
        return this;
    }

    public StateListDrawableHelper setStrokeColorChecked(int strokeColorChecked) {
        mStrokeColorChecked = strokeColorChecked;
        return this;
    }

    public StateListDrawableHelper setBackgroundOrientation(GradientDrawable.Orientation backgroundOrientation) {
        mBackgroundOrientation = backgroundOrientation;
        return this;
    }

    public StateListDrawableHelper setBackgroundColors(int[] backgroundColors) {
        mBackgroundColors = backgroundColors;
        return this;
    }

    public StateListDrawable getStateListDrawable(int viewHeight) {
        if (mBackgroundArc) {
            StateListDrawable stateListDrawable = new StateListDrawable();

            GradientDrawable normalDrawable = getGradientDrawable(mBackgroundColors, mBackgroundColor, viewHeight * 0.5f, null, mStrokeWidth,
                    mStrokeColor);

            //点击状态
            if (mBackgroundColorPressed != DEF_COLOR) {
                GradientDrawable pressedDrawable = getGradientDrawable(mBackgroundColorPressed, viewHeight * 0.5f, null, mStrokeWidth,
                        mStrokeColorPressed);
                stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);
            }

            //不可点击状态
            if (mBackgroundColorEnabled != DEF_COLOR) {
                GradientDrawable enabledDrawable = getGradientDrawable(mBackgroundColorEnabled, viewHeight * 0.5f, null, mStrokeWidth,
                        mStrokeColorEnabled);
                stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, enabledDrawable);
            }

            //选中状态
            if (mBackgroundColorChecked != DEF_COLOR) {
                GradientDrawable enabledDrawable = getGradientDrawable(mBackgroundColorChecked, viewHeight * 0.5f, null, mStrokeWidth,
                        mStrokeColorChecked);
                stateListDrawable.addState(new int[]{android.R.attr.state_checked}, enabledDrawable);
            }

            //默认背景要放最后
            stateListDrawable.addState(new int[]{}, normalDrawable);

            return stateListDrawable;
        }

        if (mBottomLeftRadius > 0 || mBottomRightRadius > 0 || mTopLeftRadius > 0 || mTopRightRadius > 0) {
            float[] radii = {mTopLeftRadius, mTopLeftRadius, mTopRightRadius, mTopRightRadius,
                    mBottomRightRadius, mBottomRightRadius, mBottomLeftRadius, mBottomLeftRadius};

            StateListDrawable stateListDrawable = new StateListDrawable();

            GradientDrawable normalDrawable = getGradientDrawable(mBackgroundColors, mBackgroundColor, 0, radii, mStrokeWidth, mStrokeColor);

            //点击状态
            if (mBackgroundColorPressed != DEF_COLOR) {
                GradientDrawable pressedDrawable = getGradientDrawable(mBackgroundColorPressed, 0, radii, mStrokeWidth, mStrokeColorPressed);
                stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);
            }

            //不可点击状态
            if (mBackgroundColorEnabled != DEF_COLOR) {
                GradientDrawable enabledDrawable = getGradientDrawable(mBackgroundColorEnabled, 0, radii, mStrokeWidth, mStrokeColorEnabled);
                stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, enabledDrawable);
            }

            //选中状态
            if (mBackgroundColorChecked != DEF_COLOR) {
                GradientDrawable enabledDrawable = getGradientDrawable(mBackgroundColorChecked, 0, radii, mStrokeWidth, mStrokeColorChecked);
                stateListDrawable.addState(new int[]{android.R.attr.state_checked}, enabledDrawable);
            }

            //默认背景要放最后
            stateListDrawable.addState(new int[]{}, normalDrawable);

            return stateListDrawable;
        }

        StateListDrawable stateListDrawable = new StateListDrawable();

        GradientDrawable normalDrawable = getGradientDrawable(mBackgroundColors, mBackgroundColor, mRadius, null, mStrokeWidth, mStrokeColor);

        //点击状态
        if (mBackgroundColorPressed != DEF_COLOR) {
            GradientDrawable pressedDrawable = getGradientDrawable(mBackgroundColorPressed, mRadius, null, mStrokeWidth, mStrokeColorPressed);
            stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);
        }

        //不可点击状态
        if (mBackgroundColorEnabled != DEF_COLOR) {
            GradientDrawable enabledDrawable = getGradientDrawable(mBackgroundColorEnabled, mRadius, null, mStrokeWidth, mStrokeColorEnabled);
            stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, enabledDrawable);
        }

        //选中状态
        if (mBackgroundColorChecked != DEF_COLOR) {
            GradientDrawable enabledDrawable = getGradientDrawable(mBackgroundColorChecked, mRadius, null, mStrokeWidth, mStrokeColorChecked);
            stateListDrawable.addState(new int[]{android.R.attr.state_checked}, enabledDrawable);
        }

        //默认背景要放最后
        stateListDrawable.addState(new int[]{}, normalDrawable);

        return stateListDrawable;
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
