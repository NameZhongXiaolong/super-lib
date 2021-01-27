package com.github.gallery;

/**
 * Created by ZhongXiaolong on 2021/1/27 10:19 AM.
 * <p>
 * 拍照成功
 */
class CameraResult {

    public CameraResult(String path) {
        this.path = path;
    }

    private final String path;

    public String getPath() {
        return path;
    }
}
