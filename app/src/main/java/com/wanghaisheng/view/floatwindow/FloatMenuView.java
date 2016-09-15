package com.wanghaisheng.view.floatwindow;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.wanghaisheng.floatwindow.R;
import com.wanghaisheng.floatwindow.engine.FloatWindowManager;

/**
 * Author: sheng on 2016/9/15 12:58
 * Email: 1392100700@qq.com
 */
public class FloatMenuView extends LinearLayout {

    Animation animation;

    public FloatMenuView(Context context) {
        super(context);

//        View menu = LayoutInflater.from(context).inflate(R.layout.flow_menu_layout,this);
        View menu = View.inflate(getContext(),R.layout.flow_menu_layout,null);
        LinearLayout llMenuRoot = (LinearLayout) menu.findViewById(R.id.ll_menu_root);
        animation = AnimationUtils.loadAnimation(context,R.anim.float_menu_in);
        animation.setFillAfter(true);
        llMenuRoot.setAnimation(animation);

        menu.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                FloatWindowManager manager = FloatWindowManager.getInstance(getContext());
                manager.hideFloatMenuView();
                manager.showFloatCircleView();
                return false;
            }
        });
        addView(menu);
    }



    public void startAnimation() {
        animation.start();
    }


}
