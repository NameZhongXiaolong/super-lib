package com.github.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
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
 * *  * <attr name="backgroundColorDisable" />
 * *  * <!--可点击时的背景颜色-->
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
 * *  * <attr name="strokeColorDisable" />
 * *  * <!--背景描边可点击时的颜色-->
 * *  * <attr name="strokeColorEnabled" />
 * *  * <!--背景描边点击时的颜色-->
 * *  * <attr name="strokeColorPressed" />
 * *  * <!--背景描边选中时的颜色-->
 * *  * <attr name="strokeColorChecked" />
 * *  * <!--文字不可点击时的颜色-->
 * *  * <attr name="textColorDisable" />
 * *  * <!--文字可点击时的颜色-->
 * *  * <attr name="textColorEnabled" />
 * *  * <!--文字点击时的颜色-->
 * *  * <attr name="textColorPressed" />
 * *  * <!--点击时的背景阴影透明度-->
 * *  * <attr name="backgroundPressedAlpha" />
 * */
public class SuperTextView extends AppCompatTextView {

    private final SuperWidgetApi mSuperWidgetApi;

    public SuperTextView(Context context) {
        this(context, null);
    }

    public SuperTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public SuperTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mSuperWidgetApi = SuperWidgetApi.wrap(this, attrs);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int[] measureSpec = mSuperWidgetApi.getMeasureSpec(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(measureSpec[0], measureSpec[1]);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        setBackground(mSuperWidgetApi.getStateListDrawable(h));
    }

    public void setWrapCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        mSuperWidgetApi.setWrapCompoundDrawables(left, top, right, bottom);
    }

    public void setWrapCompoundDrawables(@DrawableRes int left, @DrawableRes int top, @DrawableRes int right, @DrawableRes int bottom) {
        mSuperWidgetApi.setWrapCompoundDrawables(left, top, right, bottom);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(mSuperWidgetApi.getOnClickListener(0, l));
    }

    /**
     * 点击事件,响应间隔
     *
     * @param interval 两次点击的间隔,毫秒(第二次点击要大于这个间隔才有效)
     */
    public void setOnClickListener(int interval, @Nullable OnClickListener l) {
        super.setOnClickListener(mSuperWidgetApi.getOnClickListener(interval, l));
    }
}
