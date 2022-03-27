package com.github.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xmlpull.v1.XmlPullParser;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * Created by ZhongXiaolong on 2022/03/27 21:06.
 * <p>
 * 基于{@link androidx.fragment.app.DialogFragment}修改的Dialog Fragment
 * 同样是用 Fragment维护Dialog,同时可以更好的处理dialog的宽高
 * 去掉在{@link #onStop()}时隐藏dialog逻辑
 */
public class BaseDialogFragment extends Fragment {

    //默认的宽高
    private static final int DEF_SIZE = -100;

    private int mCreateViewWidth = DEF_SIZE, mCreateViewHeight = DEF_SIZE;

    private Dialog mDialog;

    private boolean mShowing;

    private DialogInterface.OnShowListener mOnShowListener;

    private DialogInterface.OnCancelListener mOnCancelListener;

    private DialogInterface.OnDismissListener mOnDismissListener;

    @NonNull
    @Override
    public LayoutInflater onGetLayoutInflater(@Nullable Bundle savedInstanceState) {
        mDialog = onCreateDialog(savedInstanceState);
        return new DialogLayoutInflater(this, super.onGetLayoutInflater(savedInstanceState));
    }

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BaseDialog(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //设置dialog布局的宽高
        if (mCreateViewWidth == DEF_SIZE) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp != null) {
                mCreateViewWidth = lp.width;
            }
        }

        if (mCreateViewHeight == DEF_SIZE) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp != null) {
                mCreateViewHeight = lp.height;
            }
        }

        if (mDialog != null) {
            mDialog.setOnDismissListener(this::onDismiss);
            mDialog.setOnCancelListener(this::onCancel);
            mDialog.setOnShowListener(this::onShow);
            //设置布局的宽高
            view.setLayoutParams(new ViewGroup.LayoutParams(mCreateViewWidth, mCreateViewHeight));
            //设置布局
            mDialog.setContentView(view);
            if (mShowing) {
                mDialog.show();
            }
        }
    }

    public boolean isShowing() {
        return mShowing;
    }

    private void show() {
        mShowing = true;
        try {
            mDialog.show();
        } catch (Exception ignored) {
        }
    }

    public void show(FragmentManager fragmentManager, String tag) {
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment instanceof BaseDialogFragment && Objects.equals(getClass().getCanonicalName(), fragment.getClass().getCanonicalName())) {
            ((BaseDialogFragment) fragment).show();
        } else {
            mShowing = true;
            fragmentManager.beginTransaction()
                    .add(this, tag)
                    .commitAllowingStateLoss();
        }
    }

    public void dismiss() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }

        if (isAdded() && getFragmentManager() != null) {
            getFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
        }
    }

    public void hide() {
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
            mOnShowListener.onShow(dialog);
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
            mOnCancelListener.onCancel(dialog);
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
            mOnDismissListener.onDismiss(dialog);
        }
    }

    @Override
    public void onDestroy() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        super.onDestroy();
    }

    protected static class DialogLayoutInflater extends LayoutInflater {

        private final LayoutInflater mLayoutInflater;
        private final BaseDialogFragment mDialogFragment;

        public DialogLayoutInflater(BaseDialogFragment dialogFragment, LayoutInflater layoutInflater) {
            super(layoutInflater, layoutInflater.getContext());
            mDialogFragment = dialogFragment;
            mLayoutInflater = layoutInflater;
        }

        @Override
        public LayoutInflater cloneInContext(Context newContext) {
            return mLayoutInflater.cloneInContext(newContext);
        }

        @Override
        public View inflate(int resource, @Nullable ViewGroup root, boolean attachToRoot) {
            XmlResourceParser parser = getContext().getResources().getLayout(resource);
            try {
                int type;
                //过滤掉END_DOCUMENT和非START_TAG的事件
                while ((type = parser.next()) != XmlPullParser.END_DOCUMENT) {
                    if (type != XmlPullParser.START_TAG || (mDialogFragment.mCreateViewWidth != DEF_SIZE && mDialogFragment.mCreateViewHeight != DEF_SIZE)) {
                        continue;
                    }

                    TypedArray a = getContext().obtainStyledAttributes(parser, new int[]{android.R.attr.layout_width, android.R.attr.layout_height});
                    mDialogFragment.mCreateViewWidth = a.getLayoutDimension(0, ViewGroup.LayoutParams.MATCH_PARENT);
                    mDialogFragment.mCreateViewHeight = a.getLayoutDimension(1, ViewGroup.LayoutParams.WRAP_CONTENT);
                    a.recycle();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return super.inflate(resource, root, attachToRoot);
        }
    }
}
