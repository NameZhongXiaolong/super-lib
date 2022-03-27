package com.github.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import androidx.appcompat.app.AppCompatDialog;

/**
 * Created by ZhongXiaolong on 2022/03/27 21:06.
 */
public class BaseDialog extends AppCompatDialog {

    public BaseDialog(Context context) {
        super(context,R.style.Theme_BaseDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
