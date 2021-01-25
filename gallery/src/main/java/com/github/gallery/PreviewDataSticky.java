package com.github.gallery;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhongXiaolong on 2020/12/15 6:43 PM.
 * <p>
 * 图片预览 粘性事件
 */
class PreviewDataSticky {

    private List<String> photos;
    private String photoJson;

    public PreviewDataSticky(List<String> photos) {
        if (photos != null) {
            JSONArray jsonArray = new JSONArray();
            for (String photo : photos) {
                jsonArray.put(photo);
            }
            photoJson = jsonArray.toString();
        }
    }

    public List<String> getPhotos() {
        if (photos == null) {
            photos = new ArrayList<>();
            if (!TextUtils.isEmpty(photoJson)) {
                try {
                    JSONArray jsonArray = new JSONArray(photoJson);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        photos.add(jsonArray.get(i).toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        return photos;
    }
}
