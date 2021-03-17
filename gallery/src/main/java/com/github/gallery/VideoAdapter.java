package com.github.gallery;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by ZhongXiaolong on 2021/3/8 11:28 AM.
 */
final class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoHolder> {

    private final List<VideoData> mData;
    private final List<VideoData> mCheckedData;
    private final ColorDrawable mPlaceholderDrawable;
    private OnCheckedChangeListener mOnCheckedChangeListener;
    private OnItemClickListener mOnItemClickListener;
    private final DecimalFormat mDecimalFormat;

    public VideoAdapter() {
        mData = new ArrayList<>();
        mCheckedData = new ArrayList<>();
        mPlaceholderDrawable = new ColorDrawable(Color.parseColor("#EDEDED"));
        mDecimalFormat = new DecimalFormat("#0.00");
    }

    @NonNull
    @Override
    public VideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item_video, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VideoHolder holder, int position) {
        final VideoData datum = mData.get(position);

        Glide.with(holder.mContext).load(new File(datum.getPath())).placeholder(mPlaceholderDrawable).into(holder.mImageView);

        final int index = mCheckedData.indexOf(datum);

        if (index >= 0) {
            holder.mViewSelected.setVisibility(View.VISIBLE);
            holder.mTvCheckedNum.setText(String.valueOf(index + 1));
            holder.mTvCheckedNum.setBackgroundResource(R.drawable.gallery_select_checked);
        }else{
            holder.mViewSelected.setVisibility(View.INVISIBLE);
            holder.mTvCheckedNum.setText(null);
            holder.mTvCheckedNum.setBackgroundResource(R.drawable.gallery_select_normal);
        }

        float m = (datum.getLength() / 1024.0f) / 1024.0f;
        float g = m / 1024.0f;

        if (g > 1) {
            holder.mTvSize.setText((mDecimalFormat.format(g) + "G"));
        }else{
            holder.mTvSize.setText((mDecimalFormat.format(m) + "M"));
        }

        holder.mViewChecked.setOnClickListener(v -> {
            boolean contains = mCheckedData.contains(datum);
            if (mOnCheckedChangeListener == null || mOnCheckedChangeListener.onCheckedChangeBefore(v, position, !contains)) {
                if (mCheckedData.contains(datum)) {
                    mCheckedData.remove(datum);
                    notifyItemChanged(position);
                    for (int i = index; i < mCheckedData.size(); i++) {
                        notifyItemChanged(mData.indexOf(mCheckedData.get(i)));
                    }
                    mOnCheckedChangeListener.onCheckedChangeAfter(v, position, !contains);
                } else {
                    mCheckedData.add(datum);
                    notifyItemChanged(position);
                    mOnCheckedChangeListener.onCheckedChangeAfter(v, position, !contains);
                }
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v,position,datum);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<VideoData> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public int getCheckedSize(){
        return mCheckedData.size();
    }

    public VideoData getDatum(int position) {
        return mData.get(position);
    }

    public List<VideoData> getCheckedData(){
        return mCheckedData;
    }

    public boolean addCheckedData(VideoData datum) {
        if (!mCheckedData.contains(datum)) {
            int index = mData.indexOf(datum);
            if (index >= 0) {
                mCheckedData.add(datum);
                notifyItemChanged(index);
                return true;
            }
        }
        return false;
    }

    public VideoAdapter setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        mOnCheckedChangeListener = onCheckedChangeListener;
        return this;
    }

    public VideoAdapter setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        return this;
    }

    public interface OnCheckedChangeListener{
        /**
         * check改变监听(改变前的事件用于确定是否改变数据)
         *
         * @return true确定改变执行刷新, false不执行刷新
         */
        boolean onCheckedChangeBefore(View view, int position, boolean isChecked);

        /**
         * {@link #onCheckedChangeBefore(View, int, boolean)}返回true之后执行
         */
        void onCheckedChangeAfter(View view, int position, boolean isChecked);
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position, VideoData path);
    }

    final static class VideoHolder extends RecyclerView.ViewHolder{

        private final TextView mTvCheckedNum;
        private final TextView mTvSize;
        private final View mViewChecked;
        private final View mViewSelected;
        private final ImageView mImageView;
        private final Context mContext;

        public VideoHolder(@NonNull View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            mImageView = itemView.findViewById(R.id.gallery_image_view);
            mViewSelected = itemView.findViewById(R.id.gallery_view_selected);
            mViewChecked = itemView.findViewById(R.id.gallery_view);
            mTvCheckedNum = itemView.findViewById(R.id.gallery_check_num);
            mTvSize = itemView.findViewById(R.id.text);
        }
    }
}
