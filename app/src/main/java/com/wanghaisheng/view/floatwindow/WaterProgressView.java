package com.wanghaisheng.view.floatwindow;

import android.content.Context;
import android.content.res.TypedArray;
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

import com.wanghaisheng.floatwindow.R;
import com.wanghaisheng.floatwindow.util.DesityUtils;
import com.wanghaisheng.floatwindow.util.ToastUtil;

/**
 * Author: sheng on 2016/9/15 22:59
 * Email: 1392100700@qq.com
 */
public class WaterProgressView extends ProgressBar {

    //默认圆的背景色
    public static final int DEFAULT_CIRCLE_COLOR = 0xff00cccc;
    //默认进度的颜色
    public static final int DEFAULT_PROGRESS_COLOR = 0xff00CC66;
    //默认文字的颜色
    public static final int DEFAULT_TEXT_COLOR = 0xffffffff;
    //默认文字的大小
    public static final int DEFAULT_TEXT_SIZE = 18;
    public static final int DEFAULT_RIPPLE_TOPHEIGHT = 10;

    private Context mContext;

    private Canvas mPaintCanvas;
    private Bitmap mBitmap;

    //画圆的画笔
    private Paint mCirclePaint;
    //画圆的画笔的颜色
    private int mCircleColor = 0xff00CCCC;

    //画进度的画笔
    private Paint mProgressPaint;
    //画进度的画笔的颜色
    private int mProgressColor = 0xff00CC66;
    //画进度的path
    private Path mProgressPath;
    private int mRippleTop = 10;

    //进度文字的画笔
    private Paint mTextPaint;
    //进度文字的颜色
    private int mTextColor = 0xffffffff;
    private int mTextSize = 18;
    //当前进度
    private volatile int mCurrentProgress = 0;
    //最大进度
    private int mMaxProgress = 100;
    //目标进度，也就是双击时处理任务的进度，会影响曲线的振幅
    private int mTargetProgress = 50;

    private GestureDetector mGestureDetector;
    //正在进行单击动画的标志
    private boolean isSingleTapAnimation;
    //单击动画进行的次数，默认为50
    private int mSingleTapAnimationCount = 50;

    public WaterProgressView(Context context) {
        this(context,null);
    }

    public WaterProgressView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public WaterProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.mContext = context;

        getAttrValue(attrs);

        //初始化画笔的相关属性
        initPaint();
        mProgressPath = new Path();

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
     * 获取自定义属性的值
     * @param attrs
     */
    private void getAttrValue(AttributeSet attrs) {
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.WaterProgressView);

        mCircleColor = ta.getColor(R.styleable.WaterProgressView_circle_color,DEFAULT_CIRCLE_COLOR);
        mProgressColor = ta.getColor(R.styleable.WaterProgressView_progress_color,DEFAULT_PROGRESS_COLOR);
        mTextColor = ta.getColor(R.styleable.WaterProgressView_text_color,DEFAULT_TEXT_COLOR);
        mTextSize = (int) ta.getDimension(R.styleable.WaterProgressView_text_size, DesityUtils.sp2px(mContext,DEFAULT_TEXT_SIZE));
        mRippleTop = (int) ta.getDimension(R.styleable.WaterProgressView_ripple_topheight,DesityUtils.dp2px(mContext,DEFAULT_RIPPLE_TOPHEIGHT));

        ta.recycle();
    }

    /**
     * 监听单击双击事件
     */
    private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            ToastUtil.showToast(mContext,"双击");
            //双击动画
            startDoubleTapAnimation();
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            ToastUtil.showToast(mContext,"单击");
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
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setDither(true);

        mProgressPaint = new Paint();
        mProgressPaint.setColor(mProgressColor);
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setDither(true);
        mProgressPaint.setStyle(Paint.Style.FILL);
        mProgressPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        mTextPaint = new Paint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);
        mTextPaint.setTextSize(mTextSize);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(width,height);

        mBitmap = Bitmap.createBitmap(width-getPaddingLeft()-getPaddingRight(),height-getPaddingTop()-getPaddingBottom(), Bitmap.Config.ARGB_8888);
        mPaintCanvas = new Canvas(mBitmap);

    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        float ratio = getProgress()*1.0f/getMax();

        int width = getWidth()-getPaddingLeft()-getPaddingRight();
        int height = getHeight()-getPaddingTop()-getPaddingBottom();

        //画圆
        mPaintCanvas.drawCircle(width/2,height/2,height/2,mCirclePaint);

        //画进度
        mProgressPath.reset();
        //从右上边开始draw path
        int rightTop = (int) ((1-ratio)*height);
        mProgressPath.moveTo(width,rightTop);
        mProgressPath.lineTo(width,height);
        mProgressPath.lineTo(0,height);
        mProgressPath.lineTo(0,rightTop);
        //画贝塞尔曲线，形成波浪线
        int count = (int) Math.ceil(height*1.0f/(mRippleTop *4));
        //不是单击animation状态
        if(!isSingleTapAnimation&&getProgress()>0) {
            float top = (mTargetProgress-getProgress())*1.0f/mTargetProgress* mRippleTop;
            for(int i=0; i<count; i++) {
                mProgressPath.rQuadTo(mRippleTop,-top,2* mRippleTop,0);
                mProgressPath.rQuadTo(mRippleTop,top,2* mRippleTop,0);
            }
        } else {
            //单击animation状态
            float top = (mSingleTapAnimationCount*1.0f/50)*10;
            //奇偶数时曲线切换
            if(mSingleTapAnimationCount%2==0) {
                for(int i=0; i<count; i++) {
                    mProgressPath.rQuadTo(mRippleTop *2,-top*2,2* mRippleTop,0);
                    mProgressPath.rQuadTo(mRippleTop *2,top*2,2* mRippleTop,0);
                }
            } else {
                for(int i=0; i<count; i++) {
                    mProgressPath.rQuadTo(mRippleTop *2,top*2,2* mRippleTop,0);
                    mProgressPath.rQuadTo(mRippleTop *2,-top*2,2* mRippleTop,0);
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
        float baseLine = height*1.0f/2 - (metrics.descent+metrics.ascent)/2;
        mPaintCanvas.drawText(text,width/2-textWidth/2,baseLine,mTextPaint);

        canvas.drawBitmap(mBitmap,0,0,null);
    }
}
