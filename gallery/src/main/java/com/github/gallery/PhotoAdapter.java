package com.github.gallery;

import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by ZhongXiaolong on 2020/4/16 11:13.
 *
 * 图片Adapter
 */
final class PhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public final static MediaImage SHOW_CAMERA = new MediaImage("com.github.gallery.camera");
    private final static int TYPE_CAMERA_HOLDER = 10;
    private final List<MediaImage> mData;
    private final List<MediaImage> mChoicePhotos;
    private final OnItemClickListener mOnItemClickListener;
    private int mImageSize;
    private boolean mShowCamera;

    PhotoAdapter(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        mData = new ArrayList<>();
        mChoicePhotos = new ArrayList<>();
    }

    /**
     * 是否显示拍照
     */
    public void showCamera(boolean show) {
        mShowCamera = show;
        if (show) {
            if (mData.size() > 0 && mData.get(0).equals(SHOW_CAMERA)) {
                return;
            }
            mData.add(0, SHOW_CAMERA);
            notifyDataSetChanged();
        } else{
            if (mData.remove(SHOW_CAMERA)) {
                notifyDataSetChanged();
            }
        }
    }

    public void add(int position, MediaImage mediaImage) {
        mData.add(position, mediaImage);
        notifyDataSetChanged();
    }

    /**
     * 重新设置图片路径
     */
    public PhotoAdapter setData(List<MediaImage> data) {
        mData.clear();
        if (mShowCamera) {
            mData.add(SHOW_CAMERA);
        }
        if (data != null) mData.addAll(data);
        notifyDataSetChanged();
        return this;
    }

    /**
     * 设置选中图片
     */
    void setChoicePhotos(List<MediaImage> choicePhotos) {
        List<MediaImage> tmpChoicePhotos = new ArrayList<>(mChoicePhotos);
        tmpChoicePhotos.addAll(choicePhotos);

        mChoicePhotos.clear();
        mChoicePhotos.addAll(choicePhotos);

        //局部刷新
        new ChoicePhotoDataAsyncTask(this).execute(tmpChoicePhotos.toArray(new MediaImage[]{}));
    }

    /**
     * 获取数据
     */
    List<MediaImage> getData() {
        return mData;
    }

    /**
     * 获取选中图片
     */
    List<MediaImage> getChoicePhotos() {
        return mChoicePhotos;
    }

    /**
     * 获取选中图片数量
     */
    int getChoicePhotoCount(){
        return mChoicePhotos.size();
    }

    /**
     * 判断是否选中
     */
    boolean getChecked(int position) {
        return mChoicePhotos.contains(mData.get(position));
    }

    /**
     * 根据下标获取图片路径
     */
    MediaImage getItem(int position) {
        return mData.get(position);
    }

    int indexOf(MediaImage photo) {
        return mData.indexOf(photo);
    }

    /**
     * 设置是否选中
     */
    void setChecked(int position, boolean isChecked) {
        MediaImage mediaImage = mData.get(position);
        if (isChecked) {
            if (!mChoicePhotos.contains(mediaImage)) {
                //添加成功刷新状态
                mChoicePhotos.add(mediaImage);
                notifyItemChanged(position);
            }
        } else {
            int index = mChoicePhotos.indexOf(mediaImage);
            if (index >= 0) {
                //删除成功刷新状态
                mChoicePhotos.remove(index);
                notifyItemChanged(position);
                //重新设置选中图片的排序
                for (int i = index; i < mChoicePhotos.size(); i++) {
                    notifyItemChanged(mData.indexOf(mChoicePhotos.get(i)));
                }
            }
        }
    }

    void setChecked(MediaImage path, boolean isChecked) {
        int position = mData.indexOf(path);
        if (position >= 0) {
            setChecked(position,isChecked);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.size() > 0) {
            if (SHOW_CAMERA.equals(mData.get(position))) {
                return TYPE_CAMERA_HOLDER;
            }
        }
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (mImageSize == 0) {
            mImageSize = viewGroup.getContext().getResources().getDisplayMetrics().widthPixels / 4;
        }
        if (viewType == TYPE_CAMERA_HOLDER) {
            return new CameraHolder(viewGroup, mOnItemClickListener);
        }
        return new PhotoHolder(viewGroup, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //if (holder instanceof CameraHolder) {
        //    CameraHolder cameraHolder = (CameraHolder) holder;
        //}
        if (holder instanceof PhotoHolder) {
            PhotoHolder photoHolder = (PhotoHolder) holder;
            final MediaImage mediaImage = mData.get(position);
            final int indexOfChoicePhotos = mChoicePhotos.indexOf(mediaImage);
            photoHolder.setImage(mediaImage, mImageSize);
            photoHolder.setChecked(indexOfChoicePhotos + 1, indexOfChoicePhotos >= 0);
            photoHolder.setButtonForeground(Color.parseColor(indexOfChoicePhotos >= 0 ? "#80000000" : "#00000000"));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * 点击事件
     */
    interface OnItemClickListener {
        void onItemClick(OnItemClickType type, int position);
    }

    enum OnItemClickType {
        CHECKED, CAMERA, OTHER,
    }

    /**
     * Holder
     */
    static class PhotoHolder extends RecyclerView.ViewHolder {

        private final ImageView mImage;
        private final View mButton;
        private final TextView mTvCheck;

        private PhotoHolder(@NonNull ViewGroup parent, final OnItemClickListener l) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item_photo, parent, false));
            mImage = itemView.findViewById(R.id.gallery_image_view);
            mButton = itemView.findViewById(R.id.gallery_view_selected);
            mTvCheck = itemView.findViewById(R.id.gallery_check_num);
            View button2 = itemView.findViewById(R.id.gallery_view);
            mButton.setOnClickListener(v -> l.onItemClick(OnItemClickType.OTHER, getAdapterPosition()));
            mTvCheck.setOnClickListener(v -> l.onItemClick(OnItemClickType.CHECKED, getAdapterPosition()));
            button2.setOnClickListener(v -> l.onItemClick(OnItemClickType.CHECKED, getAdapterPosition()));
        }

        private void setImage(MediaImage mediaImage, int overrideSize) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Uri uri = mediaImage.getUri();
                if (uri != null) {
                    Glide.with(mImage).load(uri).override(overrideSize).centerCrop().into(mImage);
                }
            } else {
                Glide.with(mImage).load(mediaImage.getAbsolutePath()).override(overrideSize).centerCrop().into(mImage);
            }
        }

        private void setChecked(int num, boolean isChecked) {
            mTvCheck.setText(num > 0 ? String.valueOf(num) : null);
            mTvCheck.setBackgroundResource(isChecked ? R.drawable.gallery_select_checked : R.drawable.gallery_select_normal);
        }

        private void setButtonForeground(int color) {
            mButton.setBackgroundColor(color);
        }
    }

    static class CameraHolder extends RecyclerView.ViewHolder{

        public CameraHolder(@NonNull ViewGroup parent, final OnItemClickListener l) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item_camera, parent, false));
            itemView.setOnClickListener(v -> l.onItemClick(OnItemClickType.CAMERA, getAdapterPosition()));
        }
    }

    static class ChoicePhotoDataAsyncTask extends AsyncTask<MediaImage, Void, Set<Integer>>{

        private final WeakReference<PhotoAdapter> mReferenceAdapter;

        private ChoicePhotoDataAsyncTask(PhotoAdapter photoAdapter) {
            mReferenceAdapter = new WeakReference<>(photoAdapter);
        }

        @Override
        protected Set<Integer> doInBackground(MediaImage... choicePhoto) {
            Set<Integer> positions = new HashSet<>();
            PhotoAdapter photoAdapter = mReferenceAdapter.get();
            if (photoAdapter != null) {
                for (MediaImage s : choicePhoto) {
                    positions.add(photoAdapter.mData.indexOf(s));
                }
            }
            return positions;
        }

        @Override
        protected void onPostExecute(Set<Integer> positions) {
            PhotoAdapter photoAdapter = mReferenceAdapter.get();
            if (photoAdapter != null) {
                for (Integer position : positions) {
                    if (position >= 0) {
                        photoAdapter.notifyItemChanged(position);
                    }
                }
            }
        }
    }
}
