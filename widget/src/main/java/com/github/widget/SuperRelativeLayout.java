package com.github.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

/**
 * Created by ZhongXiaolong on 2021/10/9 3:33 下午.
 * <p>
 * 1.添加背景半圆和设置圆角,与app:backgroundColor一起使用生效:app:backgroundArc="true"--左右半圆; android:radius="@dimen/dp_10"--设置圆角
 * 2.添加选择器,点击效果/不可点击效果,描边
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
 * *  * <!--高度为宽度的比例-->
 * *  * <attr name="height_ratio_width" />
 * *  * <!--宽度为高度的比例-->
 * *  * <attr name="width_ratio_height" />
 */
public class SuperRelativeLayout extends RelativeLayout {

    private final SuperWidgetApi mSuperWidgetApi;

    public SuperRelativeLayout(Context context) {
        this(context, null);
    }

    public SuperRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuperRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SuperRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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