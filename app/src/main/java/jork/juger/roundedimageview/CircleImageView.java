package jork.juger.roundedimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by JugerJork on 29.03.2018.
 */

public class CircleImageView extends android.support.v7.widget.AppCompatImageView {
    private Paint mShaderPaint;
    private Matrix mShaderMatrix;
    /*private float mProcessedBitmapWidth;
    private float mProcessedBitmapHeight;*/
    private float mCenterX;
    private float mCenterY;
    private float mRadius;
    /*private float mRoundRadius = 25.f;
    private RectF mRoundedRect;*/

    public CircleImageView(Context context) {
        super(context);
        init();
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        init();
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        init();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        init();
    }

    @Override
    public void setImageURI(@Nullable Uri uri) {
        super.setImageURI(uri);
        init();
    }

    private void init() {
        mCenterX = getMeasuredWidth() / 2.f;
        mCenterY = getMeasuredHeight() / 2.f;
        mRadius = Math.min(mCenterX, mCenterY);
        Bitmap bmp = getBitmapFromDrawable();
        if (bmp != null) {
            BitmapShader shader = instanceShader(bmp);
            if (mShaderPaint == null) {
                mShaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                mShaderPaint.setDither(true);
            }
            if (shader != null)
                mShaderPaint.setShader(shader);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if ((MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED && MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.UNSPECIFIED)
                || (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED && MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED)) {
            int maxSize = Math.max(width, height);
            switch (MeasureSpec.getMode(widthMeasureSpec)) {
                case MeasureSpec.UNSPECIFIED:
                    width = maxSize;
                    break;
            }
            switch (MeasureSpec.getMode(heightMeasureSpec)) {
                case MeasureSpec.UNSPECIFIED:
                    height = maxSize;
                    break;
            }
        }
        setMeasuredDimension(width, height);
    }

    private Bitmap getBitmapFromDrawable() {
        Bitmap result = null;
        if (getDrawable() != null) {
            if (getDrawable() instanceof BitmapDrawable)
                result = ((BitmapDrawable) getDrawable()).getBitmap();
            else {
                Bitmap.Config bitmapConfig = Bitmap.Config.ARGB_8888;
                if (getDrawable() instanceof ColorDrawable) {
                    int sizes = 1;
                    result = Bitmap.createBitmap(sizes, sizes, bitmapConfig);
                } else {
                    result = Bitmap.createBitmap(getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight(), bitmapConfig);
                }
            }
        }
        return result;
    }

    private BitmapShader instanceShader(Bitmap bmp) {
        BitmapShader result = null;
        if (getMeasuredWidth() > 0 && getMeasuredHeight() > 0 && bmp.getWidth() > 0 && bmp.getHeight() > 0) {
            result = new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            calculateShaderMatrix(bmp);
            result.setLocalMatrix(mShaderMatrix);
        }
        return result;
    }

    private void calculateShaderMatrix(Bitmap bmp) {
        if (mShaderMatrix == null) {
            mShaderMatrix = new Matrix();
        }
        mShaderMatrix.reset();
        float diameter = mRadius * 2.f;
        float scale = 1.f;
        switch (getScaleType()) {
            case FIT_START:
                if (getMeasuredWidth() > getMeasuredHeight())
                    mCenterX = mRadius;
                else mCenterY = mRadius;
                float widthScale = diameter / (float) bmp.getWidth();
                float heightScale = diameter / (float) bmp.getHeight();
                scale = Math.min(widthScale, heightScale);
                mShaderMatrix.setScale(scale, scale);
                float left = (int) (mCenterX - (bmp.getWidth() * scale) / 2.f);
                float top = (int) (mCenterY - (bmp.getHeight() * scale) / 2.f);
                mShaderMatrix.postTranslate(left, top);
                break;
            case FIT_CENTER:
                widthScale = diameter / (float) bmp.getWidth();
                heightScale = diameter / (float) bmp.getHeight();
                scale = Math.min(widthScale, heightScale);
                mShaderMatrix.setScale(scale, scale);
                left = (int) (mCenterX - (bmp.getWidth() * scale) / 2.f);
                top = (int) (mCenterY - (bmp.getHeight() * scale) / 2.f);
                mShaderMatrix.postTranslate(left, top);
                break;
            case FIT_END:
                if (getMeasuredWidth() > getMeasuredHeight())
                    mCenterX = getMeasuredWidth() - mRadius;
                else mCenterY = getMeasuredHeight() - mRadius;
                widthScale = diameter / (float) bmp.getWidth();
                heightScale = diameter / (float) bmp.getHeight();
                scale = Math.min(widthScale, heightScale);
                mShaderMatrix.setScale(scale, scale);
                left = (int) (mCenterX - (bmp.getWidth() * scale) / 2.f);
                top = (int) (mCenterY - (bmp.getHeight() * scale) / 2.f);
                mShaderMatrix.postTranslate(left, top);
                break;
            case FIT_XY:
                widthScale = diameter / (float) bmp.getWidth();
                heightScale = diameter / (float) bmp.getHeight();
                mShaderMatrix.setScale(widthScale, heightScale);
                mShaderMatrix.postTranslate(mCenterX - mRadius, mCenterY - mRadius);
                break;
            case CENTER_INSIDE:
                widthScale = diameter / (float) bmp.getWidth();
                heightScale = diameter / (float) bmp.getHeight();
                scale = Math.min(widthScale, heightScale);
                if (scale < 1.f) {
                    mShaderMatrix.setScale(scale, scale);
                    left = (int) (mCenterX - (bmp.getWidth() * scale) / 2.f);
                    top = (int) (mCenterY / 2.f - (bmp.getHeight() * scale) / 2.f);
                    mShaderMatrix.postTranslate(left, top);
                    break;
                } else scale = 1.f;
            case CENTER:
                left = (int) (getMeasuredWidth() / 2.f - bmp.getWidth() / 2.f);
                top = (int) (getMeasuredHeight() / 2.f - bmp.getHeight() / 2.f);
                mShaderMatrix.postTranslate(left, top);
                break;
            case CENTER_CROP:
                widthScale = diameter / (float) bmp.getWidth();
                heightScale = diameter / (float) bmp.getHeight();
                scale = Math.max(widthScale, heightScale);
                mShaderMatrix.setScale(scale, scale);
                left = (int) (((bmp.getWidth() * scale) - getMeasuredWidth()) / 2.f);
                top = (int) (((bmp.getHeight() * scale) - getMeasuredHeight()) / 2.f);
                mShaderMatrix.postTranslate(-left, -top);
                break;
            case MATRIX:
                mShaderMatrix.postTranslate(mCenterX - mRadius, mCenterY - mRadius);
                break;
        }
        /*mProcessedBitmapWidth = bmp.getWidth() * scale; //todo сделать отдельный метод для roundedImageView
        if(mProcessedBitmapWidth > getMeasuredWidth())
            mProcessedBitmapWidth = getMeasuredWidth();
        mProcessedBitmapHeight = bmp.getWidth() * scale;
        if(mProcessedBitmapHeight > getMeasuredHeight())
            mProcessedBitmapHeight = getMeasuredHeight();
        float paddingWidth = (getMeasuredWidth() - mProcessedBitmapWidth) / 2.f;
        float paddingHeight = (getMeasuredHeight() - mProcessedBitmapHeight) / 2.f;
        mRoundedRect = new RectF(paddingWidth, paddingHeight, getMeasuredHeight() - paddingWidth, getMeasuredWidth() - paddingHeight);*/
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        if (mShaderPaint != null) {
            canvas.drawCircle(mCenterX, mCenterY, mRadius, mShaderPaint);
//            canvas.drawRoundRect(mRoundedRect, mRoundRadius, mRoundRadius, mShaderPaint);
        }
    }
}
