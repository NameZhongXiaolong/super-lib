package com.github.gallery;

import android.text.TextUtils;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;

/**
 * Created by ZhongXiaolong on 2020/4/16 17:08.
 *
 * 图片路径信息
 */
class PhotoData {

    /**
     * 图片的父目录(也可以是"全部")
     */
    private String parentName;

    /**
     * 图片的标签,根据parentName获取
     */
    private String catalog;

    /**
     * 图片在parentName路径下的所有图片
     */
    private List<MediaImage> photoList;

    public String getCatalog() {
        if (TextUtils.isEmpty(catalog)) {
            catalog = parentName;
        }
        return catalog;
    }

    PhotoData setCatalog(String catalog) {
        this.catalog = catalog;
        return this;
    }

    List<MediaImage> getPhotoList() {
        return photoList;
    }

    PhotoData setPhotoList(List<MediaImage> photoList) {
        this.photoList = photoList;
        return this;
    }

    String getParentName() {
        return parentName;
    }

    PhotoData setParentName(String parentName) {
        this.parentName = parentName;
        return this;
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
        return getCatalog() + ("（" + (photoList != null ? photoList.size() : 0) + "）");
    }
}
