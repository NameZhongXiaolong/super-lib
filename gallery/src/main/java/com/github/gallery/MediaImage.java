package com.github.gallery;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

import androidx.annotation.NonNull;

/**
 * Created by ZhongXiaolong on 2021/5/25 2:53 PM.
 * <p>
 * 图片信息详情
 */
public class MediaImage implements Parcelable {

    /**
     * 图片的uri
     */
    private final Uri uri;

    /**
     * 图片的绝对路径
     */
    private final String filePath;

    MediaImage(String filePath) {
        this.uri = null;
        this.filePath = filePath;
    }

    public MediaImage(Uri uri, String filePath) {
        this.uri = uri;
        this.filePath = filePath;
    }

    //不推荐使用
    public MediaImage(Context context, String filePath) {
        this.uri = GalleryUtil.getImageContentUri(context, filePath);
        this.filePath = filePath;
    }

    protected MediaImage(Parcel in) {
        uri = in.readParcelable(Uri.class.getClassLoader());
        filePath = in.readString();
    }

    public Uri getUri() {
        return uri;
    }

    /**
     * 注意:android10以上版本只能拿到路径,不能获取file
     */
    public String getAbsolutePath() {
        return filePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaImage mediaImage = (MediaImage) o;
        return Objects.equals(uri, mediaImage.uri) && Objects.equals(filePath, mediaImage.filePath);
    }

    @NonNull
    @Override
    public String toString() {
        return "PhotoInfo{" +
                "uri=" + uri +
                ", path='" + filePath + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri, filePath);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(uri, flags);
        dest.writeString(filePath);
    }

    public static final Creator<MediaImage> CREATOR = new Creator<MediaImage>() {
        @Override
        public MediaImage createFromParcel(Parcel in) {
            return new MediaImage(in);
        }

        @Override
        public MediaImage[] newArray(int size) {
            return new MediaImage[size];
        }
    };
}
