package com.github.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;

/**
 * Created by ZhongXiaolong on 2022/03/27 21:06.
 */
public class BaseDialog extends AppCompatDialog {

    private FrameLayout mContainerLayout;

    //背景色
    private View mBackgroundView;

    private boolean mCancelable = true;

    private boolean mCanceledOnTouchOutside = true;

    /**
     * 阴影透明度0～1
     */
    private float mDimAmount = 0.65f;

    public BaseDialog(Context context) {
        super(context, R.style.Theme_BaseDialog);
    }

    @Override
    final public void setContentView(View view) {
        super.setContentView(wrapInLayout(0, view, null));
    }

    @Override
    final public void setContentView(int layoutResID) {
        super.setContentView(wrapInLayout(layoutResID, null, null));
    }

    @Override
    final public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(wrapInLayout(0, view, params));
    }

    final public void setContentView(View view, FrameLayout.LayoutParams params) {
        super.setContentView(wrapInLayout(0, view, params));
    }

    private View wrapInLayout(int layoutResId, @Nullable View view, @Nullable ViewGroup.LayoutParams params) {
        mContainerLayout = new FrameLayout(getContext());
        mContainerLayout.setBackgroundColor(Color.TRANSPARENT);
        mContainerLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mBackgroundView = new View(getContext());
        mContainerLayout.addView(mBackgroundView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (layoutResId != 0) {
            LayoutInflater.from(getContext()).inflate(layoutResId, mContainerLayout, true);
        } else if (params != null) {
            mContainerLayout.addView(view, params);
        } else if (view != null) {
            mContainerLayout.addView(view);
        }
        return mContainerLayout;
    }

    public FrameLayout getContainerLayout(){
        return mContainerLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置空白位置点击事件
        mContainerLayout.setOnClickListener(this::onContainerLayoutClick);
        //设置背景色
        setDimAmount(mDimAmount);
        mBackgroundView.setClickable(false);

        int childCount = mContainerLayout.getChildCount();
        if (childCount <= 1) {
            return;
        }

        View containerView = mContainerLayout.getChildAt(1);
        containerView.setClickable(true);
    }

    /**
     * 空白位置点击事件
     */
    private void onContainerLayoutClick(View view) {
        if (mCancelable && mCanceledOnTouchOutside) {
            dismiss();
        }
    }

    @Override
    public void setCancelable(boolean flag) {
        super.setCancelable(flag);
        mCancelable = true;
    }

    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
        mCanceledOnTouchOutside = true;
    }

    public BaseDialog setDimAmount(float dimAmount) {
        mDimAmount = dimAmount;
        if (mBackgroundView != null) {
            ColorDrawable colorDrawable = new ColorDrawable(Color.BLACK);
            colorDrawable.setAlpha((int) (255 * mDimAmount));
            mBackgroundView.setBackground(colorDrawable);
        }
        Window window = getWindow();
        if (window != null) {
            window.setStatusBarColor(Color.argb((int) (255 * mDimAmount), 0, 0, 0));
        }
        return this;
    }
}
