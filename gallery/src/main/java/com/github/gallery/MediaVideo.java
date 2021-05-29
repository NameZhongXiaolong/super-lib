package com.github.gallery;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

import androidx.annotation.NonNull;

/**
 * Created by ZhongXiaolong on 2021/5/25 4:12 PM.
 * <p>
 * 视频信息详情
 */
public class MediaVideo implements Parcelable {

    /**
     * 视频的uri
     */
    private final Uri uri;

    /**
     * 视频的绝对路径
     */
    private final String filePath;

    /**
     * 视频长度
     */
    private final long length;

    public MediaVideo(String filePath, long length) {
        this.uri = null;
        this.filePath = filePath;
        this.length = length;
    }

    public MediaVideo(Uri uri, String filePath, long length) {
        this.uri = uri;
        this.filePath = filePath;
        this.length = length;
    }

    //不推荐使用用
    public MediaVideo(Context context, String filePath, long length) {
        this.uri = GalleryUtil.getImageContentUri(context, filePath);
        this.filePath = filePath;
        this.length = length;
    }

    protected MediaVideo(Parcel in) {
        uri = in.readParcelable(Uri.class.getClassLoader());
        filePath = in.readString();
        length = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(uri, flags);
        dest.writeString(filePath);
        dest.writeLong(length);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MediaVideo> CREATOR = new Creator<MediaVideo>() {
        @Override
        public MediaVideo createFromParcel(Parcel in) {
            return new MediaVideo(in);
        }

        @Override
        public MediaVideo[] newArray(int size) {
            return new MediaVideo[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaVideo mediaVideo = (MediaVideo) o;
        return length == mediaVideo.length &&
                Objects.equals(uri, mediaVideo.uri) &&
                Objects.equals(filePath, mediaVideo.filePath);
    }

    @NonNull
    @Override
    public String toString() {
        return "VideoInfo{" +
                "uri=" + uri +
                ", path='" + filePath + '\'' +
                ", length=" + length +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri, filePath, length);
    }

    public Uri getUri() {
        return uri;
    }

    public String getAbsolutePath() {
        return filePath;
    }

    public static Creator<MediaVideo> getCREATOR() {
        return CREATOR;
    }

    public long getLength() {
        return length;
    }
}
