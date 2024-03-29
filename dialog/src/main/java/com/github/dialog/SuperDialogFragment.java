package com.github.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import org.xmlpull.v1.XmlPullParser;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

/**
 * Created by ZhongXiaolong on 2022/03/27 21:06.
 * <p>
 * 基于{@link androidx.fragment.app.DialogFragment}修改的Dialog Fragment
 * 同样是用 Fragment维护Dialog,同时可以更好的处理dialog的宽高
 * 去掉在{@link #onStop()}时隐藏dialog逻辑
 * 布局文件根布局设置的layout_gravity就是弹窗的位置,不设置就获取弹窗设置的位置,还没有就设置居中
 * 使用{@link #show(FragmentActivity)}和{@link #show(Fragment)}注意tag的唯一性{@link #getDefTag()}
 */
public class SuperDialogFragment extends Fragment implements DialogInterface {

    //弹窗内容的layoutParams
    private FrameLayout.LayoutParams mCreateViewParams;

    //弹窗的位置
    private int mDialogGravity = Gravity.CENTER;

    private Dialog mDialog;

    private Boolean mShowing;

    private boolean mHiding;

    private boolean mSetContentViewTag;

    private DialogInterface.OnShowListener mOnShowListener;

    private DialogInterface.OnCancelListener mOnCancelListener;

    private DialogInterface.OnDismissListener mOnDismissListener;

    private Boolean mCancelable, mCanceledOnTouchOutside;

    /**
     * Fragment添加标记,用于处理Fragment重复添加和tag不同导致异常崩溃
     * {@link Fragment#isAdded()}是异步的,因此也不能用来判断
     */
    private boolean mAddedTag;

    @NonNull
    @Override
    public LayoutInflater onGetLayoutInflater(@Nullable Bundle savedInstanceState) {
        mDialog = onCreateDialog(savedInstanceState);

        //获取弹窗的位置
        if (mDialog instanceof SuperDialog) {
            mDialogGravity = ((SuperDialog) mDialog).getGravity();
            if (mDialogGravity == 0) {
                mDialogGravity = Gravity.CENTER;
            }
        } else {
            try {
                mDialogGravity = mDialog.getWindow().getAttributes().gravity;
                if (mDialogGravity == 0) {
                    mDialogGravity = Gravity.CENTER;
                }
            } catch (Exception e) {
                mDialogGravity = Gravity.CENTER;
            }
        }

        if (mCancelable != null) {
            setCancelable(mCancelable);
        }
        if (mCanceledOnTouchOutside != null) {
            setCanceledOnTouchOutside(mCanceledOnTouchOutside);
        }
        return new DialogLayoutInflater(this, super.onGetLayoutInflater(savedInstanceState));
    }

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new SuperDialog(getContext());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mCreateViewParams == null) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp != null) {
                mCreateViewParams = new FrameLayout.LayoutParams(lp);
            } else {
                mCreateViewParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }
        }

        if (mDialog != null) {
            mDialog.setOnDismissListener(this::onDismiss);
            mDialog.setOnCancelListener(this::onCancel);
            mDialog.setOnShowListener(this::onShow);

            boolean showing = false;
            if (mDialog instanceof SuperDialog) {
                //封装过的Dialog特殊处理
                //设置弹窗的位置
                if ((mDialog instanceof DialogBottomSheet)) {
                    ((SuperDialog) mDialog).setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                } else {
                    ((SuperDialog) mDialog).setGravity(mDialogGravity);
                }

                //设置弹窗(没有父容器的情况才设置,避免出现 The specified child already has a parent. You must call removeView() on the child‘s pare)
                if (view.getParent() == null) {
                    //设置布局的属性
                    view.setLayoutParams(mCreateViewParams);

                    //设置布局
                    mDialog.setContentView(view);

                    showing = true;
                }
            } else {
                //常规弹窗

                //去掉标题
                try {
                    if (mDialog instanceof AppCompatDialog) {
                        ((AppCompatDialog) mDialog).supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
                    } else {
                        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    }
                } catch (Exception ignored) {
                    //捕获异常,会存在崩溃闪退隐患
                }

                //设置弹窗(没有父容器的情况才设置,避免出现 The specified child already has a parent. You must call removeView() on the child‘s pare)
                if (view.getParent() == null) {
                    //设置布局的属性
                    view.setLayoutParams(mCreateViewParams);

                    //设置布局
                    mDialog.setContentView(view);

                    showing = true;
                }

                //默认弹窗还要重新设置一次宽高,如果是MATCH_PARENT就要获取屏幕的宽高
                int width = mCreateViewParams.width, height = mCreateViewParams.height;
                if (width == ViewGroup.LayoutParams.MATCH_PARENT) {
                    width = getResources().getDisplayMetrics().widthPixels;
                }
                if (height == ViewGroup.LayoutParams.MATCH_PARENT) {
                    DisplayMetrics metrics = new DisplayMetrics();
                    requireActivity().getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
                    height = metrics.heightPixels;
                }
                //设置窗体的尺寸
                mDialog.getWindow().setLayout(width, height);

                //去掉阴影
                mDialog.getWindow().setBackgroundDrawable(new ColorDrawable());

                //设置弹窗的位置
                mDialog.getWindow().setGravity(mDialogGravity);
            }

            mSetContentViewTag = true;

            //如果设置了show或者父容器为null调用Dialog.show
            if (mShowing == null) {
                if (showing) show();
            } else {
                if (mShowing) {
                    show();
                } else if (!mHiding) {
                    //没有show,判断是否是hide,再删除Fragment
                    final FragmentManager fragmentManager = getFragmentManager();
                    if (fragmentManager != null) fragmentManager.beginTransaction().remove(this).commitAllowingStateLoss();
                }
            }
        }
    }

    /**
     * 弹窗是否显示
     */
    public boolean isShowing() {
        return mShowing != null && mShowing && isAdded() && mDialog != null && mDialog.isShowing();
    }

    private void show() {
        mShowing = true;
        mHiding = false;

        try {
            if (mSetContentViewTag) mDialog.show();
        } catch (Exception ignored) {
        }
    }

    /**
     * show
     *
     * @param fragmentManager Fragment管理器,注意:如果传入DialogFragment的fragmentManager,在DialogFragment关闭/销毁后这个弹窗也会关闭
     *                        注意处理{@link Fragment#getChildFragmentManager()}异常(在未添加到Activity时会抛异常)
     * @param tag             显示标记,注意唯一性
     */
    public void show(FragmentManager fragmentManager, String tag) {
        //根据tag查找DialogFragment
        Fragment fragment = fragmentManager.findFragmentByTag(tag);

        if (fragment == this) {
            //查找的Fragment存在并且是将要添加的class,直接调用show()方法
            ((SuperDialogFragment) fragment).show();
        } else {
            if (mAddedTag) {
                if (isAdded()) {
                    //已经添加到FragmentManager中了,再调用一次show()
                    show();
                }
                return;
            }

            try {
                //添加
                mAddedTag = true;

                //show标记
                mShowing = true;

                fragmentManager.beginTransaction()
                        .add(this, tag)
                        .commitAllowingStateLoss();
            } catch (Exception e) {
                //添加异常标记设置回默认
                mAddedTag = false;
                mShowing = false;
            }
        }
    }

    /**
     * 关联FragmentActivity的显示,注意{@link #getDefTag()}的唯一性
     */
    public void show(FragmentActivity activity) {
        if (activity != null && !activity.isFinishing()) {
            show(activity.getSupportFragmentManager(), getDefTag());
        }
    }

    /**
     * 关联Fragment显示,注意{@link #getDefTag()}的唯一性
     * 如果Fragment没有添加,就会关联Activity;如果这个Fragment是个弹窗Fragment,会取上一级的非弹窗Fragment关联.详细逻辑看{@link #getShowCompatFragmentManager(Fragment)}
     * 如果一定要关联弹窗Fragment就使用{@link #show(FragmentManager, String)}传入Fragment.getChildFragmentManager()
     */
    public void show(Fragment fragment) {
        FragmentManager fragmentManager = SuperDialogFragment.getShowCompatFragmentManager(fragment);
        if (fragmentManager != null) {
            show(fragmentManager, getDefTag());
        }
    }

    /**
     * 通过Fragment获取{@link #show(Fragment)}传入的{@link FragmentManager}
     * 兼容DialogFragment,防止弹窗关联DialogFragment后关闭
     * 也尽可能地能去到非空的值(返回值要判空)
     */
    public static FragmentManager getShowCompatFragmentManager(Fragment fragment) {
        if (fragment == null) {
            return null;
        }

        if (fragment instanceof SuperDialogFragment || fragment instanceof DialogFragment) {
            //如果是弹窗Fragment,逐级往上找常规的Fragment
            Fragment parentFragment = fragment.getParentFragment();
            while (parentFragment != null) {
                if (!(parentFragment instanceof DialogFragment) && !(parentFragment instanceof SuperDialogFragment)) {
                    break;
                } else {
                    parentFragment = parentFragment.getParentFragment();
                }
            }
            if (parentFragment != null && parentFragment.isAdded()) {
                return parentFragment.getChildFragmentManager();
            } else {
                FragmentActivity fragmentActivity = fragment.getActivity();
                if (fragmentActivity != null && !fragmentActivity.isFinishing()) {
                    return fragmentActivity.getSupportFragmentManager();
                } else {
                    return null;
                }
            }
        } else if (fragment.isAdded()) {
            return fragment.getChildFragmentManager();
        } else if (fragment.getActivity() != null && !fragment.getActivity().isFinishing()) {
            return fragment.getActivity().getSupportFragmentManager();
        } else {
            return null;
        }
    }

    @Override
    public void cancel() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.cancel();
        } else if (isAdded()) {
            final FragmentManager fragmentManager = getFragmentManager();
            if (fragmentManager != null) fragmentManager.beginTransaction().remove(this).commitAllowingStateLoss();
        }
    }

    @Override
    public void dismiss() {
        mShowing = false;

        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        } else if (isAdded()) {
            final FragmentManager fragmentManager = getFragmentManager();
            if (fragmentManager != null) fragmentManager.beginTransaction().remove(this).commitAllowingStateLoss();
        }
    }

    public void hide() {
        mShowing = false;
        mHiding = true;

        if (mDialog != null && mDialog.isShowing()) {
            mDialog.hide();
        }
    }

    public void setOnShowListener(@Nullable DialogInterface.OnShowListener listener) {
        mOnShowListener = listener;
    }

    /**
     * dialog.show回调
     */
    protected void onShow(DialogInterface dialog) {
        if (mOnShowListener != null) {
            mOnShowListener.onShow(this);
        }
    }

    public void setOnCancelListener(@Nullable DialogInterface.OnCancelListener listener) {
        mOnCancelListener = listener;
    }

    /**
     * Dialog.cancel回调
     */
    protected void onCancel(DialogInterface dialog) {
        mShowing = false;
        if (mOnCancelListener != null) {
            mOnCancelListener.onCancel(this);
        }
    }

    public void setOnDismissListener(@Nullable DialogInterface.OnDismissListener listener) {
        mOnDismissListener = listener;
    }

    /**
     * Dialog.dismiss回调
     */
    protected void onDismiss(DialogInterface dialog) {
        mShowing = false;
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(this);
        }

        if (isAdded() && getFragmentManager() != null) {
            getFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
        }
    }

    @Override
    public void onDestroy() {
        if (mDialog != null && mDialog.isShowing()) {
            if (mDialog instanceof SuperDialog) {
                ((SuperDialog) mDialog).destroy();
            } else {
                mDialog.dismiss();
            }
        }
        //移除添加标记
        mAddedTag = false;
        super.onDestroy();
    }

    /**
     * Fragment添加的Tag,同一个页面多次使用注意唯一性
     */
    protected String getDefTag() {
        return getClass().getCanonicalName();
    }

    /**
     * 获取弹窗
     * 不要复写Dialog中的方法(用替代方法)
     * {@link Dialog#setOnDismissListener(DialogInterface.OnDismissListener)} ---> {@link #setOnDismissListener(DialogInterface.OnDismissListener)}
     * {@link Dialog#setOnCancelListener(DialogInterface.OnCancelListener)} ---> {@link #setOnCancelListener(DialogInterface.OnCancelListener)}
     * {@link Dialog#setOnShowListener(DialogInterface.OnShowListener)} ---> {@link #setOnShowListener(DialogInterface.OnShowListener)}
     */
    @Nullable
    public Dialog getDialog() {
        return mDialog;
    }

    /**
     * 获取弹窗
     * 不要复写Dialog中的方法(用替代方法)
     * {@link Dialog#setOnDismissListener(DialogInterface.OnDismissListener)} ---> {@link #setOnDismissListener(DialogInterface.OnDismissListener)}
     * {@link Dialog#setOnCancelListener(DialogInterface.OnCancelListener)} ---> {@link #setOnCancelListener(DialogInterface.OnCancelListener)}
     * {@link Dialog#setOnShowListener(DialogInterface.OnShowListener)} ---> {@link #setOnShowListener(DialogInterface.OnShowListener)}
     */
    @NonNull
    public final Dialog requireDialog() {
        if (mDialog == null) {
            throw new IllegalStateException("DialogFragment " + this + " does not have a Dialog.");
        }
        return mDialog;
    }

    /**
     * 设置为false返回键和外部点击不可关闭弹窗,会覆盖{@link #onCreateDialog(Bundle)}中返回dialog的setCancelable属性
     */
    public void setCancelable(boolean flag) {
        mCancelable = flag;
        if (mDialog != null) {
            mDialog.setCancelable(mCancelable);
        }
    }

    /**
     * 设置为false 返回键可关闭弹窗,点击外部不可关闭弹窗,会覆盖{@link #onCreateDialog(Bundle)}中返回dialog的setCanceledOnTouchOutside属性
     */
    public void setCanceledOnTouchOutside(boolean cancel) {
        mCanceledOnTouchOutside = cancel;
        if (mDialog != null) {
            mDialog.setCanceledOnTouchOutside(mCanceledOnTouchOutside);
        }
    }

    protected static class DialogLayoutInflater extends LayoutInflater {

        private final LayoutInflater mLayoutInflater;
        private final SuperDialogFragment mDialogFragment;

        public DialogLayoutInflater(SuperDialogFragment dialogFragment, LayoutInflater layoutInflater) {
            super(layoutInflater, layoutInflater.getContext());
            mDialogFragment = dialogFragment;
            mLayoutInflater = layoutInflater;
        }

        @Override
        public LayoutInflater cloneInContext(Context newContext) {
            return mLayoutInflater.cloneInContext(newContext);
        }

        @SuppressLint("ResourceType")
        @Override
        public View inflate(int resource, @Nullable ViewGroup root, boolean attachToRoot) {
            XmlResourceParser parser = getContext().getResources().getLayout(resource);
            try {
                int type;
                //过滤掉END_DOCUMENT和非START_TAG的事件
                while ((type = parser.next()) != XmlPullParser.END_DOCUMENT) {
                    if (type != XmlPullParser.START_TAG || mDialogFragment.mCreateViewParams != null) {
                        continue;
                    }

                    int[] attrs = {
                            android.R.attr.layout_width,
                            android.R.attr.layout_height,
                            android.R.attr.layout_margin,
                            android.R.attr.layout_marginLeft,
                            android.R.attr.layout_marginTop,
                            android.R.attr.layout_marginRight,
                            android.R.attr.layout_marginBottom,
                            android.R.attr.layout_marginStart,
                            android.R.attr.layout_marginEnd,
                    };
                    TypedArray a = getContext().obtainStyledAttributes(parser, attrs);
                    int width = a.getLayoutDimension(0, ViewGroup.LayoutParams.MATCH_PARENT);
                    int height = a.getLayoutDimension(1, ViewGroup.LayoutParams.WRAP_CONTENT);
                    int margin = a.getLayoutDimension(2, 0);
                    int marginLeft = a.getLayoutDimension(3, margin);
                    int marginTop = a.getLayoutDimension(4, margin);
                    int marginRight = a.getLayoutDimension(5, margin);
                    int marginBottom = a.getLayoutDimension(6, margin);
                    int marginStart = a.getLayoutDimension(7, 0);
                    int marginEnd = a.getLayoutDimension(8, 0);
                    a.recycle();

                    int attributeCount = parser.getAttributeCount();
                    for (int i = 0; i < attributeCount; i++) {
                        String attributeName = parser.getAttributeName(i);
                        if (attributeName.equals("layout_gravity")) {
                            int gravity = parser.getAttributeIntValue(i, 100);
                            if (gravity != 100) {
                                mDialogFragment.mDialogGravity = gravity;
                            }
                            break;
                        }
                    }

                    mDialogFragment.mCreateViewParams = new FrameLayout.LayoutParams(width, height);
                    mDialogFragment.mCreateViewParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
                    if (marginStart > 0) {
                        mDialogFragment.mCreateViewParams.setMarginStart(marginStart);
                    }
                    if (marginEnd > 0) {
                        mDialogFragment.mCreateViewParams.setMarginEnd(marginEnd);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return mLayoutInflater.inflate(resource, root, attachToRoot);
        }

        @Override
        public View inflate(int resource, @Nullable ViewGroup root) {
            return inflate(resource, root, root != null);
        }

        @Override
        public View inflate(XmlPullParser parser, @Nullable ViewGroup root) {
            return mLayoutInflater.inflate(parser, root);
        }

        @Override
        public View inflate(XmlPullParser parser, @Nullable ViewGroup root, boolean attachToRoot) {
            return mLayoutInflater.inflate(parser, root, attachToRoot);
        }
    }
}
