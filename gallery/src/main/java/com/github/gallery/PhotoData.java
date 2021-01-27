package com.github.gallery;

import android.text.TextUtils;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;

/**
 * Created by ZhongXiaolong on 2020/4/16 17:08.
 */
class PhotoData {

    private String catalog;
    private String path;
    private List<String> photoList;

    public String getCatalog() {
        if (TextUtils.isEmpty(catalog)) {
            catalog = path;
        }
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

    String getPath() {
        return path;
    }

    PhotoData setPath(String path) {
        this.path = path;
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
