package com.github.gallery;

/**
 * Created by ZhongXiaolong on 2021/1/27 11:15 AM.
 * <p>
 * 裁剪结果
 */
class CropResult {

    public CropResult(String path) {
        this.path = path;
    }

    private final String path;

    public String getPath() {
        return path;
    }
}
