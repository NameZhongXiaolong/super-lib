package com.github.dialog;

import android.content.Context;
import android.view.Gravity;

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
}
