package com.github.nzxl.imagepicker;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import java.util.Objects;

import androidx.annotation.NonNull;

/**
 * Created by ZhongXiaolong on 2021/05/31 2:08 PM.
 * <p>
 * 图片信息,包含路径和uri
 */
public class MediaImage implements Parcelable{

    /**
     * 唯一id
     */
    private final int id;

    /**
     * 文件Uri
     */
    private final Uri uri;

    /**
     * 路径
     */
    private final String path;

    /**
     * 所在文件夹,完整路径
     */
    private final String canonicalFolder;

    /**
     * file上一层文件夹
     */
    private final String simpleFolder;

    /**
     * 大小,单位kb(k=size/1024,m=size/1024/1024)
     */
    private final long size;

    /**
     * 添加时间(秒)
     */
    private final long dateAdded;

    /**
     * 修改时间(秒)
     */
    private final long dateModified;

    /**
     * 文件名
     */
    private final String displayName;

    public MediaImage(int id, String path, long size, long dateAdded, long dateModified, String displayName) {
        this.id = id;
        this.path = path;
        this.size = size;
        this.dateAdded = dateAdded;
        this.dateModified = dateModified;
        this.displayName = displayName;
        uri = Uri.withAppendedPath(Uri.parse("content://media/external/images/media"), String.valueOf(id));
        canonicalFolder = path.substring(0, path.lastIndexOf("/"));
        simpleFolder = canonicalFolder.substring(canonicalFolder.lastIndexOf("/") + 1);
    }

    protected MediaImage(Parcel in) {
        id = in.readInt();
        uri = in.readParcelable(Uri.class.getClassLoader());
        path = in.readString();
        canonicalFolder = in.readString();
        simpleFolder = in.readString();
        size = in.readLong();
        dateAdded = in.readLong();
        dateModified = in.readLong();
        displayName = in.readString();
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

    public int getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public long getSize() {
        return size;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public long getDateModified() {
        return dateModified;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Uri getUri() {
        return uri;
    }

    public String getCanonicalFolder() {
        return canonicalFolder;
    }

    public String getSimpleFolder() {
        return simpleFolder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaImage that = (MediaImage) o;
        return id == that.id &&
                Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, path);
    }

    @NonNull
    @Override
    public String toString() {
        return "MediaImage{" +
                "id=" + id +
                ", uri=" + uri +
                ", path='" + path + '\'' +
                ", canonicalFolder='" + canonicalFolder + '\'' +
                ", simpleFolder='" + simpleFolder + '\'' +
                ", size=" + size +
                ", dateAdded='" + dateAdded + '\'' +
                ", dateModified='" + dateModified + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeParcelable(uri, flags);
        dest.writeString(path);
        dest.writeString(canonicalFolder);
        dest.writeString(simpleFolder);
        dest.writeLong(size);
        dest.writeLong(dateAdded);
        dest.writeLong(dateModified);
        dest.writeString(displayName);
    }
}
