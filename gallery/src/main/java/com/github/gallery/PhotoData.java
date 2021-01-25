package com.github.gallery;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;

/**
 * Created by ZhongXiaolong on 2020/4/16 17:08.
 */
class PhotoData {

    private String catalog;
    private List<String> photoList;

    public String getCatalog() {
        return catalog;
    }

    PhotoData setCatalog(String catalog) {
        this.catalog = catalog;
        return this;
    }

    List<String> getPhotoList() {
        return photoList;
    }

    PhotoData setPhotoList(List<String> photoList) {
        this.photoList = photoList;
        return this;
    }

    String getChineseCatalog() {
        if ("WeiXin".equalsIgnoreCase(catalog)) return "微信";
        if ("pictures".equalsIgnoreCase(catalog)) return "图片(pictures)";
        if ("Screenshots".equalsIgnoreCase(catalog)) return "截屏";
        if ("Download".equalsIgnoreCase(catalog)) return "下载";
        if ("Camera".equalsIgnoreCase(catalog)) return "相机";
        if ("DCIM".equalsIgnoreCase(catalog)) return "相册(DCIM)";
        return catalog != null ? catalog : "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhotoData photoData = (PhotoData) o;
        return Objects.equals(catalog, photoData.catalog);
    }

    @Override
    public int hashCode() {
        return Objects.hash(catalog);
    }

    @NonNull
    @Override
    public String toString() {
        return getChineseCatalog() + ("（" + (photoList != null ? photoList.size() : 0) + "）");
    }
}
