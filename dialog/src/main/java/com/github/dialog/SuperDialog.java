package com.github.dialog;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.TypedValue;
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

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.AnimRes;
import androidx.annotation.AnimatorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by ZhongXiaolong on 2022/03/27 21:06.
 * <p>
 * Base弹窗
 * 与其他弹窗不同,本弹窗实际是占满屏幕的主容器{@link #mContentView},背景{@link #mBackgroundView},setContentView添加到主容器中,
 * 用法与通用弹窗类似,布局文件可以设置位置、margin属性,
 * 需要在setContentView中对应的View设置好相应的位置宽高属性,避免传统Dialog需要调用{@link #getWindow()#setLayout(int, int)}等方法来实现位置,
 * 额外方法
 * {@link #setDimAmount(float, boolean)}设置空白部分阴影(即黑色的透明度),在导航栏是否可以设置
 * {@link #setLayout(int, int)}设置宽高,将会覆盖布局文件的宽高
 * {@link #setGravity(int)}设置弹窗的位置
 * {@link #setAnimations(int, int)}重写动画,原本的主题动画已失效,需在这里重写
 * {@link #destroy()}销毁方法,在组建销毁的时候调用一下,避免窗体泄漏
 * 推荐和{@link SuperDialogFragment}一起使用
 * <p>
 * 如果需要使用底部弹出框使用{@link BottomSheetDialog},进行了相关的兼容处理
 */
public class SuperDialog extends AppCompatDialog {

    /**
     * 默认的{@link Window#setDimAmount(float)}数值
     */
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
    private int mLayoutWidth, mLayoutHeight, mGravity;

    //背景
    private Drawable mContentViewBackground;

    //进场动画,出场动画
    private int mAnimEnter = 0, mAnimExit = 0;

    private final boolean mIsLightStatusBar;

    private final List<Integer> mContentLayoutResIds = new ArrayList<>();

    public SuperDialog(Context context) {
        this(context, false);
    }

    /**
     * 构造器
     *
     * @param isLightStatusBar true状态栏浅色,黑色图标
     *                         false状态栏深色,白色图标
     */
    public SuperDialog(Context context, boolean isLightStatusBar) {
        super(context, R.style.Theme_BaseDialog);
        mIsLightStatusBar = isLightStatusBar;
    }

    /**
     * 自定义主题,最好继承 R.style.Theme_BaseDialog
     *
     * @param isLightStatusBar true状态栏浅色,黑色图标
     *                         false状态栏深色,白色图标
     */
    protected SuperDialog(Context context, int theme, boolean isLightStatusBar) {
        super(context, theme);
        mIsLightStatusBar = isLightStatusBar;
    }

    /**
     * 确保View已经设置了{@link View#setLayoutParams(ViewGroup.LayoutParams)}属性,否则会导致窗体宽高与实际设置的不一致
     */
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
            mContentLayoutResIds.add(layoutResId);
            mContentView = LayoutInflater.from(getContext()).inflate(layoutResId, mContainerLayout, false);
            mContainerLayout.addView(mContentView);
        } else if (params != null) {
            mContentView = view;
            mContainerLayout.addView(view, params);
        } else if (view != null) {
            mContentView = view;
            if (view.getLayoutParams() != null) {
                mContainerLayout.addView(view);
            } else {
                mContainerLayout.addView(view, new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.CENTER));
            }
        }
        return mContainerLayout;
    }

    private void addContentView(int layoutResId, @Nullable View view, @Nullable FrameLayout.LayoutParams params) {
        if (mContainerLayout == null) {
            super.setContentView(wrapInLayout(layoutResId, view, params));
            return;
        }

        if (layoutResId != 0) {
            if (mContentLayoutResIds.contains(layoutResId)) {
                return;
            }
            mContentLayoutResIds.add(layoutResId);
            view = LayoutInflater.from(getContext()).inflate(layoutResId, mContainerLayout, false);
            params = (FrameLayout.LayoutParams) view.getLayoutParams();
        } else if (view == null) {
            return;
        }

        if (params == null) {
            params = new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.CENTER);
        }

        for (int i = 0; i < mContainerLayout.getChildCount(); i++) {
            View childView = mContainerLayout.getChildAt(i);
            if (childView == view || childView.equals(view)) {
                return;
            }
        }

        view.setClickable(true);

        if (mGravity != 0) {
            params.gravity = mGravity;
        }

        if (mLayoutWidth != 0 && mLayoutHeight != 0) {
            params.width = mLayoutWidth;
            params.height = mLayoutHeight;
        }

        if (mContentViewBackground != null) {
            view.setBackground(mContentViewBackground);
        }

        mContainerLayout.addView(view);
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        addContentView(0, view, new FrameLayout.LayoutParams(params));
    }

    public void addContentView(View view, FrameLayout.LayoutParams params) {
        addContentView(0, view, params);
    }

    public void addContentView(View view) {
        addContentView(0, view, null);
    }

    public void addContentView(int layoutResID) {
        addContentView(layoutResID, null, null);
    }

    public FrameLayout getContainerLayout() {
        return mContainerLayout;
    }

    @Override
    final public void onContentChanged() {
        super.onContentChanged();

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
        mCancelable = flag;
    }

    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
        mCanceledOnTouchOutside = cancel;
    }

    /**
     * 设置弹窗空白部分阴影透明度
     *
     * @param dimAmount     0～1之间,越深越明显
     * @param navigationBar 是否可以设置导航栏,true设置,false不设置
     */
    public SuperDialog setDimAmount(float dimAmount, boolean navigationBar) {
        mDimAmount = dimAmount;
        mDimAmountWithNavigationBar = navigationBar;
        if (mBackgroundView != null) {
            ColorDrawable colorDrawable = new ColorDrawable(Color.BLACK);
            colorDrawable.setAlpha((int) (255 * mDimAmount));
            mBackgroundView.setBackground(colorDrawable);
        }
        Window window = getWindow();
        if (window != null) {
            int visibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            if (mDimAmountWithNavigationBar) {
                visibility = visibility | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
                window.setNavigationBarColor(Color.TRANSPARENT);
            }
            if (mIsLightStatusBar && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                visibility = visibility | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            window.getDecorView().setSystemUiVisibility(visibility);
            window.setBackgroundDrawable(new ColorDrawable());
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setDimAmount(0);
        }

        return this;
    }

    public float getDimAmount() {
        return mDimAmount;
    }

    /**
     * 设置宽高,会使布局文件或View设置的宽高失效
     * {@link #getWindow()#setLayout(int, int)}已经失效使用这个
     */
    public SuperDialog setLayout(int width, int height) {
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
     * 设置位置,会使布局文件或View设置的位置失效
     * {@link #getWindow()#setGravity(int)}已经失效使用这个
     */
    public SuperDialog setGravity(int gravity) {
        mGravity = gravity;
        if (mContentView != null) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mContentView.getLayoutParams();
            lp.gravity = mGravity;
            mContentView.setLayoutParams(lp);
        }
        return this;
    }

    /**
     * 获取弹窗内容的位置
     */
    public int getGravity() {
        return mGravity;
    }

    /**
     * 设置内容背景,包括setContentView和addContentView
     */
    public SuperDialog setContentViewBackground(Drawable background) {
        mContentViewBackground = background;
        if (mContentView != null) {
            mContentView.setBackground(mContentViewBackground);
        }
        return this;
    }

    /**
     * 设置内容背景
     *
     * @param color    颜色
     * @param radiusDp 四个圆角,单位为dp
     */
    public SuperDialog setContentViewBackground(int color, int radiusDp) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, radiusDp, getContext().getResources().getDisplayMetrics()));
        return setContentViewBackground(drawable);
    }

    /**
     * 设置内容背景
     */
    public SuperDialog setContentViewBackgroundColor(int color) {
        return setContentViewBackground(new ColorDrawable(color));
    }

    /**
     * 设置进场、出场动画,在show方法前使用
     *
     * @param enter 进场动画
     * @param exit  出场动画
     */
    public SuperDialog setAnimations(@AnimatorRes @AnimRes int enter, @AnimatorRes @AnimRes int exit) {
        mAnimEnter = enter;
        mAnimExit = exit;
        return this;
    }

    /**
     * 读取动画,会为null
     */
    private Animation loadAnimation(@AnimatorRes @AnimRes int id) {
        Animation animation;
        try {
            animation = AnimationUtils.loadAnimation(getContext(), id);
        } catch (Exception e) {
            animation = null;
        }

        return animation;
    }

    private AlphaAnimation getAlphaAnimation(float fromAlpha, float toAlpha, long duration) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(fromAlpha, toAlpha);
        alphaAnimation.setDuration(duration);
        return alphaAnimation;
    }

    @Override
    public void show() {
        boolean showing = isShowing();
        super.show();
        if (!showing && mContentView != null) {
            Animation animation = loadAnimation(mAnimEnter);
            if (animation == null) {
                animation = getAlphaAnimation(0.5f, 1, 200);
            }
            AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
            alphaAnimation.setDuration(animation.getDuration());
            mBackgroundView.startAnimation(alphaAnimation);
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
            if (animation == null) {
                animation = getAlphaAnimation(1, 0, 200);
            }
            AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
            alphaAnimation.setDuration(animation.getDuration());
            mBackgroundView.startAnimation(alphaAnimation);
            mContentView.startAnimation(animation);
            mContentView.postDelayed(SuperDialog.super::dismiss, animation.getDuration());
            mContentView.postDelayed(() -> mOnAnimExitTag = false, animation.getDuration());
        } else {
            super.dismiss();
        }
    }

    @Override
    public void hide() {
        if (mOnAnimExitTag) {
            super.hide();
            return;
        }
        boolean showing = isShowing();
        if (showing && mContentView != null) {
            mOnAnimExitTag = true;
            Animation animation = loadAnimation(mAnimExit);
            if (animation == null) {
                animation = getAlphaAnimation(1, 0, 200);
            }
            AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
            alphaAnimation.setDuration(animation.getDuration());
            mBackgroundView.setAnimation(alphaAnimation);
            mContentView.startAnimation(animation);
            mContentView.postDelayed(SuperDialog.super::hide, animation.getDuration());
            mContentView.postDelayed(() -> mOnAnimExitTag = false, animation.getDuration());
        } else {
            super.hide();
        }
    }

    @Override
    public void cancel() {
        super.cancel();
    }

    public boolean getDimAmountWithNavigationBar() {
        return mDimAmountWithNavigationBar;
    }

    public int getNavigationBarHeight() {
        int navigationBarHeight = -1;
        Resources resources = getContext().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navigationBarHeight = resources.getDimensionPixelSize(resourceId);
        }
        return navigationBarHeight;
    }

    /**
     * 获取内容布局,在setContentView之后调用才不为null
     */
    public View getContentView() {
        return mContentView;
    }

    @NonNull
    @Override
    public <T extends View> T findViewById(int id) {
        T view = super.findViewById(id);
        if (view == null && mContainerLayout != null) {
            view = mContainerLayout.findViewById(id);
        }

        if (view == null) {
            String idKey;
            try {
                idKey = "R.id." + getContext().getResources().getResourceEntryName(id);
            } catch (Exception e) {
                idKey = String.valueOf(id);
            }

            throw new NullPointerException("No find view by id: " + idKey);
        }
        return view;
    }

    /**
     * 销毁之前没有调用{@link #dismiss()}的时候调用
     */
    public void destroy() {
        super.dismiss();
    }
}
