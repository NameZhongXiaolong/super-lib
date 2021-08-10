package com.github.nzxl.imagepicker;

import android.net.Uri;

import java.util.List;

/**
 * Created by ZhongXiaolong on 2021/05/31 23:49 PM.
 * <p>
 * 多选监听,返回Uri集合
 */
public interface MultipleUriListener {

    void onCallback(List<Uri> mediaImages);

}
