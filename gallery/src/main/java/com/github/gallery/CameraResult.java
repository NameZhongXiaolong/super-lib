package com.github.gallery;

import android.net.Uri;

/**
 * Created by ZhongXiaolong on 2021/1/27 10:19 AM.
 * <p>
 * 拍照成功
 */
class CameraResult {

    public CameraResult(String path, Uri uri) {
        this.path = path;
        this.uri = uri;
    }

    private final String path;
    private final Uri uri;

    public String getPath() {
        return path;
    }

    public Uri getUri() {
        return uri;
    }
}
