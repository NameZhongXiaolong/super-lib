package com.github.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;

/**
 * Created by ZhongXiaolong on 2020/9/2 4:31 PM.
 * <p>
 * dialog
 */
public class Dialog extends AppCompatDialog {

    public static final int BUTTON_POSITIVE = R.id.btn_negative;
    public static final int BUTTON_NEGATIVE = R.id.btn_positive;

    private Builder mBuilder;
    private Button mBtnNegative;
    private Button mBtnPositive;
    private TextView mTvTitle;
    private LinearLayout mLlRootView;
    private ViewStub mViewStub;
    private LinearLayout mLlButtonParent;

    Dialog(Builder builder) {
        super(builder.context);
        mBuilder = builder;
    }

    Dialog(Builder builder, int theme) {
        super(builder.context, theme);
        mBuilder = builder;
    }

    private Dialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable());

        setContentView(R.layout.dialog_super);
        mLlRootView = findViewById(R.id.ll_root);
        GradientDrawable background = new GradientDrawable();
        background.setColor(mBuilder.backgroundColor);
        background.setCornerRadius(mBuilder.backgroundCornerRadius);

        mTvTitle = findViewById(R.id.tv_title);
        mBtnNegative = findViewById(R.id.btn_negative);
        mBtnPositive = findViewById(R.id.btn_positive);
        mViewStub = findViewById(R.id.view_stub);
        mLlButtonParent = findViewById(R.id.ll_button_parent);

        mTvTitle.setText(mBuilder.title);
        mBtnNegative.setText(mBuilder.negative);
        mBtnPositive.setText(mBuilder.positive);

        mBtnPositive.setOnClickListener(this::onClick);
        mBtnNegative.setOnClickListener(this::onClick);

        mTvTitle.setVisibility(mTvTitle.length() == 0 ? View.GONE : View.VISIBLE);

        if (mBuilder.height != 0) mLlRootView.getLayoutParams().height = mBuilder.height;
        if (mBuilder.width != 0) mLlRootView.getLayoutParams().width = mBuilder.width;
        mLlRootView.setBackground(background);

        if (mBuilder instanceof ProgressBuilder) {
            createProgressView();
        }

        if (mBuilder instanceof AlertBuilder) {
            createAlertView();
        }

        if (mBuilder instanceof InputBuilder) {
            createInputView();
        }
    }

    private void createProgressView(){
        mViewStub.setLayoutResource(R.layout.dialog_progress);
        View view = mViewStub.inflate();
        TextView tvMsg = view.findViewById(R.id.tv_msg);
        tvMsg.setText(mBuilder.message);
        tvMsg.setVisibility(TextUtils.isEmpty(mBuilder.message) ? View.GONE : View.VISIBLE);
        mLlButtonParent.setVisibility(mBtnPositive.length() == 0 && mBtnNegative.length() == 0 ? View.GONE : View.VISIBLE);
    }

    private void createAlertView(){
        mViewStub.setLayoutResource(R.layout.dialog_alert);
        View view = mViewStub.inflate();
        TextView tvMsg = view.findViewById(R.id.tv_msg);
        tvMsg.setText(mBuilder.message);
    }

    private void createInputView(){
        mViewStub.setLayoutResource(R.layout.dialog_input);
        View view = mViewStub.inflate();
        EditText etMsg = view.findViewById(R.id.et_input);
        etMsg.setHint(mBuilder.message);
    }

    private void onClick(View view) {
        if (view.getId() == R.id.btn_negative) {
            if (mBuilder.negativeClickListener != null) {
                mBuilder.negativeClickListener.onClick(this,view.getId());
            }else{
                dismiss();
            }
        }
        if (view.getId() == R.id.btn_positive) {
            if (mBuilder.positiveClickListener != null) {
                mBuilder.positiveClickListener.onClick(this,view.getId());
            }else{
                dismiss();
            }
        }
    }

    public static class ProgressBuilder extends Builder {

        public ProgressBuilder(Context context) {
            super(context);
            negative = "";
            positive = "";
        }
    }

    public static class InputBuilder extends Builder {

        public InputBuilder(Context context) {
            super(context);
        }
    }

    public static class AlertBuilder extends Builder {

        public AlertBuilder(Context context) {
            super(context);
        }
    }

    public static class ListBuilder extends Builder {

        public ListBuilder(Context context) {
            super(context);
        }
    }

    public interface OnClickListener{
        void onClick(Dialog dialog, int id);
    }
}
