package com.github.nzxl.imagepicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;

/**
 * Created by ZhongXiaolong on 2021/06/25 6:54 PM.
 */
public class ImageCatalog {

    private String canonicalFolder;

    private int size;
    private String title;

    public String getCanonicalFolder() {
        return canonicalFolder;
    }

    public String getTitle() {
        return title;
    }

    public static List<ImageCatalog> obtain(final ArrayMap<String, List<MediaImage>> dataMap) {
        List<ImageCatalog> data = new ArrayList<>();
        for (String key : dataMap.keySet()) {
            ImageCatalog imageCatalog = new ImageCatalog();
            imageCatalog.canonicalFolder = key;
            List<MediaImage> mediaImages = dataMap.get(key);
            imageCatalog.size = mediaImages != null ? mediaImages.size() : 0;
            int index = key.lastIndexOf("/");
            imageCatalog.title = (index > 0 ? key.substring(index + 1) : key) + "(" + imageCatalog.size + ")";
            data.add(imageCatalog);
        }
        Collections.sort(data, (o1, o2) -> Integer.compare(o2.size, o1.size));
        return data;
    }

    @NonNull
    @Override
    public String toString() {
        return title;
    }
}
