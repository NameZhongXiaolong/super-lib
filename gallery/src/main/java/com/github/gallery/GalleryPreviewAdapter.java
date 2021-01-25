package com.github.gallery;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static android.graphics.drawable.GradientDrawable.RECTANGLE;

/**
 * Created by ZhongXiaolong on 2020/4/21 11:41.
 */
public class GalleryPreviewAdapter extends RecyclerView.Adapter<GalleryPreviewAdapter.PreviewPhotoHolder> {

    private List<String> mData;
    private final int dp60 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, Resources.getSystem().getDisplayMetrics());
    private final int dp10 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, Resources.getSystem().getDisplayMetrics());
    private OnItemClickListener mOnItemClickListener;
    private int mCheckedPosition = -1;

    GalleryPreviewAdapter(List<String> data, OnItemClickListener onItemClickListener) {
        mData = data;
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public PreviewPhotoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        ImageView imageView = new ImageView(context);
        imageView.setPadding(3, 3, 3, 3);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setAdjustViewBounds(true);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(dp60, dp60);
        params.setMargins(dp10, dp10, dp10, dp10);
        imageView.setLayoutParams(params);
        return new PreviewPhotoHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewPhotoHolder previewPhotoHolder, int position) {
        ImageView itemView = (ImageView) previewPhotoHolder.itemView;
        Glide.with(itemView).load(mData.get(position)).centerCrop().into(itemView);
        itemView.setOnClickListener(v -> mOnItemClickListener.onItemClick(mData.get(position), position));
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(RECTANGLE);
        gradientDrawable.setColor(Color.parseColor("#EDEDED"));
        gradientDrawable.setStroke(3, mCheckedPosition == position ? Color.parseColor("#FF0C0C") : Color.TRANSPARENT);
        itemView.setBackground(gradientDrawable);
    }

    void setChecked(int checkedPosition) {
        int tmpPosition = mCheckedPosition;
        mCheckedPosition = checkedPosition;
        if (tmpPosition != mCheckedPosition) {
            if (tmpPosition >= 0) notifyItemChanged(tmpPosition);
            if (mCheckedPosition >= 0) notifyItemChanged(mCheckedPosition);
        }
    }

    int getCheckedPosition() {
        return mCheckedPosition;
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    interface OnItemClickListener {
        void onItemClick(String photo, int position);
    }

    static class PreviewPhotoHolder extends RecyclerView.ViewHolder{
         PreviewPhotoHolder(@NonNull ImageView itemView) {
            super(itemView);
        }
    }
}
