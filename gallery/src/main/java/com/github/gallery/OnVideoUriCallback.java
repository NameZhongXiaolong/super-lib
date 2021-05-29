package com.github.gallery;

import android.net.Uri;

import java.util.List;

/**
 * Created by ZhongXiaolong on 2021/5/30 1:41.
 */
public interface OnVideoUriCallback {

    void onChoiceVideoComplete(List<Uri> uris);

}
