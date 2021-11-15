package com.github.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

/**
 * Created by ZhongXiaolong on 2021/10/18 3:05 下午.
 */
public class SuperImageView extends AppCompatImageView {

    private boolean mCircleCrop;
    private int mCircleStrokeColor;
    private int mCircleStrokeSize;

    public SuperImageView(Context context) {
        super(context);
    }

    public SuperImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuperImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SuperImageView, defStyle, 0);

        mCircleStrokeSize = a.getDimensionPixelSize(R.styleable.SuperImageView_circle_stroke_size, 0);
        mCircleStrokeColor = a.getColor(R.styleable.SuperImageView_circle_stroke_color, Color.TRANSPARENT);
        mCircleCrop = a.getBoolean(R.styleable.SuperImageView_circle_crop, false);

        a.recycle();

        if (mCircleCrop && getDrawable() != null) {
            setImageDrawable(getDrawable());
        }
    }

    @Override
    public ScaleType getScaleType() {
        if (mCircleCrop) {
            return ScaleType.CENTER_CROP;
        } else {
            return super.getScaleType();
        }
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (!mCircleCrop) {
            super.setScaleType(scaleType);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mCircleCrop) {
            int size;
            if (getLayoutParams().width != ViewGroup.LayoutParams.WRAP_CONTENT && getLayoutParams().width != ViewGroup.LayoutParams.MATCH_PARENT) {
                size = MeasureSpec.getSize(widthMeasureSpec);
            } else if (getLayoutParams().height != ViewGroup.LayoutParams.WRAP_CONTENT && getLayoutParams().height != ViewGroup.LayoutParams.MATCH_PARENT) {
                size = MeasureSpec.getSize(heightMeasureSpec);
            } else {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                return;
            }
            widthMeasureSpec = heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        if (drawable == null) {
            super.setImageDrawable(new ColorDrawable());
        } else if (mCircleCrop) {
            super.setImageDrawable(new CircleDrawable(drawable, mCircleStrokeSize, mCircleStrokeColor));
        } else {
            super.setImageDrawable(drawable);
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        if (bm == null) {
            super.setImageDrawable(new ColorDrawable());
        } else if (mCircleCrop) {
            super.setImageDrawable(new CircleDrawable(bm, mCircleStrokeSize, mCircleStrokeColor));
        } else {
            super.setImageBitmap(bm);
        }
    }

    /**
     * 设置边框的宽度和颜色
     *
     * @param size  宽度
     * @param color 颜色
     */
    public void setCircleStroke(int size, @ColorInt int color) {
        mCircleStrokeSize = size;
        mCircleStrokeColor = color;
        Drawable drawable = getDrawable();
        if (drawable != null) {
            setImageDrawable(new CircleDrawable(drawable, size, color));
        }
    }

    /**
     * 设置边框的颜色
     *
     * @param color 颜色
     */
    public void setCircleStrokeColor(@ColorInt int color) {
        mCircleStrokeColor = color;
        Drawable drawable = getDrawable();
        if (drawable != null) {
            setImageDrawable(new CircleDrawable(drawable, mCircleStrokeSize, color));
        }
    }

    /**
     * 设置边框的宽度
     *
     * @param size  宽度
     */
    public void setCircleStrokeSize(int size) {
        mCircleStrokeSize = size;
        Drawable drawable = getDrawable();
        if (drawable != null) {
            setImageDrawable(new CircleDrawable(drawable, size, mCircleStrokeColor));
        }
    }
}
