package com.github.gallery;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import androidx.gridlayout.widget.GridLayout;

/**
 * Created by ZhongXiaolong on 2020/8/21 5:38 PM.
 * <p>
 * 选图和展示结果View(仿微信九宫格选图),简化Activity/Fragment代码量,在要获取图片的地方使用{@link #getPhotoUrls()}
 * 多图选择+展示结果View(九宫格展示)
 */
public class ChoiceGalleryView extends GridLayout {

    private final List<String> mPhotoUrls;

    private OnItemClickListener mOnItemClick;

    public ChoiceGalleryView(Context context) {
        this(context, null);
    }

    public ChoiceGalleryView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChoiceGalleryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ChoiceGalleryView);
        int columnCount = a.getInt(R.styleable.ChoiceGalleryView_choiceGalleryViewColumnCount, 3);
        int rowCount = a.getInt(R.styleable.ChoiceGalleryView_choiceGalleryViewRowCount, 3);
        int max = a.getInt(R.styleable.ChoiceGalleryView_choiceGalleryViewMax, 9);
        int dp3 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
        int childMargin = a.getDimensionPixelOffset(R.styleable.ChoiceGalleryView_choiceGalleryViewChildMargin, dp3);
        a.recycle();

        setColumnCount(columnCount);
        setRowCount(rowCount);
        setOrientation(GridLayout.HORIZONTAL);
        setAlignmentMode(ALIGN_MARGINS);

        for (int i = 0; i < columnCount; i++) {
            for (int j = 0; j < rowCount; j++) {
                View childView = LayoutInflater.from(getContext()).inflate(R.layout.layout_choice_multiple_image, this, false);
                LayoutParams layoutParams = new LayoutParams(GridLayout.spec(i,1.0f), GridLayout.spec(j,1.0f));
                layoutParams.height = 0;
                layoutParams.width = 0;
                layoutParams.leftMargin = (int) (childMargin * 0.5);
                layoutParams.rightMargin = (int) (childMargin * 0.5);
                layoutParams.topMargin = (int) (childMargin * 0.5);
                layoutParams.bottomMargin = (int) (childMargin * 0.5);
                childView.setLayoutParams(layoutParams);
                addView(childView);
            }
        }
        mPhotoUrls = new ArrayList<>();
        resetLayout();
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        final int measuredWidth = getMeasuredWidth();
        final int measuredHeight = getMeasuredHeight();
        if (measuredHeight != measuredWidth) {
            setMeasuredDimension(measuredWidth, measuredWidth);
        }
    }

    public void resetLayout() {
        for (int i = 0; i < Math.min(mPhotoUrls.size(), getChildCount()); i++) {
            ImageView imageView = getChildAt(i).findViewById(R.id.gallery_image_view);
            Glide.with(getContext()).load(mPhotoUrls.get(i)).into(imageView);
            imageView.setOnClickListener(this::onChildViewClick);
            View btnDel = getChildAt(i).findViewById(R.id.gallery_image_button);
            btnDel.setVisibility(VISIBLE);
            btnDel.setOnClickListener(view -> removePhotoUrl(indexOfChild(((ViewGroup) view.getParent()))));
        }

        for (int i = mPhotoUrls.size(); i < getChildCount(); i++) {
            ImageView imageView = getChildAt(i).findViewById(R.id.gallery_image_view);
            Glide.with(getContext()).load(new ColorDrawable()).into(imageView);
            View btnDel = getChildAt(i).findViewById(R.id.gallery_image_button);
            btnDel.setVisibility(GONE);
        }

        int addIconPosition = mPhotoUrls.size();
        if (addIconPosition < getChildCount()) {
            ImageView imageView = getChildAt(addIconPosition).findViewById(R.id.gallery_image_view);
            Glide.with(getContext()).load(R.mipmap.gallery_ic_add_to).into(imageView);
            imageView.setOnClickListener(this::onChildViewClick);
        }
    }

    private void onChildViewClick(View view) {
        int index = indexOfChild(((ViewGroup) view.getParent()));
        String url = mPhotoUrls.size() > index ? mPhotoUrls.get(index) : null;
        if (mOnItemClick != null) {
            mOnItemClick.onItemClick(view, index, url);
            return;
        }
        if (url == null) {
            new ChoiceGallery(getContext()).setChoiceList(mPhotoUrls).setMaxChoice(maxSize()).setCallback(this::newPhotoUrls).start();
        } else {
//            GalleryPreviewActivity.start();
//            PhotoPreviewActivity.start(getContext(), mPhotoUrls, index);
        }
    }

    public void setItemClickListener(OnItemClickListener l) {
        mOnItemClick = l;
    }

    public void clear() {
        mPhotoUrls.clear();
        resetLayout();
    }

    public void newPhotoUrls(List<String> photoUrls) {
        mPhotoUrls.clear();
        mPhotoUrls.addAll(photoUrls);
        resetLayout();
    }

    public void addPhotoUrls(List<String> photoUrls) {
        mPhotoUrls.addAll(photoUrls);
        resetLayout();
    }

    public void addPhotoUrl(String photoUrl) {
        mPhotoUrls.add(photoUrl);
        resetLayout();
    }

    public void removePhotoUrl(int position) {
        mPhotoUrls.remove(position);
        resetLayout();
    }

    public void removePhotoUrl(String photoUrl) {
        mPhotoUrls.remove(photoUrl);
        resetLayout();
    }

    public List<String> getPhotoUrls() {
        return mPhotoUrls;
    }

    public int getSize() {
        return mPhotoUrls.size();
    }

    public int maxSize() {
        return 9;
    }

    public interface OnItemClickListener {

        /**
         * 点击事件
         *
         * @param view     image
         * @param position 下标
         * @param url      图片地址,为null时没有选择图片,触发选择图片事件
         */
        void onItemClick(View view, int position, String url);
    }
}
