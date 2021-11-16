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
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

/**
 * Created by ZhongXiaolong on 2021/9/23 3:17 下午.
 * <p>
 * 自定义View属性解析/设置以及相关api
 */
class SuperWidgetApi {

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
    private int mBackgroundColorDisable = DEF_COLOR;
    private int mBackgroundColorPressed = DEF_COLOR;
    private int mBackgroundColorChecked = DEF_COLOR;
    private int mStrokeWidth;
    private int mStrokeColor = DEF_COLOR;
    private int mStrokeColorEnabled = DEF_COLOR;
    private int mStrokeColorDisable = DEF_COLOR;
    private int mStrokeColorPressed = DEF_COLOR;
    private int mStrokeColorChecked = DEF_COLOR;
    private int mTextColorEnabled = DEF_COLOR;
    private int mTextColorDisable = DEF_COLOR;
    private int mTextColorPressed = DEF_COLOR;
    private int mTextColorChecked = DEF_COLOR;
    private GradientDrawable.Orientation mBackgroundOrientation = GradientDrawable.Orientation.TL_BR;
    private int[] mBackgroundColors;
    private float mRatioWidthToHeight, mRatioHeightToWidth;
    private int
            mDrawableStartWidth, mDrawableStartHeight,
            mDrawableEndWidth, mDrawableEndHeight,
            mDrawableTopWidth, mDrawableTopHeight,
            mDrawableBottomWidth, mDrawableBottomHeight;

    public static SuperWidgetApi wrap(View view, AttributeSet attrs) {
        final Context context = view.getContext();
        final SuperWidgetApi helper = new SuperWidgetApi();
        boolean resetDrawable = false;
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
                } else if ("backgroundColorDisable".equals(attributeName)) {
                    helper.mBackgroundColorDisable = AttrsParseUtil.getColor(context, attrs, i);
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
                } else if ("strokeColorDisable".equals(attributeName)) {
                    helper.mStrokeColorDisable = AttrsParseUtil.getColor(context, attrs, i);
                } else if ("strokeColorPressed".equals(attributeName)) {
                    helper.mStrokeColorPressed = AttrsParseUtil.getColor(context, attrs, i);
                } else if ("strokeColorChecked".equals(attributeName)) {
                    helper.mStrokeColorChecked = AttrsParseUtil.getColor(context, attrs, i);
                } else if ("textColorEnabled".equals(attributeName)) {
                    helper.mTextColorEnabled = AttrsParseUtil.getColor(context, attrs, i);
                } else if ("textColorDisable".equals(attributeName)) {
                    helper.mTextColorDisable = AttrsParseUtil.getColor(context, attrs, i);
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
                } else if ("drawableStartWidth".equals(attributeName)) {
                    helper.mDrawableStartWidth = AttrsParseUtil.getDimensionPixelOffset(context, attrs, i);
                    resetDrawable = true;
                } else if ("drawableStartHeight".equals(attributeName)) {
                    helper.mDrawableStartHeight = AttrsParseUtil.getDimensionPixelOffset(context, attrs, i);
                    resetDrawable = true;
                } else if ("drawableEndWidth".equals(attributeName)) {
                    helper.mDrawableEndWidth = AttrsParseUtil.getDimensionPixelOffset(context, attrs, i);
                    resetDrawable = true;
                } else if ("drawableEndHeight".equals(attributeName)) {
                    helper.mDrawableEndHeight = AttrsParseUtil.getDimensionPixelOffset(context, attrs, i);
                    resetDrawable = true;
                } else if ("drawableTopWidth".equals(attributeName)) {
                    helper.mDrawableTopWidth = AttrsParseUtil.getDimensionPixelOffset(context, attrs, i);
                    resetDrawable = true;
                } else if ("drawableTopHeight".equals(attributeName)) {
                    helper.mDrawableTopHeight = AttrsParseUtil.getDimensionPixelOffset(context, attrs, i);
                    resetDrawable = true;
                } else if ("drawableBottomWidth".equals(attributeName)) {
                    helper.mDrawableBottomWidth = AttrsParseUtil.getDimensionPixelOffset(context, attrs, i);
                    resetDrawable = true;
                } else if ("drawableBottomHeight".equals(attributeName)) {
                    helper.mDrawableBottomHeight = AttrsParseUtil.getDimensionPixelOffset(context, attrs, i);
                    resetDrawable = true;
                } else if ("width_ratio_height".equals(attributeName)) {
                    String attributeValue = attrs.getAttributeValue(i);
                    int index = attributeValue.indexOf(":");
                    if (index > 0) {
                        float arg1 = ObjUtil.parseFloat(attributeValue.substring(0, index));
                        float arg2 = ObjUtil.parseFloat(attributeValue.substring(index + 1));
                        if (arg1 != 0 && arg2 != 0) {
                            helper.mRatioWidthToHeight = arg1 / arg2;
                        }
                    } else {
                        helper.mRatioWidthToHeight = ObjUtil.parseFloat(attributeValue);
                    }
                } else if ("height_ratio_width".equals(attributeName)) {
                    String attributeValue = attrs.getAttributeValue(i);
                    int index = attributeValue.indexOf(":");
                    if (index > 0) {
                        float arg1 = ObjUtil.parseFloat(attributeValue.substring(0, index));
                        float arg2 = ObjUtil.parseFloat(attributeValue.substring(index + 1));
                        if (arg1 != 0 && arg2 != 0) {
                            helper.mRatioHeightToWidth = arg1 / arg2;
                        }
                    } else {
                        helper.mRatioHeightToWidth = ObjUtil.parseFloat(attributeValue);
                    }
                }
            }
        }

        helper.mStrokeColorEnabled = helper.firstNotDefColor(helper.mStrokeColorEnabled, helper.mStrokeColor);
        helper.mStrokeColorDisable = helper.firstNotDefColor(helper.mStrokeColorDisable, helper.mStrokeColor);
        helper.mStrokeColorPressed = helper.firstNotDefColor(helper.mStrokeColorPressed, helper.mStrokeColor);
        helper.mStrokeColorChecked = helper.firstNotDefColor(helper.mStrokeColorChecked, helper.mStrokeColor);

        helper.setTextColorStateList();

        if (resetDrawable) {
            helper.resetDrawable();
        }

        return helper;
    }

    /**
     * 根据预设的属性获取颜色选择器
     */
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
                    newColors[1] = ColorUtils.blendARGB(mBackgroundColors[0], mBackgroundColors[1], 0.5f);
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

        //选中状态
        if (mBackgroundColorChecked != DEF_COLOR) {
            GradientDrawable enabledDrawable = getGradientDrawable(mBackgroundColorChecked, radii, mStrokeColorChecked);
            stateListDrawable.addState(new int[]{android.R.attr.state_checked}, enabledDrawable);
        }

        //可点击状态
        if (mBackgroundColorEnabled != DEF_COLOR) {
            GradientDrawable enabledDrawable = getGradientDrawable(mBackgroundColorEnabled, radii, mStrokeColorEnabled);
            stateListDrawable.addState(new int[]{android.R.attr.state_enabled}, enabledDrawable);
        }

        //不可点击状态
        if (mBackgroundColorDisable != DEF_COLOR) {
            GradientDrawable enabledDrawable = getGradientDrawable(mBackgroundColorDisable, radii, mStrokeColorDisable);
            stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, enabledDrawable);
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
        if (strokeColor != DEF_COLOR && strokeColor != Color.TRANSPARENT && mStrokeWidth > 0) {
            gradientDrawable.setStroke(mStrokeWidth, strokeColor);
        }
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
            List<Integer> states = new ArrayList<>();
            List<Integer> colors = new ArrayList<>();

            if (mTextColorPressed != DEF_COLOR) {
                states.add(android.R.attr.state_pressed);
                colors.add(mTextColorPressed);
            }
            if (mTextColorChecked != DEF_COLOR) {
                states.add(android.R.attr.state_checked);
                colors.add(mTextColorChecked);
            }
            if (mTextColorEnabled != DEF_COLOR) {
                states.add(android.R.attr.state_enabled);
                colors.add(mTextColorEnabled);
            }
            if (mTextColorDisable != DEF_COLOR) {
                states.add(-android.R.attr.state_enabled);
                colors.add(mTextColorDisable);
            }

            //当设置了选择器
            if (colors.size() > 0) {
                //设置新的选择器
                int[][] statesArr = new int[states.size() + 1][1];
                int[] colorsArr = new int[colors.size() + 1];
                for (int i = 0; i < states.size(); i++) {
                    statesArr[i] = new int[]{states.get(i)};
                    colorsArr[i] = colors.get(i);
                }

                //默认颜色
                final int defaultTextColor = textView.getTextColors().getDefaultColor();
                colorsArr[colorsArr.length - 1] = defaultTextColor;

                //设置
                ColorStateList colorStateList = new ColorStateList(statesArr, colorsArr);
                textView.setTextColor(colorStateList);
            }
        }
    }

    /**
     * 重新设置Drawable
     */
    private void resetDrawable() {
        if (!(mView instanceof TextView)) {
            return;
        }

        final TextView textView = (TextView) mView;

        Drawable[] compoundDrawablesRelative = textView.getCompoundDrawablesRelative();
        Drawable[] compoundDrawables = textView.getCompoundDrawables();
        Drawable drawableStart = ObjUtil.firstNotNull(compoundDrawablesRelative[0], compoundDrawables[0]);
        Drawable drawableTop = compoundDrawablesRelative[1];
        Drawable drawableEnd = ObjUtil.firstNotNull(compoundDrawablesRelative[2], compoundDrawables[2]);
        Drawable drawableBottom = compoundDrawablesRelative[3];

        textView.setCompoundDrawables(
                wrapDrawable(drawableStart, mDrawableStartWidth, mDrawableStartHeight),
                wrapDrawable(drawableTop, mDrawableTopWidth, mDrawableTopHeight),
                wrapDrawable(drawableEnd, mDrawableEndWidth, mDrawableEndHeight),
                wrapDrawable(drawableBottom, mDrawableBottomWidth, mDrawableBottomHeight));
    }

    /**
     * 获取自适应的Drawable
     */
    public Drawable wrapDrawable(Drawable drawable, int width, int height) {
        if (width > 0 || height > 0) {
            if (drawable != null) {
                if (width == 0) {
                    width = drawable.getIntrinsicWidth() * height / drawable.getIntrinsicHeight();
                    drawable.setBounds(0, 0, width, height);
                } else if (height == 0) {
                    height = drawable.getIntrinsicHeight() * width / drawable.getIntrinsicWidth();
                    drawable.setBounds(0, 0, width, height);
                } else {
                    drawable.setBounds(0, 0, width, height);
                }
            }
        } else {
            if (drawable != null) {
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            }
        }
        return drawable;
    }

    public void setWrapCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        if (!(mView instanceof TextView)) {
            return;
        }

        ((TextView) mView).
                setCompoundDrawables(
                        wrapDrawable(left, mDrawableStartWidth, mDrawableStartHeight),
                        wrapDrawable(top, mDrawableTopWidth, mDrawableTopHeight),
                        wrapDrawable(right, mDrawableEndWidth, mDrawableEndHeight),
                        wrapDrawable(bottom, mDrawableBottomWidth, mDrawableBottomHeight)
                );
    }

    public void setWrapCompoundDrawables(@DrawableRes int left, @DrawableRes int top, @DrawableRes int right, @DrawableRes int bottom) {
        setWrapCompoundDrawables(
                getDrawable(left),
                getDrawable(top),
                getDrawable(right),
                getDrawable(bottom)
        );
    }

    private Drawable getDrawable(@DrawableRes int drawableRes) {
        try {
            return ContextCompat.getDrawable(mView.getContext(), drawableRes);
        } catch (Exception e) {
            return null;
        }
    }

    public int[] getMeasureSpec(int widthMeasureSpec, int heightMeasureSpec) {
        if (mRatioHeightToWidth > 0) {
            int newHeightSize;
            if (mView.getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
                newHeightSize = (int) (mView.getMeasuredWidth() * mRatioHeightToWidth);
            } else {
                newHeightSize = (int) (View.MeasureSpec.getSize(widthMeasureSpec) * mRatioHeightToWidth);
            }
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(newHeightSize, View.MeasureSpec.EXACTLY);
        } else if (mRatioWidthToHeight > 0) {
            int newWidthSize;
            if (mView.getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                newWidthSize = (int) (mView.getMeasuredHeight() * mRatioWidthToHeight);
            } else {
                newWidthSize = (int) (View.MeasureSpec.getSize(heightMeasureSpec) * mRatioWidthToHeight);
            }
            widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(newWidthSize, View.MeasureSpec.EXACTLY);
        }

        return new int[]{widthMeasureSpec, heightMeasureSpec};
    }

    private long mOnClickTime;

    /**
     * 获取点击事件
     *
     * @param interval 两次点击的间隔
     * @param l        回调
     */
    public View.OnClickListener getOnClickListener(final int interval, final View.OnClickListener l) {
        if (l == null) {
            return null;
        } else if (interval <= 80) {
            return l;
        } else {
            return v -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - mOnClickTime > interval) {
                    //防止重复点击
                    l.onClick(v);
                    mOnClickTime = currentTime;
                }
            };
        }
    }
}
