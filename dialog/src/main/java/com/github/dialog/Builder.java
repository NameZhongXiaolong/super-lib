package com.github.dialog;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;

/**
 * Created by ZhongXiaolong on 2020/9/2 4:34 PM.
 */
class Builder {

    Context context;
    String title;
    String message;
    String positive;
    String negative;
    boolean canceledOnTouchOutside;
    boolean cancelable;
    @Gravity int gravity;
    int backgroundColor;
    int backgroundCornerRadius;
    int height;
    int width;
    Dialog.OnClickListener positiveClickListener;
    Dialog.OnClickListener negativeClickListener;

    public Builder(Context context) {
        this.context = context;
        backgroundColor = Color.WHITE;
        backgroundCornerRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, context.getResources().getDisplayMetrics());
        negative = "取消";
        positive = "确认";
    }

    public Builder setTitle(String title) {
        this.title = title;
        return this;
    }

    public Builder setMessage(String message) {
        this.message = message;
        return this;
    }

    public Builder setPositive(String positive, Dialog.OnClickListener positiveClickListener) {
        this.positiveClickListener = positiveClickListener;
        this.positive = positive;
        return this;
    }

    public Builder setNegative(String negative, Dialog.OnClickListener negativeClickListener) {
        this.negativeClickListener = negativeClickListener;
        this.negative = negative;
        return this;
    }

    public Builder setPositive(String positive) {
        this.positive = positive;
        return this;
    }

    public Builder setNegative(String negative) {
        this.negative = negative;
        return this;
    }

    public Builder setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        this.canceledOnTouchOutside = canceledOnTouchOutside;
        return this;
    }

    public Builder setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public Builder setLayout(int width, int height) {
        return setWidth(width).setHeight(height);
    }

    public Builder setHeight(int height) {
        this.height = height;
        return this;
    }

    public Builder setWidth(int width) {
        this.width = width;
        return this;
    }

    /**
     * 弹窗位置:
     * 居   中 {@link android.view.Gravity#CENTER}
     * 顶部居中 {@link android.view.Gravity#TOP}
     * 底部居中 {@link android.view.Gravity#BOTTOM}
     */
    public Builder setGravity(@Gravity int gravity) {
        this.gravity = gravity;
        return this;
    }

    /**
     * 设置背景颜色
     */
    public Builder setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    /**
     * 设置背景角度,单位dp
     */
    public Builder setBackgroundCornerRadius(int dp) {
        return setBackgroundCornerRadius(dp, TypedValue.COMPLEX_UNIT_DIP);
    }

    /**
     * 设置背景角度
     *
     * @param value 数值
     * @param unit  单位,{@link TypedValue#COMPLEX_UNIT_DIP},{@link TypedValue#COMPLEX_UNIT_PX},{@link TypedValue#COMPLEX_UNIT_PT}...
     */
    public Builder setBackgroundCornerRadius(int value, int unit) {
        this.backgroundCornerRadius = (int) TypedValue.applyDimension(unit, value, context.getResources().getDisplayMetrics());
        return this;
    }

    public Dialog create() {
        Dialog dialog = new Dialog(this);
        dialog.create();
        return dialog;
    }

    public Dialog show() {
        Dialog dialog = create();
        dialog.show();
        return dialog;
    }
}
