package com.wanghaisheng.floatwindow.engine;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import com.wanghaisheng.floatwindow.util.DesityUtils;
import com.wanghaisheng.view.floatwindow.FloatCircleView;
import com.wanghaisheng.view.floatwindow.FloatMenuView;

/**
 * Author: sheng on 2016/9/14 10:12
 * Email: 1392100700@qq.com
 * 浮窗管理类，单例
 * 通过这个管理类来管理浮窗的显示和隐藏以及拖动
 */
public class FloatWindowManager implements View.OnTouchListener{

    private Context mContext;
    private WindowManager mWindowManager;
    private static FloatWindowManager instance;
    //浮窗对象
    private FloatCircleView mFloatCircleView;
    //FloatCircleView的LayoutParams
    private WindowManager.LayoutParams mFloatCircleViewLayoutParams;
    //记录FloatCircleView上一次触摸点的坐标（绝对坐标）
    private float mLastX;
    private float mLastY;

    //第一次触摸点的坐标
    private float mFirstX;
    private float mFirstY;

    //拖动事件的响应最低值
    private float mTouchSlop;

    private WindowManager.LayoutParams mFloatMenuViewLayoutParams;
    private FloatMenuView mFloatMenuView;

    private FloatWindowManager(Context context) {
        this.mContext = context;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mFloatCircleView = new FloatCircleView(context);
        mFloatCircleView.setOnTouchListener(this);
        mFloatCircleView.setOnClickListener(new View.OnClickListener() {
            //FloatCircleView点击事件
            @Override
            public void onClick(View view) {
                Log.d("tag","onclick");
//                Toast.makeText(mContext,"FloatCircleView onclicked",Toast.LENGTH_SHORT).show();
                hideFloatCircleView();
                showFloatMenuView();
                mFloatMenuView.startAnimation();
            }
        });

        mFloatMenuView = new FloatMenuView(context);

        //初始化最小手动响应距离
        mTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
    }

    public static FloatWindowManager getInstance(Context context) {
        if(instance == null) {
            synchronized (FloatWindowManager.class) {
                if(instance == null) {
                    instance = new FloatWindowManager(context);
                }
            }
        }

        return instance;
    }

    /**
     * 显示浮窗体
     */
    public void showFloatCircleView() {
        if(mFloatCircleViewLayoutParams == null) {
            mFloatCircleViewLayoutParams = new WindowManager.LayoutParams();
            //设置宽和高为FloatCircleView的宽和高
            mFloatCircleViewLayoutParams.width = mFloatCircleView.getWidth();
            mFloatCircleViewLayoutParams.height = mFloatCircleView.getHeight();
            //设置对齐方式
            mFloatCircleViewLayoutParams.gravity = Gravity.LEFT|Gravity.TOP;
            //设置x,y点坐标
            mFloatCircleViewLayoutParams.x = 0;
            mFloatCircleViewLayoutParams.y = 0;
            //设置不可抢占焦点
            mFloatCircleViewLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            //设置类型为phone
            mFloatCircleViewLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            //设置背景颜色类型
            mFloatCircleViewLayoutParams.format = PixelFormat.RGBA_8888;
        }

        mWindowManager.addView(mFloatCircleView, mFloatCircleViewLayoutParams);
    }

    public void hideFloatCircleView() {
        mWindowManager.removeView(mFloatCircleView);
    }

    public void hideFloatMenuView() {
        mWindowManager.removeView(mFloatMenuView);
    }

    /**
     * 显示浮窗体
     */
    public void showFloatMenuView() {
        if(mFloatMenuViewLayoutParams == null) {
            mFloatMenuViewLayoutParams = new WindowManager.LayoutParams();
            //设置宽和高为FloatCircleView的宽和高
            mFloatMenuViewLayoutParams.width = DesityUtils.getScreenWidth(mContext);
            mFloatMenuViewLayoutParams.height = DesityUtils.getScreenHeight(mContext)-DesityUtils.getStatubarHeight(mContext);
            //设置对齐方式
            mFloatMenuViewLayoutParams.gravity = Gravity.LEFT|Gravity.BOTTOM;
            //设置x,y点坐标
            mFloatMenuViewLayoutParams.x = 0;
            mFloatMenuViewLayoutParams.y = 0;
            //设置不可抢占焦点
            mFloatMenuViewLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            //设置类型为phone
            mFloatMenuViewLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            //设置背景颜色类型
            mFloatMenuViewLayoutParams.format = PixelFormat.RGBA_8888;
        }
        mWindowManager.addView(mFloatMenuView, mFloatMenuViewLayoutParams);
    }


    /**
     * 处理手势触摸事件
     * @param view
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //记录第一次触摸点的坐标
                mLastX = x;
                mLastY = y;

                mFirstX = x;
                mFirstY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = x - mLastX;
                float deltay = y - mLastY;
                //当手指移动时，更新其layoutParams，更新位置
                mFloatCircleViewLayoutParams.x += deltaX;
                mFloatCircleViewLayoutParams.y += deltay;
                mWindowManager.updateViewLayout(mFloatCircleView, mFloatCircleViewLayoutParams);

                mLastX = x;
                mLastY = y;
                mFloatCircleView.setDragState(true);
                break;

            case MotionEvent.ACTION_UP:
                //当放开手指的时候，让FloatCircleView移动到左边或右边
                //如果处于屏幕中间线的左边则让其移动到左边，如果处理屏幕中间线的右边则让其移动到右边
                int width = DesityUtils.getScreenWidth(mContext);

                if(x > width/2) {
                    mFloatCircleViewLayoutParams.x = width - mFloatCircleView.getWidth();
                } else {
                    mFloatCircleViewLayoutParams.x = 0;
                }
                mWindowManager.updateViewLayout(mFloatCircleView, mFloatCircleViewLayoutParams);
                mFloatCircleView.setDragState(false);

                //处理拖动与点击事件冲突的解决：当搬运距离大于3的时候，不响应点击事件，否则响应点击事件
                Log.d("action_up","can drag "+canDragAction(x - mFirstX, y - mFirstY));
                return canDragAction(x - mFirstX, y - mFirstY);
        }

        return false;
    }

    /**
     * 判断滑动距离是否足够响应滑动事件
     * @param x
     * @param y
     * @return
     */
    private boolean canDragAction(float x,float y) {
        return Math.sqrt(x*x+y*y) > mTouchSlop;
    }
}
