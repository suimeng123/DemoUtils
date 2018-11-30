package com.lx.demoutils.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.lx.demoutils.R;

/**
 * com.lx.demoutils
 * DemoUtils
 * Created by lixiao2
 * 2018/10/8.
 */

public class CropScaleImageView extends View {
    private static final String TAG = "CropScaleImageView";

    private int screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
    private int screenHeight = getContext().getResources().getDisplayMetrics().heightPixels;

    // 原始图片bitmap
    private Bitmap mBitmap;
    // 当前图片bitmap
    private Bitmap nowBitmap;


    // 当前图片宽度和高度
    private int mWidth, mHeight;

    // 遮罩层的画笔
    private Paint mPaint;

    // 裁剪层画笔
    private Paint cPaint;

    // 裁剪层rectf的上下左右坐标点
    private int cLeft, cTop, cRight, cBottom;


    // 默认截取框的宽高
    private int clipWidth = 500;
    private int clipHeight = 300;

    // 裁剪层栅格线条数
    private int clipLine = 3;

    // 顶点交叉线的长度
    private int dHeight = 80;

    // 顶点交叉线的宽度
    private int dWidth = 10;

    // 裁剪框最小宽度和高度
    private int minClipWidth = 200;
    private int minClipHeight = 200;

    public CropScaleImageView(Context context) {
        this(context, null);
    }

    public CropScaleImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropScaleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CropScaleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CropImageView);
        Drawable drawable = array.getDrawable(R.styleable.CropImageView_src);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        mBitmap = bitmapDrawable.getBitmap();

        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();

        float wPercent = screenWidth * 1.0f / width;
        mWidth = screenWidth;
        mHeight = (int) (wPercent * height);
        Matrix matrix = new Matrix();
        matrix.setScale(wPercent, wPercent);
        nowBitmap = Bitmap.createBitmap(mBitmap, 0, 0, width, height, matrix, true);


        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setARGB(140, 0, 0, 0);

        cPaint = new Paint();
        cPaint.setStyle(Paint.Style.STROKE);
        cPaint.setAntiAlias(true);
        cPaint.setColor(Color.WHITE);
        cPaint.setStrokeWidth(1f);

        cLeft = mWidth / 2 - clipWidth / 2;
        cRight = mWidth / 2 + clipWidth / 2;
        if (mHeight > screenHeight) {
            cTop = screenHeight / 2 - clipHeight / 2;
            cBottom = screenHeight / 2 + clipHeight / 2;
        } else {
            cTop = mHeight / 2 - clipHeight / 2;
            cBottom = mHeight / 2 + clipHeight / 2;
        }


        if (array != null) {
            array.recycle();
        }
    }


    private float downX;
    private float downY;

    private float lastMoveX;
    private float lastMoveY;

    /**
     * 通过裁剪框的四条线延长可以将屏幕分为9块，当down事件时去判断 down 时是在哪个区域
     * 1：左上区域 水平依次增加 1-9 块 5表示裁剪区域
     */
    private int isClipRectF = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_POINTER_2_DOWN:
            case MotionEvent.ACTION_DOWN:
//                Log.i(TAG, "ACTION_DOWN.getActionIndex(): " + event.getActionIndex());
                downX = event.getX();
                downY = event.getY();
                isClipRectF = isClipRectF(downX, downY);
                break;
            case MotionEvent.ACTION_MOVE:
                int index1 = event.findPointerIndex(0);
                int index2 = event.findPointerIndex(1);
                Log.i(TAG, "index1: " + index1 + "; index2: " + index2);
                if (index2 + index1 == -1) {
                    // 只有一个手指触摸时
                    if (lastMoveX == 0) {
                        lastMoveX = downX;
                    }
                    if (lastMoveY == 0) {
                        lastMoveY = downY;
                    }
                    changeClipRectF(event);
                    lastMoveX = event.getX();
                    lastMoveY = event.getY();
                    invalidate();
                } else {
                    // 有两个及以上手指 现只考虑两个手指
                    float x1 = event.getX(index1);
                    float y1 = event.getY(index1);
                    float x2 = event.getX(index2);
                    float y2 = event.getY(index2);
                }
                break;
            case MotionEvent.ACTION_POINTER_2_UP:
            case MotionEvent.ACTION_UP:
                lastMoveX = 0;
                lastMoveY = 0;
                downX = 0;
                downY = 0;
                break;
            default:
                break;
        }
        return true;
    }

    // 改变裁剪区域
    private void changeClipRectF(MotionEvent event) {
        switch (isClipRectF) {
            case 1:
                cLeft = (int) event.getX();
                cTop = (int) event.getY();
                if (event.getX() <= 0) {
                    cLeft = 0;
                }
                if (event.getX() >= (cRight - minClipWidth)) {
                    cLeft = cRight - minClipWidth;
                }
                if (event.getY() <= 0) {
                    cTop = 0;
                }
                if (event.getY() >= (cBottom - minClipHeight)) {
                    cTop = cBottom - minClipHeight;
                }
                clipWidth = cRight - cLeft;
                clipHeight = cBottom - cTop;
                break;
            case 2:
                cTop = (int) event.getY();
                if (event.getY() <= 0) {
                    cTop = 0;
                }
                if (event.getY() >= (cBottom - minClipHeight)) {
                    cTop = cBottom - minClipHeight;
                }
                clipHeight = cBottom - cTop;
                break;
            case 3:
                cTop = (int) event.getY();
                cRight = (int) event.getX();
                if (event.getX() <= (cLeft + minClipWidth)) {
                    cRight = cLeft + minClipWidth;
                }
                if (event.getX() >= mWidth) {
                    cRight = mWidth;
                }
                if (event.getY() <= 0) {
                    cTop = 0;
                }
                if (event.getY() >= (cBottom - minClipHeight)) {
                    cTop = cBottom - minClipHeight;
                }
                clipWidth = cRight - cLeft;
                clipHeight = cBottom - cTop;
                break;
            case 4:
                cLeft = (int) event.getX();
                if (event.getX() <= 0) {
                    cLeft = 0;
                }
                if (event.getX() >= (cRight - minClipWidth)) {
                    cLeft = cRight - minClipWidth;
                }
                clipWidth = cRight - cLeft;
                break;
            case 5:
                // 在裁剪框内
                float dx = event.getX() - lastMoveX;
                float dy = event.getY() - lastMoveY;
                cLeft = (int) (dx + cLeft);
                cRight = (int) (dx + cRight);
                cTop = (int) (dy + cTop);
                cBottom = (int) (dy + cBottom);
                if (cLeft <= 0) {
                    cLeft = 0;
                    cRight = cLeft + clipWidth;
                }
                if (cRight >= mWidth) {
                    cRight = mWidth;
                    cLeft = cRight - clipWidth;
                }
                if (cTop <= 0) {
                    cTop = 0;
                    cBottom = cTop + clipHeight;
                }
                if (cBottom >= mHeight) {
                    cBottom = mHeight;
                    cTop = cBottom - clipHeight;
                }
                break;
            case 6:
                cRight = (int) event.getX();
                if (event.getX() <= (cLeft + minClipWidth)) {
                    cRight = cLeft + minClipWidth;
                }
                if (event.getX() >= mWidth) {
                    cRight = mWidth;
                }
                clipWidth = cRight - cLeft;
                break;
            case 7:
                cLeft = (int) event.getX();
                cBottom = (int) event.getY();
                if (event.getX() <= 0) {
                    cLeft = 0;
                }
                if (event.getX() >= (cRight - minClipWidth)) {
                    cLeft = cRight - minClipWidth;
                }
                if (event.getY() <= (cTop + minClipHeight)) {
                    cBottom = cTop + minClipHeight;
                }
                if (event.getY() >= mHeight) {
                    cBottom = mHeight;
                }
                clipWidth = cRight - cLeft;
                clipHeight = cBottom - cTop;
                break;
            case 8:
                cBottom = (int) event.getY();
                if (event.getY() <= (cTop + minClipHeight)) {
                    cBottom = cTop + minClipHeight;
                }
                if (event.getY() >= mHeight) {
                    cBottom = mHeight;
                }
                clipHeight = cBottom - cTop;
                break;
            case 9:
                cBottom = (int) event.getY();
                cRight = (int) event.getX();
                if (event.getX() <= (cLeft + minClipWidth)) {
                    cRight = cLeft + minClipWidth;
                }
                if (event.getX() >= mWidth) {
                    cRight = mWidth;
                }
                if (event.getY() <= (cTop + minClipHeight)) {
                    cBottom = cTop + minClipHeight;
                }
                if (event.getY() >= mHeight) {
                    cBottom = mHeight;
                }
                clipWidth = cRight - cLeft;
                clipHeight = cBottom - cTop;
                break;
            default:
                break;
        }
    }

    // 手指触摸触发裁剪框缩放的距离
    private int pointClipMove = 40;

    // 计算点击时手指是否在剪裁框内
    private int isClipRectF(float x, float y) {
        if (cTop <= y && y <= cBottom && cLeft <= x && cRight >= x) {
            return 5;
        } else if (x <= cLeft && x >= (cLeft - pointClipMove) && y <= cTop && y >= (cTop - pointClipMove)) {
            return 1;
        } else if (x >= cLeft && x < cRight && y <= cTop && y >= (cTop - pointClipMove)) {
            return 2;
        } else if (x >= cRight && x <= (cRight + pointClipMove) && y <= cTop && y >= (cTop - pointClipMove)) {
            return 3;
        } else if (x <= cLeft && x >= (cLeft - pointClipMove) && y >= cTop && y <= cBottom) {
            return 4;
        } else if (x >= cRight && x <= (cRight + pointClipMove) && y >= cTop && y <= cBottom) {
            return 6;
        } else if (x <= cLeft && x >= (cLeft - pointClipMove) && y >= cBottom && y <= (cBottom + pointClipMove)) {
            return 7;
        } else if (x >= cLeft && x <= cRight && y >= cBottom && y <= (cBottom + pointClipMove)) {
            return 8;
        } else if (x >= cRight && x <= (cRight + pointClipMove) && y >= cBottom && y <= (cBottom + pointClipMove)) {
            return 9;
        }
        return 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(nowBitmap, 0, 0, new Paint());
        RectF cRectF = new RectF(cLeft, cTop, cRight, cBottom);
        canvas.drawRect(cRectF, cPaint);

        drawLines(canvas);

        canvas.clipRect(cRectF, Region.Op.XOR);
        RectF rectF = new RectF(0, 0, mWidth, mHeight);
        canvas.drawRect(rectF, mPaint);
    }


    private void drawLines(Canvas canvas) {
        cPaint.setStrokeWidth(1f);
        // 画栅格线
        for (int i = 1; i < clipLine; i++) {
            canvas.drawLine(cLeft + (clipWidth / clipLine) * i, cTop, cLeft + (clipWidth / clipLine) * i, cBottom, cPaint);
            canvas.drawLine(cLeft, cTop + (clipHeight / clipLine) * i, cRight, cTop + (clipHeight / clipLine) * i, cPaint);
        }

        cPaint.setStrokeWidth(dWidth);
        // 画顶点的交叉线
        // 左上
        canvas.drawLine(cLeft + dWidth / 2, cTop, cLeft + dWidth / 2, cTop + dHeight, cPaint);
        canvas.drawLine(cLeft, cTop + dWidth / 2, cLeft + dHeight, cTop + dWidth / 2, cPaint);

        // 右上
        canvas.drawLine(cRight - dWidth / 2, cTop, cRight - dWidth / 2, cTop + dHeight, cPaint);
        canvas.drawLine(cRight, cTop + dWidth / 2, cRight - dHeight, cTop + dWidth / 2, cPaint);

        // 左下
        canvas.drawLine(cLeft + dWidth / 2, cBottom, cLeft + dWidth / 2, cBottom - dHeight, cPaint);
        canvas.drawLine(cLeft, cBottom - dWidth / 2, cLeft + dHeight, cBottom - dWidth / 2, cPaint);

        // 右下
        canvas.drawLine(cRight - dWidth / 2, cBottom, cRight - dWidth / 2, cBottom - dHeight, cPaint);
        canvas.drawLine(cRight, cBottom - dWidth / 2, cRight - dHeight, cBottom - dWidth / 2, cPaint);

        cPaint.setStrokeWidth(1f);
    }

    // 剪裁图片
    public Bitmap getClipRectfBitmap() {
        return Bitmap.createBitmap(nowBitmap, cLeft, cTop, cRight - cLeft, cBottom - cTop);
    }
}
