package com.github.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;

/**
 * Created by ZhongXiaolong on 2022/03/28 4:11 下午.
 * <p>
 * 对{@link SuperDialog}扩展
 * 底部弹窗的弹窗,做了适配,默认底部弹出.并且在{@link SuperDialogFragment}中无视布局文件位置直接在底部
 * 一般情况下项目中都会使用基类(例:MyBaseDialog)来继承{@link SuperDialog},为了保证继承性,可在自己的项目下新创建底部弹窗基类,继承MyBaseDialog,将适配的代码复制过去(注意要实现{@link DialogBottomSheet})
 */
public class BottomSheetDialog extends SuperDialog implements DialogBottomSheet {

    public BottomSheetDialog(Context context) {
        this(context, null);
    }

    /**
     * 构造器
     *
     * @param isLightStatusBar true状态栏浅色,黑色图标
     *                         false状态栏深色,白色图标
     */
    public BottomSheetDialog(Context context, boolean isLightStatusBar) {
        this(context, R.style.Theme_BaseDialog, isLightStatusBar);
    }

    /**
     * 构造器
     *
     * @param isLightStatusBar true状态栏浅色,黑色图标
     *                         false状态栏深色,白色图标
     *                         null跟随Activity
     */
    public BottomSheetDialog(Context context, Boolean isLightStatusBar) {
        this(context, R.style.Theme_BaseDialog, isLightStatusBar);
    }

    /**
     * 自定义主题,最好继承 R.style.Theme_BaseDialog
     *
     * @param isLightStatusBar true状态栏浅色,黑色图标
     *                         false状态栏深色,白色图标
     *                         null跟随Activity
     */
    public BottomSheetDialog(Context context, int theme, Boolean isLightStatusBar) {
        super(context, theme, isLightStatusBar);
        setDimAmount(SuperDialog.DEF_DIM_AMOUNT, false);
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
