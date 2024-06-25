package com.fptech.dhother_magnify_maskviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;




public class MagnifierAutoLayout extends RelativeLayout {
    private Bitmap mBitmap;
    private Paint mPaintShadow;
    private long mShowTime = 0;
    private boolean mIsShowMagnifier = false;
    private Path mPath;
    private float mShowMagnifierX = 0;
    private float mShowMagnifierY = 0;

    private float mX = 0;
    private float mY = 0;

    private Paint paint;
    private Bitmap scaledBitmap;
    private Bitmap cutBitmap;
    private Bitmap roundBitmap;
    private MagnifierBuilder builder;

    public MagnifierAutoLayout(@NonNull Context context) {
        this(context, null);
    }

    public MagnifierAutoLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MagnifierAutoLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        init();
    }


    public void setMagnifierBuilder(MagnifierBuilder magnifierBuilder) {
        this.builder = magnifierBuilder;
        init();
    }


    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(builder.getStrokeWidth());

        mPaintShadow = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintShadow.setShadowLayer(20, 6, 6, Color.BLACK);
        mPath = new Path();

    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mIsShowMagnifier && builder != null) {


            float magnifierWidthAndHeight = builder.getMagnifierRadius() * 2 / builder.getScaleRate();
            int cutX = Math.max((int) (mShowMagnifierX - builder.getMagnifierRadius() - magnifierWidthAndHeight / 2), 0);
            int cutY = Math.min(Math.max((int) (mShowMagnifierY + builder.getMagnifierRadius() - magnifierWidthAndHeight / 2), 0), mBitmap.getHeight());

            int cutWidth = magnifierWidthAndHeight + cutX > mBitmap.getWidth() ? mBitmap.getWidth() - cutX : (int) magnifierWidthAndHeight;
            int cutHeight = magnifierWidthAndHeight + cutY > mBitmap.getHeight() ? mBitmap.getHeight() - cutY : (int) magnifierWidthAndHeight;


            if (cutWidth <= 0 || cutHeight == 0) {
                return;
            }
            cutBitmap = Bitmap.createBitmap(mBitmap, cutX, cutY, cutWidth, cutHeight);

            scaledBitmap = Bitmap.createScaledBitmap(cutBitmap, (int) (cutBitmap.getWidth() * builder.getScaleRate()), (int) (cutBitmap.getHeight() * builder.getScaleRate()), true);

            roundBitmap = BitmapHelper.GetRoundedCornerBitmap(scaledBitmap);


            RectF rectf;
            RectF rectF = new RectF(builder.getLeftSpace(), builder.getTopSpace(), roundBitmap.getWidth() + builder.getLeftSpace(), roundBitmap.getHeight() + builder.getTopSpace());

            float x = mX;
            float y = mY;
            float max = 0;
            if (builder.getShouldAutoMoveMagnifier()) {
                if (x >= rectF.left && x <= rectF.right && y >= rectF.top && y <= rectF.bottom) {

                    max = (getWidth() - roundBitmap.getWidth() - builder.getLeftSpace() * 2);
                    rectf = new RectF(builder.getLeftSpace() + (getWidth() - roundBitmap.getWidth() - builder.getLeftSpace() * 2), builder.getTopSpace(), roundBitmap.getWidth() + builder.getLeftSpace() + max, roundBitmap.getHeight() + builder.getTopSpace());
                } else {
                    rectf = new RectF(builder.getLeftSpace(), builder.getTopSpace(), roundBitmap.getWidth() + builder.getLeftSpace(), roundBitmap.getHeight() + builder.getTopSpace());

                }
            } else {
                rectf = new RectF(builder.getLeftSpace(), builder.getTopSpace(), roundBitmap.getWidth() + builder.getLeftSpace(), roundBitmap.getHeight() + builder.getTopSpace());

            }


            canvas.drawRoundRect(rectf, 15, 15, paint);
            canvas.save();
            canvas.drawBitmap(roundBitmap, builder.getLeftSpace() + max, builder.getTopSpace(), null);
            canvas.restore();

        } else {

            super.dispatchDraw(canvas);
        }
    }


    public void setTouch(MotionEvent event, ViewGroup viewGroup) {
        release();
        BitmapHelper.recycler(mBitmap);


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mShowTime = System.currentTimeMillis();
                mShowMagnifierX = event.getX() + builder.getMagnifierRadius();
                mShowMagnifierY = event.getY() - builder.getMagnifierRadius();
                mX = event.getX();
                mY = event.getY();

                break;
            case MotionEvent.ACTION_MOVE:
                mIsShowMagnifier = true;
                mShowMagnifierX = event.getX() + builder.getMagnifierRadius();
                mShowMagnifierY = event.getY() - builder.getMagnifierRadius();

                mX = event.getX();
                mY = event.getY();
//                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                mIsShowMagnifier = false;

                break;
        }

        if (!BitmapHelper.isNotEmpty(mBitmap)) {
            mBitmap = Bitmap.createBitmap(viewGroup.getWidth(), (int) (viewGroup.getHeight()), Bitmap.Config.ARGB_4444);
            Canvas canvas = new Canvas(mBitmap);

            canvas.clipRect(0, mShowMagnifierY, viewGroup.getWidth(), mShowMagnifierY + builder.getTopSpace());
            viewGroup.draw(canvas);
        }
        postInvalidate();

    }

    public void release() {
        BitmapHelper.recycler(cutBitmap, scaledBitmap, roundBitmap, mBitmap);
    }

}
