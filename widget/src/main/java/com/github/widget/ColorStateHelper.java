package com.github.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by ZhongXiaolong on 2021/9/23 3:17 下午.
 * <p>
 * 颜色选择器帮助类
 */
class ColorStateHelper {

    public final static int DEF_COLOR = AttrsParseUtil.DEF_COLOR;
    private View mView;
    private boolean mBackgroundArc;
    private float mRadius;
    private float mTopLeftRadius;
    private float mTopRightRadius;
    private float mBottomLeftRadius;
    private float mBottomRightRadius;
    private int mBackgroundColor = DEF_COLOR;
    private int mBackgroundColorEnabled = DEF_COLOR;
    private int mBackgroundColorPressed = DEF_COLOR;
    private int mBackgroundColorChecked = DEF_COLOR;
    private int mStrokeWidth;
    private int mStrokeColor = DEF_COLOR;
    private int mStrokeColorEnabled = DEF_COLOR;
    private int mStrokeColorPressed = DEF_COLOR;
    private int mStrokeColorChecked = DEF_COLOR;
    private int mTextColorEnabled = DEF_COLOR;
    private int mTextColorPressed = DEF_COLOR;
    private int mTextColorChecked = DEF_COLOR;
    private GradientDrawable.Orientation mBackgroundOrientation = GradientDrawable.Orientation.TL_BR;
    private int[] mBackgroundColors;

    public static ColorStateHelper wrap(View view, AttributeSet attrs) {
        final Context context = view.getContext();
        final ColorStateHelper helper = new ColorStateHelper();
        helper.mView = view;
        if (attrs != null) {
            for (int i = 0; i < attrs.getAttributeCount(); i++) {
                String attributeName = attrs.getAttributeName(i);
                if ("backgroundArc".equals(attributeName)) {
                    helper.mBackgroundArc = attrs.getAttributeBooleanValue(i, false);
                } else if ("backgroundColor".equals(attributeName)) {
                    helper.mBackgroundColor = AttrsParseUtil.getColor(context, attrs, i);
                } else if ("backgroundColorEnabled".equals(attributeName)) {
                    helper.mBackgroundColorEnabled = AttrsParseUtil.getColor(context, attrs, i);
                } else if ("backgroundColorPressed".equals(attributeName)) {
                    helper.mBackgroundColorPressed = AttrsParseUtil.getColor(context, attrs, i);
                } else if ("backgroundColorChecked".equals(attributeName)) {
                    helper.mBackgroundColorChecked = AttrsParseUtil.getColor(context, attrs, i);
                } else if ("strokeWidth".equals(attributeName)) {
                    helper.mStrokeWidth = AttrsParseUtil.getDimensionPixelOffset(context, attrs, i);
                } else if ("strokeColor".equals(attributeName)) {
                    helper.mStrokeColor = AttrsParseUtil.getColor(context, attrs, i);
                } else if ("strokeColorEnabled".equals(attributeName)) {
                    helper.mStrokeColorEnabled = AttrsParseUtil.getColor(context, attrs, i);
                } else if ("strokeColorPressed".equals(attributeName)) {
                    helper.mStrokeColorPressed = AttrsParseUtil.getColor(context, attrs, i);
                } else if ("strokeColorChecked".equals(attributeName)) {
                    helper.mStrokeColorChecked = AttrsParseUtil.getColor(context, attrs, i);
                } else if ("textColorEnabled".equals(attributeName)) {
                    helper.mTextColorEnabled = AttrsParseUtil.getColor(context, attrs, i);
                } else if ("textColorPressed".equals(attributeName)) {
                    helper.mTextColorPressed = AttrsParseUtil.getColor(context, attrs, i);
                } else if ("textColorChecked".equals(attributeName)) {
                    helper.mTextColorChecked = AttrsParseUtil.getColor(context, attrs, i);
                } else if ("radius".equals(attributeName)) {
                    helper.mRadius = AttrsParseUtil.getDimensionPixelOffset(context, attrs, i);
                } else if ("topLeftRadius".equals(attributeName)) {
                    helper.mTopLeftRadius = AttrsParseUtil.getDimensionPixelOffset(context, attrs, i);
                } else if ("topRightRadius".equals(attributeName)) {
                    helper.mTopRightRadius = AttrsParseUtil.getDimensionPixelOffset(context, attrs, i);
                } else if ("bottomLeftRadius".equals(attributeName)) {
                    helper.mBottomLeftRadius = AttrsParseUtil.getDimensionPixelOffset(context, attrs, i);
                } else if ("bottomRightRadius".equals(attributeName)) {
                    helper.mBottomRightRadius = AttrsParseUtil.getDimensionPixelOffset(context, attrs, i);
                } else if ("backgroundColors".equals(attributeName)) {
                    //渐变颜色
                    String backgroundColorsString = attrs.getAttributeValue(i);
                    helper.mBackgroundColors = ObjUtil.subInt(backgroundColorsString);
                } else if ("backgroundOrientation".equals(attributeName)) {
                    //渐变方向
                    int backgroundOrientation = attrs.getAttributeIntValue(i, 0);
                    helper.mBackgroundOrientation = helper.int2Orientation(backgroundOrientation);
                }
            }
        }

        helper.mStrokeColorEnabled = helper.firstNotDefColor(helper.mStrokeColorEnabled, helper.mStrokeColor);
        helper.mStrokeColorPressed = helper.firstNotDefColor(helper.mStrokeColorPressed, helper.mStrokeColor);
        helper.mStrokeColorChecked = helper.firstNotDefColor(helper.mStrokeColorChecked, helper.mStrokeColor);

        helper.setTextColorStateList();

        return helper;
    }

    public StateListDrawable getStateListDrawable(int viewHeight) {
        final float[] radii;
        if (mBackgroundArc) {
            float radius = viewHeight * 0.5f;
            radii = new float[]{radius, radius, radius, radius, radius, radius, radius, radius};
        } else if (mBottomLeftRadius > 0 || mBottomRightRadius > 0 || mTopLeftRadius > 0 || mTopRightRadius > 0) {
            radii = new float[]{mTopLeftRadius, mTopLeftRadius, mTopRightRadius, mTopRightRadius,
                    mBottomRightRadius, mBottomRightRadius, mBottomLeftRadius, mBottomLeftRadius};
        } else {
            radii = new float[]{mRadius, mRadius, mRadius, mRadius, mRadius, mRadius, mRadius, mRadius};
        }

        StateListDrawable stateListDrawable = new StateListDrawable();

        //默认
        Drawable normalDrawable;

        if (mBackgroundColor == DEF_COLOR && (mBackgroundColors == null || mBackgroundColors.length == 0)) {
            Drawable background = mView.getBackground();
            if (background instanceof ColorDrawable) {
                normalDrawable = getGradientDrawable(((ColorDrawable) background).getColor(), radii, mStrokeColor);
            } else if (mStrokeColor != DEF_COLOR && mStrokeWidth > 1) {
                normalDrawable = getGradientDrawable(Color.TRANSPARENT, radii, mStrokeColor);
            } else {
                normalDrawable = background;
            }
        } else {
            GradientDrawable gradientDrawable = getGradientDrawable(mBackgroundColor, radii, mStrokeColor);
            if (mBackgroundColors != null && mBackgroundColors.length > 0) {
                if (mBackgroundColors.length == 1) {
                    gradientDrawable.setColor(mBackgroundColors[0]);
                } else if (mBackgroundColors.length > 2) {
                    gradientDrawable.setColors(mBackgroundColors);
                    gradientDrawable.setOrientation(mBackgroundOrientation);
                } else {
                    int[] newColors = new int[3];
                    newColors[0] = mBackgroundColors[0];
                    newColors[1] = mBackgroundColors[0];
                    newColors[2] = mBackgroundColors[1];
                    gradientDrawable.setColors(newColors);
                    gradientDrawable.setOrientation(mBackgroundOrientation);
                }
            }
            normalDrawable = gradientDrawable;
        }

        //点击状态
        if (mBackgroundColorPressed != DEF_COLOR) {
            GradientDrawable pressedDrawable = getGradientDrawable(mBackgroundColorPressed, radii, mStrokeColorPressed);
            stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);
        }

        //不可点击状态
        if (mBackgroundColorEnabled != DEF_COLOR) {
            GradientDrawable enabledDrawable = getGradientDrawable(mBackgroundColorEnabled, radii, mStrokeColorEnabled);
            stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, enabledDrawable);
        }

        //选中状态
        if (mBackgroundColorChecked != DEF_COLOR) {
            GradientDrawable enabledDrawable = getGradientDrawable(mBackgroundColorChecked, radii, mStrokeColorChecked);
            stateListDrawable.addState(new int[]{android.R.attr.state_checked}, enabledDrawable);
        }

        //默认背景要放最后
        stateListDrawable.addState(new int[]{}, normalDrawable);

        return stateListDrawable;
    }

    private GradientDrawable getGradientDrawable(int color, float[] radii, int strokeColor) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setColor(color);
        gradientDrawable.setCornerRadii(radii);
        gradientDrawable.setStroke(mStrokeWidth, strokeColor);
        return gradientDrawable;
    }

    private int firstNotDefColor(int... color) {
        for (int i : color) {
            if (i != DEF_COLOR) {
                return i;
            }
        }
        return DEF_COLOR;
    }

    private GradientDrawable.Orientation int2Orientation(int orientation) {
        if (orientation == 1) return GradientDrawable.Orientation.TOP_BOTTOM;
        else if (orientation == 2) return GradientDrawable.Orientation.TR_BL;
        else if (orientation == 3) return GradientDrawable.Orientation.RIGHT_LEFT;
        else if (orientation == 4) return GradientDrawable.Orientation.BR_TL;
        else if (orientation == 5) return GradientDrawable.Orientation.BOTTOM_TOP;
        else if (orientation == 6) return GradientDrawable.Orientation.BL_TR;
        else if (orientation == 7) return GradientDrawable.Orientation.LEFT_RIGHT;
        else return GradientDrawable.Orientation.TL_BR;
    }

    /**
     * 设置文字选择器
     */
    private void setTextColorStateList() {
        if (mView instanceof TextView) {
            TextView textView = (TextView) mView;
            final int defaultTextColor = textView.getTextColors().getDefaultColor();
            if (mTextColorEnabled != DEF_COLOR || mTextColorPressed != DEF_COLOR || mTextColorChecked != DEF_COLOR) {
                int[][] states = {{-android.R.attr.state_enabled}, {android.R.attr.state_pressed}, {android.R.attr.state_checked}, {}};
                int[] colors = {
                        firstNotDefColor(mTextColorEnabled, defaultTextColor),
                        firstNotDefColor(mTextColorPressed, defaultTextColor),
                        firstNotDefColor(mTextColorChecked, defaultTextColor),
                        defaultTextColor
                };
                ColorStateList colorStateList = new ColorStateList(states, colors);
                textView.setTextColor(colorStateList);
            }
        }
    }
}
