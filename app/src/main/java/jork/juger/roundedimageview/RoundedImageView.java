package jork.juger.roundedimageview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by JugerJork on 10.03.2018.
 * Rounded image view
 */

@SuppressLint("AppCompatCustomView")
public class RoundedImageView extends ImageView {
    private float mTopLeftCorner;
    private float mTopRightCorner;
    private float mBottomLeftCorner;
    private float mBottomRightCorner;
    private boolean mIsCircle = false;

    private float mTopScalePadding;
    private float mBottomScalePadding;
    private float mLeftScalePadding;
    private float mRightScalePadding;

    private Path mClipPath;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public RoundedImageView(Context context) {
        super(context);
        init();
    }

    public RoundedImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        extractAttributes(attrs);
    }

    public RoundedImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        extractAttributes(attrs);
    }

    private void extractAttributes(AttributeSet attrs) {
        if (attrs == null)
            return;
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.RoundedImageView, 0, 0);
        try {
            mIsCircle = ta.getBoolean(R.styleable.RoundedImageView_is_circle, false);
            float corners = ta.getDimension(R.styleable.RoundedImageView_corner_radius, 0.f);
            if (corners > 0.f) {
                mTopLeftCorner = mTopRightCorner = mBottomLeftCorner = mBottomRightCorner = corners;
            } else {
                mTopLeftCorner = ta.getDimension(R.styleable.RoundedImageView_top_left_corner, 0.f);
                mTopRightCorner = ta.getDimension(R.styleable.RoundedImageView_top_right_corner, 0.f);
                mBottomLeftCorner = ta.getDimension(R.styleable.RoundedImageView_bottom_left_corner, 0.f);
                mBottomRightCorner = ta.getDimension(R.styleable.RoundedImageView_bottom_right_corner, 0.f);
            }
        } finally {
            ta.recycle();
        }
    }

    public void setCornersRadius(float cornersRadius) {
        mTopLeftCorner = mTopRightCorner = mBottomLeftCorner = mBottomRightCorner = cornersRadius;
        initClipPath();
    }

    public void setTopLeftCorner(float topLeftCorner) {
        mTopLeftCorner = topLeftCorner;
        initClipPath();
    }

    public void setTopRightCorner(float topRightCorner) {
        mTopRightCorner = topRightCorner;
        initClipPath();
    }

    public void setBottomLeftCorner(float bottomLeftCorner) {
        mBottomLeftCorner = bottomLeftCorner;
        initClipPath();
    }

    public void setBottomRightCorner(float bottomRightCorner) {
        mBottomRightCorner = bottomRightCorner;
        initClipPath();
    }

    public void setIsCircle(boolean isCircle) {
        mIsCircle = isCircle;
        initClipPath();
    }

    public float getTopLeftCorner() {
        return mTopLeftCorner;
    }

    public float getTopRightCorner() {
        return mTopRightCorner;
    }

    public float getBottomLeftCorner() {
        return mBottomLeftCorner;
    }

    public float getBottomRightCorner() {
        return mBottomRightCorner;
    }

    public boolean isCircle() {
        return mIsCircle;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        super.setScaleType(scaleType);
        initClipPath();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh)
            initClipPath();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mIsCircle && ((MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED && MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED)
                || (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED && MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED))) {
            if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
                int measuredSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY);
                super.onMeasure(widthMeasureSpec, measuredSpec);
            } else if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED) {
                int measuredSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY);
                super.onMeasure(widthMeasureSpec, measuredSpec);
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.TRANSPARENT);
        mPaint.setXfermode(xfermode);
        initClipPath();
    }

    private void initPath() {
        if (mClipPath == null) {
            mClipPath = new Path();
            mClipPath.setFillType(Path.FillType.INVERSE_EVEN_ODD);
        } else mClipPath.reset();
    }

    private void initClipPath() {
        initPath();
        float w = getMeasuredWidth();
        float h = getMeasuredHeight();
        if (w == 0 || h == 0 || getDrawable() == null)
            return;

        calculateScalePaddings();
        float leftPadding = getPaddingLeft() + mLeftScalePadding;
        float rightPadding = getPaddingRight() + mRightScalePadding;
        float topPadding = getPaddingTop() + mTopScalePadding;
        float bottomPadding = getPaddingBottom() + mBottomScalePadding;

        if (mIsCircle) {
            float realCenterX = (w - (leftPadding + rightPadding)) / 2.f;
            float realCenterY = (h - (topPadding + bottomPadding)) / 2.f;
            float radius = Math.min(realCenterX, realCenterY);
            mClipPath.addCircle(leftPadding + realCenterX, topPadding + realCenterY, radius, Path.Direction.CCW);
        } else {
            RectF rectF = new RectF(leftPadding, topPadding, w - rightPadding, h - bottomPadding);
            mClipPath.addRoundRect(rectF, new float[]{mTopLeftCorner, mTopLeftCorner, mTopRightCorner, mTopRightCorner,
                    mBottomRightCorner, mBottomRightCorner, mBottomLeftCorner, mBottomLeftCorner}, Path.Direction.CCW);
        }
        postInvalidate();
    }

    /*private Bitmap getBitmap() {
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
        if (result != null) {
            Rect outRect = new Rect();
            getDrawingRect(outRect);
//            getImageMatrix();
//            result = Bitmap.createBitmap(result, outRect.left, outRect.top, outRect.width(), outRect.height());
            result = result.copy(result.getConfig(), true);

            Canvas canvas = new Canvas(result);
            canvas.drawPath(mClipPath, mPaint);
//            setImageDrawable(new BitmapDrawable(result));
//            setImageDrawable(new BitmapDrawable(getResources(), result));
//            setImageBitmap(result);
        }
        return result;
    }*/

    private void calculateScalePaddings() {
        mRightScalePadding = mLeftScalePadding = mTopScalePadding = mBottomScalePadding = 0.f;
        if (getDrawable() != null) {
            switch (getScaleType()) {
                case CENTER:
                    if (getDrawable() != null) {
                        if (getMeasuredWidth() > getDrawable().getIntrinsicWidth()) {
                            mRightScalePadding = getMeasuredWidth() - getDrawable().getIntrinsicWidth();
                            mLeftScalePadding = mRightScalePadding = mRightScalePadding / 2.f;
                        } else {
                            mLeftScalePadding = mRightScalePadding = 0.f;
                        }
                        if (getMeasuredHeight() > getDrawable().getIntrinsicHeight()) {
                            mBottomScalePadding = getMeasuredHeight() - getDrawable().getIntrinsicHeight();
                            mTopScalePadding = mBottomScalePadding = mBottomScalePadding / 2.f;
                        } else {
                            mTopScalePadding = mBottomScalePadding = 0.f;
                        }
                    }
                    break;
                case CENTER_INSIDE:
                    if (getDrawable() != null) {
                        if (getMeasuredWidth() > getDrawable().getIntrinsicWidth() && getMeasuredHeight() > getDrawable().getIntrinsicHeight()) {
                            mRightScalePadding = getMeasuredWidth() - getDrawable().getIntrinsicWidth();
                            mLeftScalePadding = mRightScalePadding = mRightScalePadding / 2.f;
                            mBottomScalePadding = getMeasuredHeight() - getDrawable().getIntrinsicHeight();
                            mTopScalePadding = mBottomScalePadding = mBottomScalePadding / 2.f;
                        } else {
                            calculateFitPadding();
                        }
                    }
                    break;
                case FIT_CENTER:
                    calculateFitPadding();
                    break;
                case FIT_END:
                    if (calculateFitPadding()) {
                        mLeftScalePadding += mRightScalePadding;
                        mRightScalePadding = 0;
                    } else {
                        mTopScalePadding += mBottomScalePadding;
                        mBottomScalePadding = 0;
                    }
                    break;
                case FIT_START:
                    if (calculateFitPadding()) {
                        mRightScalePadding += mLeftScalePadding;
                        mLeftScalePadding = 0;
                    } else {
                        mBottomScalePadding += mTopScalePadding;
                        mTopScalePadding = 0;
                    }
                    break;
                case MATRIX:
                    if (getDrawable() != null) {
                        mRightScalePadding = getMeasuredWidth() - getDrawable().getIntrinsicWidth();
                        mBottomScalePadding = getMeasuredHeight() - getDrawable().getIntrinsicHeight();
                    }
                    break;
            }
        }
    }

    private boolean calculateFitPadding() {
        boolean isWidth = false;
        float factorWidth = getMeasuredWidth() / (float) getDrawable().getIntrinsicWidth();
        float factorHeight = getMeasuredHeight() / (float) getDrawable().getIntrinsicHeight();
        if (getDrawable().getIntrinsicHeight() * factorWidth < getMeasuredHeight()) {
            mTopScalePadding = (getMeasuredHeight() - (getDrawable().getIntrinsicHeight() * factorWidth)) / 2.f;
            mBottomScalePadding = mTopScalePadding;
        } else if (getDrawable().getIntrinsicWidth() * factorHeight < getMeasuredWidth()) {
            mLeftScalePadding = (getMeasuredWidth() - (getDrawable().getIntrinsicWidth() * factorHeight)) / 2.f;
            mRightScalePadding = mLeftScalePadding;
            isWidth = true;
        }
        return isWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mClipPath, mPaint);
    }
}