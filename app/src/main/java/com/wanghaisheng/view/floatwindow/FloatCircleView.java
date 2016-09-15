package com.wanghaisheng.view.floatwindow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.wanghaisheng.floatwindow.R;
import com.wanghaisheng.floatwindow.util.DesityUtils;

/**
 * Author: sheng on 2016/9/14 10:21
 * Email: 1392100700@qq.com
 * 浮窗类
 */
public class FloatCircleView extends View {

    //浮窗的宽和高
    private int mWidth = 60;
    private int mHeight = 60;

    //画圆的画笔
    private Paint mCirclePaint;
    private int mCirclePaintColor = 0xff00CC99;
    //画text的画笔
    private Paint mTextPaint;
    private int mTextPaintColor = Color.WHITE;
    private int mTextSize = 18;
    private String mText = "50%";

    //设置是否为拖拽状态
    private boolean mDrag;
    //拖拽状态下显示的bitmap
    private Bitmap mDraggingBitmap;

    public FloatCircleView(Context context) {
        this(context,null);
    }

    public FloatCircleView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FloatCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //将dp转为px或将sp转为px
        mWidth = DesityUtils.dp2px(context,mWidth);
        mHeight = DesityUtils.dp2px(context,mHeight);
        mTextSize = DesityUtils.sp2px(context,mTextSize);

        //初始化paint
        initPaint();
    }

    private void initPaint() {

        //设置画圆的paint的属性
        mCirclePaint = new Paint();
        mCirclePaint.setColor(mCirclePaintColor);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setDither(true);
        mCirclePaint.setStyle(Paint.Style.FILL);

        //设置画text的paint的属性
        mTextPaint = new Paint();
        mTextPaint.setColor(mTextPaintColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setTextSize(mTextSize);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dragging_icon);
        mDraggingBitmap = Bitmap.createScaledBitmap(bitmap,mWidth,mHeight,true);

    }


    /**
     * 设置宽和高为设定的固定值
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth,mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if(mDrag) {
            //如果在drag状态则draw draggingbitmap，否则draw circle和text
            canvas.drawBitmap(mDraggingBitmap,0,0,null);
        } else {
            //画圆
            canvas.drawCircle(mWidth/2,mWidth/2,mHeight/2,mCirclePaint);

            //画text
            float textWidth = mTextPaint.measureText(mText);
            //draw text起始点
            int textX = (int) (getWidth()/2 - textWidth/2);
            Paint.FontMetrics metrics = mTextPaint.getFontMetrics();
            //获取baseLine离descent的height
            float textBaseLineHeight = -(metrics.descent+metrics.ascent)/2;
            //因为drawText是从baseLine开始画的
            int textY = (int) (getHeight()/2 + textBaseLineHeight);
            canvas.drawText(mText,textX,textY,mTextPaint);
        }


    }

    /**
     * 设置当前控件是否为拖拽状态
     * @param drag
     */
    public void setDragState(boolean drag) {
        this.mDrag = drag;
        invalidate();
    }
}
