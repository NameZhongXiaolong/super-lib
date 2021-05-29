package com.github.gallery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhongXiaolong on 2020/12/15 6:43 PM.
 * <p>
 * 图片预览 粘性事件
 */
class PreviewDataSticky {

    private final List<MediaImage> photos;

    public PreviewDataSticky(List<MediaImage> photos) {
        this.photos = new ArrayList<>();
        if (photos != null) {
            this.photos.addAll(photos);
        }
    }

    public List<MediaImage> getPhotos() {
        return photos;
    }
}
