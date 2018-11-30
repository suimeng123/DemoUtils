package com.lx.demoutils.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.lx.demoutils.R;

import java.sql.Ref;

/**
 * com.lx.demoutils
 * DemoUtils
 * Created by lixiao2
 * 2018/10/8.
 */

public class CropImageView extends View {
    private static final String TAG = "CropImageView";

    private int screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
    private int screenHeight = getContext().getResources().getDisplayMetrics().heightPixels;

    private Bitmap mBitmap, mBitmap2;


    // 图片定位在手机屏幕中心时的左上角位置left top
    private int left;
    private int top;

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

    public CropImageView(Context context) {
        this(context, null);
    }

    public CropImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CropImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CropImageView);
        Drawable drawable = array.getDrawable(R.styleable.CropImageView_src);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        mBitmap = bitmapDrawable.getBitmap();

        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();

//        if (width <= screenWidth) {
//            // 如果图片宽度比屏幕窄
//            left = (screenWidth - width) / 2;
//        } else {
//            left = -((width - screenWidth) / 2);
//        }
//
//        if (height <= screenHeight) {
//            // 如果图片高度比屏幕窄
//            top = (screenHeight - height) / 2;
//        } else {
//            top = -((height - screenHeight) / 2);
//        }

        float wPercent = screenWidth * 1.0f / width;
        float hPercent = screenHeight * 1.0f / height;
        Log.i(TAG, "wPercent: " + wPercent + "; hPercent: " + hPercent);
        Matrix matrix = new Matrix();
        if (wPercent > hPercent) {
            // 屏幕与图片宽的比例大时 高占满屏幕
            int wh = (int) (hPercent * width);
            int hh = screenHeight;
            top = 0;
            left = (screenHeight - wh) / 2;
            matrix.setScale(hPercent, hPercent);
            mBitmap2 = Bitmap.createBitmap(mBitmap, 0, 0, width, height, matrix, true);
        } else {
            // 屏幕与图片高的比例大时 宽占满屏幕
            int wh = screenWidth;
            int hh = (int) (height * wPercent);
            top = (screenHeight - hh) / 2;
            left = 0;
            matrix.setScale(wPercent, wPercent);
            mBitmap2 = Bitmap.createBitmap(mBitmap, 0, 0, width, height, matrix, true);
        }


        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setARGB(140, 0, 0, 0);

        cPaint = new Paint();
        cPaint.setStyle(Paint.Style.STROKE);
        cPaint.setAntiAlias(true);
        cPaint.setColor(Color.WHITE);
        cPaint.setStrokeWidth(1f);

        cLeft = screenWidth / 2 - clipWidth / 2;
        cRight = screenWidth / 2 + clipWidth / 2;
        cTop = screenHeight / 2 - clipHeight / 2;
        cBottom = screenHeight / 2 + clipHeight / 2;


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
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                isClipRectF = isClipRectF(downX, downY);
                break;
            case MotionEvent.ACTION_MOVE:
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
                break;
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
        Log.i(TAG, "isClipRectF: " + isClipRectF);
        switch (isClipRectF) {
            case 1:
                cLeft = (int) event.getX();
                cTop = (int) event.getY();
                if (event.getX() <= left) {
                    cLeft = left;
                }
                if (event.getX() >= (cRight - minClipWidth)) {
                    cLeft = cRight - minClipWidth;
                }
                if (event.getY() <= top) {
                    cTop = top;
                }
                if (event.getY() >= (cBottom - minClipHeight)) {
                    cTop = cBottom - minClipHeight;
                }
                clipWidth = cRight - cLeft;
                clipHeight = cBottom - cTop;
                break;
            case 2:
                cTop = (int) event.getY();
                if (event.getY() <= top) {
                    cTop = top;
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
                if (event.getX() >= (screenWidth - left)) {
                    cRight = screenWidth - left;
                }
                if (event.getY() <= top) {
                    cTop = top;
                }
                if (event.getY() >= (cBottom - minClipHeight)) {
                    cTop = cBottom - minClipHeight;
                }
                clipWidth = cRight - cLeft;
                clipHeight = cBottom - cTop;
                break;
            case 4:
                cLeft = (int) event.getX();
                if (event.getX() <= left) {
                    cLeft = left;
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
                if (cLeft <= left) {
                    cLeft = left;
                    cRight = cLeft + clipWidth;
                }
                if (cRight >= screenWidth - left) {
                    cRight = screenWidth - left;
                    cLeft = cRight - clipWidth;
                }
                if (cTop <= top) {
                    cTop = top;
                    cBottom = cTop + clipHeight;
                }
                if (cBottom >= screenHeight - top) {
                    cBottom = screenHeight - top;
                    cTop = cBottom - clipHeight;
                }
                break;
            case 6:
                cRight = (int) event.getX();
                if (event.getX() <= (cLeft + minClipWidth)) {
                    cRight = cLeft + minClipWidth;
                }
                if (event.getX() >= (screenWidth - left)) {
                    cRight = screenWidth - left;
                }
                clipWidth = cRight - cLeft;
                break;
            case 7:
                cLeft = (int) event.getX();
                cBottom = (int) event.getY();
                if (event.getX() <= left) {
                    cLeft = left;
                }
                if (event.getX() >= (cRight - minClipWidth)) {
                    cLeft = cRight - minClipWidth;
                }
                if (event.getY() <= (cTop + minClipHeight)) {
                    cBottom = cTop + minClipHeight;
                }
                if (event.getY() >= (screenHeight - top)) {
                    cBottom = screenHeight - top;
                }
                clipWidth = cRight - cLeft;
                clipHeight = cBottom - cTop;
                break;
            case 8:
                cBottom = (int) event.getY();
                if (event.getY() <= (cTop + minClipHeight)) {
                    cBottom = cTop + minClipHeight;
                }
                if (event.getY() >= (screenHeight - top)) {
                    cBottom = screenHeight - top;
                }
                clipHeight = cBottom - cTop;
                break;
            case 9:
                cBottom = (int) event.getY();
                cRight = (int) event.getX();
                if (event.getX() <= (cLeft + minClipWidth)) {
                    cRight = cLeft + minClipWidth;
                }
                if (event.getX() >= (screenWidth - left)) {
                    cRight = screenWidth - left;
                }
                if (event.getY() <= (cTop + minClipHeight)) {
                    cBottom = cTop + minClipHeight;
                }
                if (event.getY() >= (screenHeight - top)) {
                    cBottom = screenHeight - top;
                }
                clipWidth = cRight - cLeft;
                clipHeight = cBottom - cTop;
                break;
            default:
                break;
        }
    }

    // 计算点击时手指是否在剪裁框内
    private int isClipRectF(float x, float y) {
        if (cTop <= y && y <= cBottom && cLeft <= x && cRight >= x) {
            return 5;
        } else if (x <= cLeft && y <= cTop) {
            return 1;
        } else if ( x >= cLeft && x < cRight && y <= cTop) {
            return 2;
        } else if (x >= cRight && y <= cTop) {
            return 3;
        } else if (x <= cLeft && y >= cTop && y <= cBottom) {
            return 4;
        } else if (x >= cRight && y >= cTop && y <= cBottom) {
            return 6;
        } else if (x <= cLeft && y >= cBottom) {
            return 7;
        } else if (x >= cLeft && x <= cRight && y >= cBottom) {
            return 8;
        } else if (x >= cRight && y >= cBottom) {
            return 9;
        }
        return  0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap2, left, top, new Paint());
        RectF cRectF = new RectF(cLeft, cTop, cRight, cBottom);
        canvas.drawRect(cRectF, cPaint);

        drawLines(canvas);

        canvas.clipRect(cRectF, Region.Op.XOR);
        RectF rectF = new RectF(left, top, screenWidth - left, screenHeight - top);
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
        return Bitmap.createBitmap(mBitmap2, cLeft - left, cTop - top, cRight - cLeft, cBottom - cTop);
    }
}
