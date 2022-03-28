package com.github.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;

/**
 * Created by ZhongXiaolong on 2022/03/28 4:11 下午.
 * <p>
 * 底部弹窗的弹窗
 */
public class BaseBottomSheetDialog extends BaseDialog {

    public BaseBottomSheetDialog(Context context) {
        this(context, false);
    }

    public BaseBottomSheetDialog(Context context, boolean isLightStatusBar) {
        super(context, isLightStatusBar);
        setDimAmount(BaseDialog.DEF_DIM_AMOUNT, false);
        setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        setAnimations(R.anim.bottom_sheet_in, R.anim.bottom_sheet_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getDimAmountWithNavigationBar() && getContentView() != null) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) getContentView().getLayoutParams();
            lp.bottomMargin += getNavigationBarHeight();
            getContentView().setLayoutParams(lp);
        }
    }
}
