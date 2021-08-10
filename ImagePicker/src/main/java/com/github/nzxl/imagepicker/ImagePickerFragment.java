package com.github.nzxl.imagepicker;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by ZhongXiaolong on 2021/05/31 11:31 AM.
 *
 * 图片选择器Fragment
 *
 * 通过Fragment.startActivityForResult启动图库,回调选择的数据给接口
 */
public class ImagePickerFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
