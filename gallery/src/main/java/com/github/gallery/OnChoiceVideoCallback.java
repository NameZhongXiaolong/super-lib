package com.github.gallery;

import java.util.List;

/**
 * Created by ZhongXiaolong on 2021/3/8 10:24 AM.
 *
 * 接收视频回调
 */
public interface OnChoiceVideoCallback {

    void onChoiceVideoComplete(List<String> videos);

}
