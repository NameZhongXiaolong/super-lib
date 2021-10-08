package com.github.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.DrawableRes;
import androidx.appcompat.widget.AppCompatTextView;

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
 * *  * <!--文字不可点击时的颜色-->
 * *  * <attr name="textColorEnabled" />
 * *  * <!--文字点击时的颜色-->
 * *  * <attr name="textColorPressed" />
 */
public class SuperTextView extends AppCompatTextView {

    private final ColorStateHelper mColorStateHelper;

    private final DrawableSizeHelper mDrawableSizeHelper;

    public SuperTextView(Context context) {
        this(context, null);
    }

    public SuperTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public SuperTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mColorStateHelper = ColorStateHelper.wrap(this, attrs);

        mDrawableSizeHelper = DrawableSizeHelper.create(this, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        setBackground(mColorStateHelper.getStateListDrawable(h));
    }

    public void setWrapCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        mDrawableSizeHelper.setWrapCompoundDrawables(left, top, right, bottom);
    }

    public void setWrapCompoundDrawables(@DrawableRes int left, @DrawableRes int top, @DrawableRes int right, @DrawableRes int bottom) {
        mDrawableSizeHelper.setWrapCompoundDrawables(left, top, right, bottom);
    }
}
