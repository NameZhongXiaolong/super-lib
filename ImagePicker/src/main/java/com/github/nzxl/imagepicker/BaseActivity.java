package com.github.nzxl.imagepicker;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import java.util.Collection;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by ZhongXiaolong on 2021/06/24 7:39 PM.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置竖屏
        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } catch (Exception e) {
            Log.d("BaseSuperActivity", "e:" + e);
        }
    }

    protected int getListSize(Collection<?> collection) {
        if (collection != null) {
            return collection.size();
        } else {
            return 0;
        }
    }
}
