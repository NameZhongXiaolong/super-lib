package com.github.widget;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by ZhongXiaolong on 2021/10/19 2:23 下午.
 * <p>
 * 圆形Drawable
 */
class CircleDrawable extends Drawable {

    private final Paint mBitmapPaint;
    private final int mSize; //宽/高，直径
    private final Paint mStrokePaint;

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public CircleDrawable(Drawable drawable,int circleStrokeSize, @ColorInt int circleStrokeColor) {
        this(drawableToBitmap(drawable), circleStrokeSize, circleStrokeColor);
    }

    public CircleDrawable(Bitmap bitmap, int circleStrokeSize, @ColorInt int circleStrokeColor) {
        //着色器 水平和竖直都需要填充满
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(bitmapShader);
        //宽高的最小值作为直径
        mSize = Math.min(bitmap.getWidth(), bitmap.getHeight());

        mStrokePaint = new Paint();
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setColor(circleStrokeColor);
        mStrokePaint.setStrokeWidth(circleStrokeSize);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        //绘制圆形Bitmap
        canvas.drawCircle(mSize * 0.5f, mSize * 0.5f, mSize * 0.49f, mBitmapPaint);

        //描边
        canvas.drawCircle(mSize * 0.5f, mSize * 0.5f, mSize * 0.49f, mStrokePaint);

    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int i) {
        mBitmapPaint.setAlpha(i);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mBitmapPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        //设置系统默认，让drawable支持和窗口一样的透明度
        return PixelFormat.TRANSLUCENT;
    }

    //还需要从重写以下2个方法，返回drawable实际宽高
    @Override
    public int getIntrinsicWidth() {
        return mSize;
    }

    @Override
    public int getIntrinsicHeight() {
        return mSize;
    }

}
