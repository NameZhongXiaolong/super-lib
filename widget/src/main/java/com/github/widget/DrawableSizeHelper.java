package com.github.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

/**
 * Created by ZhongXiaolong on 2021/9/24 2:24 下午.
 */
class DrawableSizeHelper {

    private View mView;

    private int
            mDrawableStartWidth, mDrawableStartHeight,
            mDrawableEndWidth, mDrawableEndHeight,
            mDrawableTopWidth, mDrawableTopHeight,
            mDrawableBottomWidth, mDrawableBottomHeight;

    private DrawableSizeHelper() {}

    public static DrawableSizeHelper wrap(View view, AttributeSet attrs) {
        DrawableSizeHelper helper = new DrawableSizeHelper();
        final Context context = view.getContext();
        helper.mView = view;
        if (attrs != null) {
            for (int i = 0; i < attrs.getAttributeCount(); i++) {
                String attributeName = attrs.getAttributeName(i);
                if ("drawableStartWidth".equals(attributeName)) {
                    helper.mDrawableStartWidth = AttrsParseUtil.getDimensionPixelOffset(context, attrs, i);
                } else if ("drawableStartHeight".equals(attributeName)) {
                    helper.mDrawableStartHeight = AttrsParseUtil.getDimensionPixelOffset(context, attrs, i);
                } else if ("drawableEndWidth".equals(attributeName)) {
                    helper.mDrawableEndWidth = AttrsParseUtil.getDimensionPixelOffset(context, attrs, i);
                } else if ("drawableEndHeight".equals(attributeName)) {
                    helper.mDrawableEndHeight = AttrsParseUtil.getDimensionPixelOffset(context, attrs, i);
                } else if ("drawableTopWidth".equals(attributeName)) {
                    helper.mDrawableTopWidth = AttrsParseUtil.getDimensionPixelOffset(context, attrs, i);
                } else if ("drawableTopHeight".equals(attributeName)) {
                    helper.mDrawableTopHeight = AttrsParseUtil.getDimensionPixelOffset(context, attrs, i);
                } else if ("drawableBottomWidth".equals(attributeName)) {
                    helper.mDrawableBottomWidth = AttrsParseUtil.getDimensionPixelOffset(context, attrs, i);
                } else if ("drawableBottomHeight".equals(attributeName)) {
                    helper.mDrawableBottomHeight = AttrsParseUtil.getDimensionPixelOffset(context, attrs, i);
                }
            }
        }

        helper.resetDrawable();

        return helper;
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
}
