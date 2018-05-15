package jork.juger.roundedimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by Admin on 29.03.2018.
 */

public class Temp extends android.support.v7.widget.AppCompatImageView {
    private Paint mPaint;

    public Temp(Context context) {
        super(context);
        init();
    }

    public Temp(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Temp(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        refreshBitmap();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        refreshBitmap();
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        refreshBitmap();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        refreshBitmap();
    }

    private void refreshBitmap() {
        if (getMeasuredWidth() > 0 && getMeasuredHeight() > 0) {
            Bitmap bmp = getBitmap();
            if (bmp != null) {
                BitmapShader shader = instanceShader(bmp);
                if (mPaint == null)
                    mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                if (shader != null)
                    mPaint.setShader(shader);
                mPaint.setDither(true);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        refreshBitmap();
    }

    private BitmapShader instanceShader(Bitmap bmp) {
        if (bmp == null)
            return null;
        int left;
        int right;
        int top;
        int bottom;
        float scaleWidth = getMeasuredWidth() / (float)bmp.getWidth();
        float scaleHeight = getMeasuredHeight() / (float)bmp.getHeight();
        float maxScale = Math.max(scaleWidth, scaleHeight);

        float width = getMeasuredWidth() / maxScale;
        float height = getMeasuredHeight() / maxScale;

        left = (int)(bmp.getWidth() / 2 - width / 2.f);
        right = bmp.getWidth() - left;
        top = (int)(bmp.getHeight() / 2 - height / 2.f);
        bottom = bmp.getHeight() - top;

//        float width = bmp.getWidth() * maxScale; //1
//        float height = bmp.getHeight() * maxScale;
//        float centerX = width / 2.f;
//        float centerY = height / 2.f;
//        left = (int) (centerX - getMeasuredWidth() / 2.f);
//        top = (int) (centerY - getMeasuredHeight() / 2.f);
//        right = (int) (centerX + getMeasuredWidth() / 2.f);
//        bottom = (int) (centerY + getMeasuredHeight() / 2.f);

//        left = (int) (bmp.getWidth() / 2.f - width / 2.f); //2
//        top = (int) (bmp.getHeight() / 2.f - height / 2.f);
//        right = (int) (bmp.getWidth() / 2.f + width / 2.f);
//        bottom = (int) (bmp.getHeight() / 2.f + height / 2.f);


//        left = (int) ((bmp.getWidth() / 2.f - getMeasuredWidth() / 2.f) / maxScale); //3
//        top = (int) ((bmp.getHeight() / 2.f - getMeasuredHeight() / 2.f) / maxScale);
////        left = (int) ((getMeasuredWidth() / 2.f) - (bmp.getWidth() * maxScale));
////        top = (int) ((getMeasuredHeight() / 2.f) - (bmp.getHeight() * maxScale));
//        right = getMeasuredWidth() - left;
//        bottom = getMeasuredHeight() - top;
        Rect rect = new Rect(left, top, right, bottom);
        if (rect.width() > 0 && rect.height() > 0) {
            bmp = Bitmap.createBitmap(bmp, rect.left, rect.top, rect.width(), rect.height());
            return new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        }
        return null;
    }

    private Bitmap getBitmap() {
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
        if (result != null)
            result = result.copy(result.getConfig(), true);
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.FILL);
        canvas.drawCircle(getMeasuredWidth() / 2.f, getMeasuredHeight() / 2.f, getMeasuredHeight() / 2.f + 15.f, p);
        canvas.drawCircle(getMeasuredWidth() / 2.f, getMeasuredHeight() / 2.f, getMeasuredHeight() / 2.f, mPaint);
    }
}
