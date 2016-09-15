package com.wanghaisheng.view.floatwindow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

import com.wanghaisheng.floatwindow.util.DesityUtils;
import com.wanghaisheng.floatwindow.util.ToastUtil;

/**
 * Author: sheng on 2016/9/14 22:17
 * Email: 1392100700@qq.com
 */
public class FloatProgressView extends ProgressBar {

    private Context mContext;

    //宽和高
    private int mWidth = 160;
    private int mHeight = 160;

    private Canvas mPaintCanvas;
    private Bitmap mBitmap;

    //画圆的画笔
    private Paint mCirclePaint;
    //画圆的画笔的颜色
    private int mCirclePaintColor = 0xff00CCCC;

    //画进度的画笔
    private Paint mProgressPaint;
    //画进度的画笔的颜色
    private int mProgressPaintColor = 0xff00CC66;
    //画进度的path
    private Path mProgressPath;
    private int mShapeTop = 10;

    //进度文字的画笔
    private Paint mTextPaint;
    //进度文字的颜色
    private int mTextPaintColor = 0xffffffff;
    private int mTextSize = 18;
    //当前进度
    private volatile int mCurrentProgress = 0;
    //最大进度
    private int mMaxProgress = 100;
    //目标，也就是双击时处理任务的进度，会影响曲线的振幅
    private int mTargetProgress = 50;

    private GestureDetector mGestureDetector;
    //正在进行单击动画的标志
    private boolean isSingleTapAnimation;
    //单击动画进行的次数，默认为50
    private int mSingleTapAnimationCount = 50;

    public FloatProgressView(Context context) {
        this(context,null);
    }

    public FloatProgressView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FloatProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        mWidth = DesityUtils.dp2px(context,mWidth);
        mHeight = DesityUtils.dp2px(context,mHeight);
        mTextSize = DesityUtils.sp2px(context,mTextSize);
        mShapeTop = DesityUtils.dp2px(context,mShapeTop);

        //初始化画笔的相关属性
        initPaint();
        mProgressPath = new Path();
        mBitmap = Bitmap.createBitmap(mWidth,mHeight, Bitmap.Config.ARGB_8888);
        mPaintCanvas = new Canvas(mBitmap);

        setProgress(mCurrentProgress);
        setMax(mMaxProgress);

        mGestureDetector = new GestureDetector(context,new MyGestureDetector());
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //将触摸事件交由mGestureDetector处理
                mGestureDetector.onTouchEvent(motionEvent);

                return true;
            }
        });

    }

    /**
     * 监听单击双击事件
     */
    private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
//            Toast.makeText(getContext(),"双击了",Toast.LENGTH_SHORT).show();
            ToastUtil.showCenterToast(mContext,"双击");
            //双击动画
            startDoubleTapAnimation();
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
//            Toast.makeText(getContext(),"单击了",Toast.LENGTH_SHORT).show();
            ToastUtil.showShortCenterToast(mContext,"单击");
            //单击动画
            startSingleTapAnimation();
            return true;
        }
    }


    private void startSingleTapAnimation() {
        isSingleTapAnimation = true;
        singleTapHandler.postDelayed(singleTapRunnable,200);
    }

    private Handler singleTapHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    //单击处理线程，隔200ms发送一次数据
    private Runnable singleTapRunnable = new Runnable() {
        @Override
        public void run() {
            if(mSingleTapAnimationCount > 0) {
                invalidate();
                mSingleTapAnimationCount--;
                singleTapHandler.postDelayed(singleTapRunnable,200);
            } else {
                singleTapHandler.removeCallbacks(singleTapRunnable);
                isSingleTapAnimation = false;
                mSingleTapAnimationCount = 50;
            }
        }
    };


    private Handler doubleTapHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    /**
     * 实现双击动画
     */
    private void startDoubleTapAnimation() {
        setProgress(0);
        doubleTapHandler.postDelayed(doubleTapRunnable,60);
    }

    //双击处理线程，隔60ms发送一次数据
    private Runnable doubleTapRunnable = new Runnable() {
        @Override
        public void run() {
            if(getProgress() < mTargetProgress) {
                invalidate();
                setProgress(getProgress()+1);
                doubleTapHandler.postDelayed(doubleTapRunnable,60);
            } else {
                doubleTapHandler.removeCallbacks(doubleTapRunnable);
            }
        }
    };

    /**
     * 初始化画笔的相关属性
     */
    private void initPaint() {
        mCirclePaint = new Paint();
        mCirclePaint.setColor(mCirclePaintColor);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setDither(true);

        mProgressPaint = new Paint();
        mProgressPaint.setColor(mProgressPaintColor);
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setDither(true);
        mProgressPaint.setStyle(Paint.Style.FILL);
        mProgressPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        mTextPaint = new Paint();
        mTextPaint.setColor(mTextPaintColor);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);
        mTextPaint.setTextSize(mTextSize);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth,mHeight);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        float ratio = getProgress()*1.0f/getMax();

        //画圆
        mPaintCanvas.drawCircle(mWidth/2,mHeight/2,mHeight/2,mCirclePaint);

        //画进度
        mProgressPath.reset();
        //从右上边开始draw path
        int rightTop = (int) ((1-ratio)*mHeight);
        mProgressPath.moveTo(mWidth,rightTop);
        mProgressPath.lineTo(mWidth,mHeight);
        mProgressPath.lineTo(0,mHeight);
        mProgressPath.lineTo(0,rightTop);
        //画贝塞尔曲线，形成波浪线
        int count = (int) Math.ceil(mHeight*1.0f/(mShapeTop*4));
        //不是单击animation状态
        if(!isSingleTapAnimation&&getProgress()>0) {
//            float top = (0.5f-ratio)*mShapeTop*2;
            float top = (mTargetProgress-getProgress())*1.0f/mTargetProgress*mShapeTop;
            for(int i=0; i<count; i++) {
                mProgressPath.rQuadTo(mShapeTop,-top,2*mShapeTop,0);
                mProgressPath.rQuadTo(mShapeTop,top,2*mShapeTop,0);
            }
        } else {
            //单击animation状态
            float top = (mSingleTapAnimationCount*1.0f/50)*10;
            //奇偶数时曲线切换
            if(mSingleTapAnimationCount%2==0) {
                for(int i=0; i<count; i++) {
                    mProgressPath.rQuadTo(mShapeTop*2,-top,2*mShapeTop,0);
                    mProgressPath.rQuadTo(mShapeTop*2,top,2*mShapeTop,0);
                }
            } else {
                for(int i=0; i<count; i++) {
                    mProgressPath.rQuadTo(mShapeTop*2,top,2*mShapeTop,0);
                    mProgressPath.rQuadTo(mShapeTop*2,-top,2*mShapeTop,0);
                }
            }

        }
        mProgressPath.close();
        mPaintCanvas.drawPath(mProgressPath,mProgressPaint);

        //画进度文字
        String text = ((int)(ratio*100))+"%";
        //获得文字的宽度
        float textWidth = mTextPaint.measureText(text);
        Paint.FontMetrics metrics = mTextPaint.getFontMetrics();
        //descent+ascent为负数，所以是减而不是加
        float baseLine = mHeight*1.0f/2 - (metrics.descent+metrics.ascent)/2;
        mPaintCanvas.drawText(text,mWidth/2-textWidth/2,baseLine,mTextPaint);

        canvas.drawBitmap(mBitmap,0,0,null);
    }
}
