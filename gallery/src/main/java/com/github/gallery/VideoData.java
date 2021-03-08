package com.github.gallery;

import java.util.Objects;

/**
 * Created by ZhongXiaolong on 2021/3/8 6:12 PM.
 */
class VideoData {

    private final long length;
    private final String path;

    public VideoData(long length, String path) {
        this.length = length;
        this.path = path;
    }

    public long getLength() {
        return length;
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VideoData videoData = (VideoData) o;
        return length == videoData.length &&
                Objects.equals(path, videoData.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(length, path);
    }
}
