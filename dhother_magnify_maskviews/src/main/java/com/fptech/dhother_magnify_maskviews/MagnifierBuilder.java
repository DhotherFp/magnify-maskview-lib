package com.fptech.dhother_magnify_maskviews;

import android.content.Context;




public class MagnifierBuilder {
    private float mMagnifierRadius;
    private float mScaleRate;

    private float strokeWidth;
    private float leftSpace;
    private float topSpace;
    private boolean shouldAutoMoveMagnifier;


    public float getMagnifierRadius() {
        return mMagnifierRadius;
    }

    public float getScaleRate() {
        return mScaleRate;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public float getLeftSpace() {
        return leftSpace;
    }

    public float getTopSpace() {
        return topSpace;
    }

    public boolean getShouldAutoMoveMagnifier() {
        return shouldAutoMoveMagnifier;
    }

    public MagnifierBuilder(Context context) {
        widthMagnifierRadius(DisplayUtil.dip2px(context, 50));
        widthMagnifierScaleRate(1.3f);
        widthMagnifierStrokeWidth(DisplayUtil.dip2px(context, 5));
        widthMagnifierLeftSpace(DisplayUtil.dip2px(context, 10));
        widthMagnifierTopSpace(DisplayUtil.dip2px(context, 100));
        widthMagnifierShouldAutoMoveMagnifier(false);
    }


    public MagnifierBuilder widthMagnifierShouldAutoMoveMagnifier(boolean b) {
        this.shouldAutoMoveMagnifier = b;
        return this;
    }


    public MagnifierBuilder widthMagnifierTopSpace(int topSpace) {
        this.topSpace = topSpace;
        return this;
    }


    public MagnifierBuilder widthMagnifierLeftSpace(int leftSpace) {
        this.leftSpace = leftSpace;
        return this;
    }



    public MagnifierBuilder widthMagnifierStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        return this;
    }



    public MagnifierBuilder widthMagnifierScaleRate(float rate) {
        this.mScaleRate = rate;
        return this;
    }



    public MagnifierBuilder widthMagnifierRadius(int radius) {
        mMagnifierRadius = radius;
        return this;
    }


} 