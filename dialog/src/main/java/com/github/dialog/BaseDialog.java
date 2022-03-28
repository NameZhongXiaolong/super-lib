package com.github.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import androidx.annotation.AnimRes;
import androidx.annotation.AnimatorRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by ZhongXiaolong on 2022/03/27 21:06.
 * <p>
 * Base弹窗
 */
public class BaseDialog extends AppCompatDialog {

    public static final float DEF_DIM_AMOUNT = 0.65f;

    //内容容器
    private FrameLayout mContainerLayout;

    //内容布局
    private View mContentView;

    //背景色
    private View mBackgroundView;

    private boolean mCancelable = true;

    private boolean mCanceledOnTouchOutside = true;

    /**
     * 阴影透明度0～1
     */
    private float mDimAmount = DEF_DIM_AMOUNT;

    private boolean mDimAmountWithNavigationBar = true;

    //宽,高,位置
    private int mLayoutWidth, mLayoutHeight, mGravity = Gravity.CENTER;

    //背景
    private Drawable mContentViewBackground;

    //进场动画,出场动画
    private int mAnimEnter = 0, mAnimExit = 0;

    private final boolean mIsLightStatusBar;

    public BaseDialog(Context context) {
        this(context, false);
    }

    /**
     * 构造器
     *
     * @param isLightStatusBar true状态栏浅色,黑色图标
     *                         false状态栏深色,白色图标
     */
    public BaseDialog(Context context, boolean isLightStatusBar) {
        super(context, R.style.Theme_BaseDialog);
        mIsLightStatusBar = isLightStatusBar;
    }

    /**
     * 自定义主题,最好继承 R.style.Theme_BaseDialog
     *
     * @param isLightStatusBar true状态栏浅色,黑色图标
     *                         false状态栏深色,白色图标
     */
    protected BaseDialog(Context context, int theme, boolean isLightStatusBar) {
        super(context, theme);
        mIsLightStatusBar = isLightStatusBar;
    }

    @Override
    final public void setContentView(View view) {
        super.setContentView(wrapInLayout(0, view, null));
    }

    /**
     * 可以完整的读取布局文件的属性
     */
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
        mContainerLayout.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        mBackgroundView = new View(getContext());
        mContainerLayout.addView(mBackgroundView, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        if (layoutResId != 0) {
            LayoutInflater.from(getContext()).inflate(layoutResId, mContainerLayout, true);
        } else if (params != null) {
            mContainerLayout.addView(view, params);
        } else if (view != null) {
            mContainerLayout.addView(view);
        }
        return mContainerLayout;
    }

    public FrameLayout getContainerLayout() {
        return mContainerLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置空白位置点击事件
        mContainerLayout.setOnClickListener(this::onContainerLayoutClick);
        //设置阴影背景
        setDimAmount(mDimAmount, mDimAmountWithNavigationBar);
        mBackgroundView.setClickable(false);

        int childCount = mContainerLayout.getChildCount();
        if (childCount <= 1) {
            return;
        }

        mContentView = mContainerLayout.getChildAt(1);
        mContentView.setClickable(true);

        if (mGravity != 0) {
            setGravity(mGravity);
        }

        if (mLayoutWidth != 0 && mLayoutHeight != 0) {
            setLayout(mLayoutWidth, mLayoutHeight);
        }

        if (mContentViewBackground != null) {
            setContentViewBackground(mContentViewBackground);
        }
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

    /**
     * 设置弹窗空白部分阴影透明度
     *
     * @param dimAmount     0～1之间,越深越明显
     * @param navigationBar 是否可以设置导航栏,true设置,false不设置
     */
    public BaseDialog setDimAmount(float dimAmount, boolean navigationBar) {
        mDimAmount = dimAmount;
        mDimAmountWithNavigationBar = navigationBar;
        if (mDimAmountWithNavigationBar) {
            Window window = getWindow();
            if (window != null) {
                int visibility;
                if (mIsLightStatusBar && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    visibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else {
                    visibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE ;
                }
                window.getDecorView().setSystemUiVisibility(visibility);
                window.setNavigationBarColor(Color.TRANSPARENT);
                window.setBackgroundDrawable(new ColorDrawable());
                window.setDimAmount(mDimAmount);
                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
        } else {
            if (mBackgroundView != null) {
                ColorDrawable colorDrawable = new ColorDrawable(Color.BLACK);
                colorDrawable.setAlpha((int) (255 * mDimAmount));
                mBackgroundView.setBackground(colorDrawable);
            }
            Window window = getWindow();
            if (window != null) {
                int visibility;
                if (mIsLightStatusBar && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    visibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else {
                    visibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE ;
                }
                window.getDecorView().setSystemUiVisibility(visibility);
                window.setBackgroundDrawable(new ColorDrawable());
                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setDimAmount(0);
            }
        }
        return this;
    }

    public float getDimAmount() {
        return mDimAmount;
    }

    /**
     * 设置宽高
     * {@link #getWindow()#setLayout(int, int)}已经失效使用这个
     */
    public BaseDialog setLayout(int width, int height) {
        mLayoutWidth = width;
        mLayoutHeight = height;
        if (mContentView != null) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mContentView.getLayoutParams();
            lp.width = mLayoutWidth;
            lp.height = mLayoutHeight;
            mContentView.setLayoutParams(lp);
        }
        return this;
    }

    /**
     * 设置位置
     * {@link #getWindow()#setGravity(int)}已经失效使用这个
     */
    public BaseDialog setGravity(int gravity) {
        mGravity = gravity;
        if (mContentView != null) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mContentView.getLayoutParams();
            lp.gravity = mGravity;
            mContentView.setLayoutParams(lp);
        }
        return this;
    }

    /**
     * 设置内容背景
     */
    public BaseDialog setContentViewBackground(Drawable background) {
        mContentViewBackground = background;
        if (mContentView != null) {
            mContentView.setBackground(mContentViewBackground);
        }
        return this;
    }

    /**
     * 设置内容背景
     */
    public BaseDialog setContentViewBackgroundColor(int color) {
        return setContentViewBackground(new ColorDrawable(color));
    }

    /**
     * 设置进场、出场动画,在show方法前使用
     *
     * @param enter 进场动画
     * @param exit  出场动画
     */
    public BaseDialog setAnimations(@AnimatorRes @AnimRes int enter, @AnimatorRes @AnimRes int exit) {
        mAnimEnter = enter;
        mAnimExit = exit;
        return this;
    }

    /**
     * 读取动画
     */
    private Animation loadAnimation(@AnimatorRes @AnimRes int id) {
        Animation animation;
        try {
            animation = AnimationUtils.loadAnimation(getContext(), id);
        } catch (Exception e) {
            animation = null;
        }
        if (animation == null) {
            AlphaAnimation alphaAnimation = new AlphaAnimation(1, 1);
            alphaAnimation.setDuration(1);
            return alphaAnimation;
        } else {
            return animation;
        }
    }

    @Override
    public void show() {
        boolean showing = isShowing();
        super.show();
        if (!showing && mContentView != null) {
            Animation animation = loadAnimation(mAnimEnter);
            if (!mDimAmountWithNavigationBar) {
                AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
                alphaAnimation.setDuration(Math.max(animation.getDuration(), 200));
                mBackgroundView.startAnimation(alphaAnimation);
            }
            mContentView.startAnimation(animation);
        }
    }

    private boolean mOnAnimExitTag;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void dismiss() {
        if (mOnAnimExitTag) {
            super.dismiss();
            return;
        }
        boolean showing = isShowing();
        if (showing && mContentView != null) {
            mOnAnimExitTag = true;
            Animation animation = loadAnimation(mAnimExit);
            long durationMillis = Math.max(animation.getDuration(), 200);
            if (!mDimAmountWithNavigationBar) {
                AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
                alphaAnimation.setDuration(durationMillis);
                mBackgroundView.setAnimation(alphaAnimation);
            }
            mContentView.startAnimation(animation);
            mContentView.postDelayed(BaseDialog.super::dismiss, durationMillis);
            mContentView.postDelayed(() -> mOnAnimExitTag = false, durationMillis);
        } else {
            super.dismiss();
        }
    }

    @Override
    public void hide() {
        if (mOnAnimExitTag) {
            super.dismiss();
            return;
        }
        boolean showing = isShowing();
        if (showing && mContentView != null) {
            mOnAnimExitTag = true;
            Animation animation = loadAnimation(mAnimExit);
            long durationMillis = Math.max(animation.getDuration(), 200);
            if (!mDimAmountWithNavigationBar) {
                AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
                alphaAnimation.setDuration(durationMillis);
                mBackgroundView.setAnimation(alphaAnimation);
            }
            mContentView.startAnimation(animation);
            mContentView.postDelayed(BaseDialog.super::hide, durationMillis);
            mContentView.postDelayed(() -> mOnAnimExitTag = false, durationMillis);
        } else {
            super.hide();
        }
    }

    @Override
    public void cancel() {
        super.cancel();
    }

    /**
     * 销毁之前没有调用{@link #dismiss()}的时候调用
     */
    public void destroy() {
        super.dismiss();
    }
}
