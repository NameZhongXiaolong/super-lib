package com.github.gallery;

import android.net.Uri;

import java.util.List;

/**
 * Created by ZhongXiaolong on 2021/5/30 1:20.
 */
public interface OnGalleryUriCallback {

    void onChoiceGalleryComplete(List<Uri> uris);

}
