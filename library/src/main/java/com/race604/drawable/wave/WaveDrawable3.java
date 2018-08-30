package com.race604.drawable.wave;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Choreographer;
import android.view.animation.DecelerateInterpolator;

import static android.content.ContentValues.TAG;

/**
 * Created by jing on 16-12-6.
 */

public class WaveDrawable3 extends Drawable implements Animatable, ValueAnimator.AnimatorUpdateListener {

    private static final float WAVE_SPEED_FACTOR = 0.02f;
    private static final int UNDEFINED_VALUE = Integer.MIN_VALUE;

    private int mWidth, mHeight;
    private int mWaveHeight = UNDEFINED_VALUE;
    private int mWaveLength = UNDEFINED_VALUE;
    private int mWaveStep = UNDEFINED_VALUE;
    private int mWaveOffset = 0;
    private int mWaveLevel = 0;
    private float mProgress = 0.3f;
    private Paint mPaint;
    private Bitmap mMask;
    private Matrix mMatrix = new Matrix();
    private boolean mRunning = false;
    private boolean mIndeterminate = false;
    private int mColor;

    private Choreographer.FrameCallback mFrameCallback = new Choreographer.FrameCallback() {
        @Override
        public void doFrame(long l) {
            invalidateSelf();
            if (mRunning) {
                Choreographer.getInstance().postFrameCallback(this);
            }
        }
    };

    public WaveDrawable3(int color) {
        init();
        mColor = color;
    }


    private void init() {
        mMatrix.reset();
        mPaint = new Paint();
        mPaint.setFilterBitmap(false);
    }

    /**
     * Set wave move distance (in pixels) in very animation frame
     * @param step distance in pixels
     */
    public void setWaveSpeed(int step) {
        mWaveStep = Math.min(step, mWidth / 2);
    }

    /**
     * Set wave length (in pixels)
     * @param length
     */
    public void setWaveLength(int length) {
        length = Math.max(8, Math.min(mWidth * 2, length));
        if (length != mWaveLength) {
            mWaveLength = length;
            updateMask(mWidth, mWaveLength, mWaveHeight);
            invalidateSelf();
        }
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        updateBounds(bounds);
    }

    private void updateBounds(Rect bounds) {
        if (bounds.width() <= 0 || bounds.height() <= 0) {
            return;
        }

        if (mWidth <= 0 || mHeight <= 0) {
            mWidth = bounds.width();
            mHeight = bounds.height();
            if (mWaveHeight == UNDEFINED_VALUE) {
                mWaveHeight = mHeight;
            }

            if (mWaveLength == UNDEFINED_VALUE) {
                mWaveLength = mWidth;
            }

            if (mWaveStep == UNDEFINED_VALUE) {
                mWaveStep = -Math.max(1, (int) (mWidth * WAVE_SPEED_FACTOR));
            }

            updateMask(mWidth, mWaveLength, mWaveHeight);

            start();
            setProgress(0.5f);
        }
    }

    @Override
    public int getIntrinsicHeight() {
        return mHeight;
    }

    @Override
    public int getIntrinsicWidth() {
        return mWidth;
    }

    @Override
    public void draw(Canvas canvas) {
        if (mProgress <= 0.001f) {
            return;
        }

        mWaveOffset += mWaveStep;
        if (mWaveOffset > mWaveLength) {
            mWaveOffset -= mWaveLength;
        }
        if (mWaveOffset <= 0) {
            mWaveOffset += mWaveLength;
        }

        if (mMask != null) {
            mMatrix.setTranslate(-mWaveOffset, mWaveLevel);
            canvas.drawBitmap(mMask, mMatrix, mPaint);
        }
    }

    @Override
    public void setAlpha(int i) {
        mPaint.setAlpha(i);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void start() {
        mRunning = true;
        Choreographer.getInstance().postFrameCallback(mFrameCallback);
    }

    @Override
    public void stop() {
        mRunning = false;
        Choreographer.getInstance().removeFrameCallback(mFrameCallback);
    }

    @Override
    public boolean isRunning() {
        return mRunning;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        if (mIndeterminate) {
            setProgress(animation.getAnimatedFraction());
            if (!mRunning) {
                invalidateSelf();
            }
        }
    }

    public boolean isIndeterminate() {
        return mIndeterminate;
    }

    private ValueAnimator getDefaultAnimator() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(5000);
        return animator;
    }

    public void setProgress(float progress) {
        mProgress = progress;
        if (mHeight > 0 && mWaveHeight > 0) {
            mWaveLevel = mHeight - (int)((mHeight + mWaveHeight) * mProgress);
            invalidateSelf();
        }
    }

    private void updateMask(int width, int length, int height) {
        if (width <= 0 || length <= 0 || height <= 0) {
            Log.w(TAG, "updateMask: size must > 0");
            mMask = null;
            return;
        }


        final int count = (int) Math.ceil((width + length) / (float)length);

        Bitmap bm = Bitmap.createBitmap(length * count, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        float amplitude = height / 2f;
        Path path = new Path();
        path.moveTo(0, amplitude);

        final float stepX = length / 4f;
        float x = 0;
        float y = -amplitude;
        for (int i = 0; i < count * 2; i++) {
            x += stepX;
            path.quadTo(x, y, x+stepX, amplitude);
            x += stepX;
            y = height - y;
        }
        path.lineTo(bm.getWidth(), height);
        path.lineTo(0, height);
        path.close();

        p.setColor(mColor);
        c.drawPath(path, p);

        mMask = bm;
    }
}
