package com.github.nzxl.imagepicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by ZhongXiaolong on 2021/06/25 11:33 AM.
 */
class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private final List<MediaImage> mData;
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private final int mSize;
    private final GradientDrawable mPlaceholderDrawable;

    public ImageAdapter(Context context, int spanCount) {
        mData = new ArrayList<>();
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
        mSize = widthPixels / spanCount;
        mPlaceholderDrawable = new GradientDrawable();
        mPlaceholderDrawable.setColor(Color.parseColor("#EDEDED"));
        mPlaceholderDrawable.setSize(mSize, mSize);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.image_picker_item_image, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MediaImage mediaImage = mData.get(position);
        ImageView imageView = holder.findViewById(R.id.iv_image);
        TextView tvSize = holder.findViewById(R.id.tv_size);

        Glide.with(mContext)
                .load(mediaImage.getUri())
                .override(mSize)
                .placeholder(mPlaceholderDrawable)
                .error(mPlaceholderDrawable)
                .into(imageView);

        if (mediaImage.getSize()>1024) {
            long k = mediaImage.getSize() / 1024;
            if (k > 1024) {
                tvSize.setText((k/1024 + "m"));

            }else{
                tvSize.setText((k + "k"));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<MediaImage> data) {
        mData.clear();
        if (data != null) {
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        private final SparseArray<View> mViewSparseArray;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mViewSparseArray = new SparseArray<>();
        }

        @SuppressWarnings("unchecked")
        <T extends View> T findViewById(@IdRes int id) {
            T view = (T) mViewSparseArray.get(id);
            if (view == null) {
                view = itemView.findViewById(id);
                mViewSparseArray.put(id, view);
            }
            return view;
        }
    }
}
