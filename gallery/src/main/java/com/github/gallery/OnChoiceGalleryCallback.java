package com.github.gallery;

import java.util.List;

/**
 * Created by ZhongXiaolong on 2019/12/30 17:19.
 *
 * 接收图片回调
 */
public interface OnChoiceGalleryCallback {

    void onChoiceGalleryComplete(List<MediaImage> mediaImages);

}
