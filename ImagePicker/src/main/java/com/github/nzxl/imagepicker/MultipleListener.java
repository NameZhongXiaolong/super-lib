package com.github.nzxl.imagepicker;

import java.util.List;

/**
 * Created by ZhongXiaolong on 2021/05/31 23:49 PM.
 * <p>
 * 多选监听
 */
public interface MultipleListener {

    void onCallback(List<MediaImage> mediaImages);

}
