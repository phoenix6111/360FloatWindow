package com.wanghaisheng.floatwindow.util;

import android.content.Context;
import android.util.TypedValue;

import java.lang.reflect.Field;

/**
 * Author: sheng on 2016/9/14 18:58
 * Email: 1392100700@qq.com
 */
public class DesityUtils {

    private DesityUtils()
    {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 获取屏幕宽度
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * sp转px
     * @param spValue
     * @return
     */
    public static int sp2px(Context context,int spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,spValue,context.getResources().getDisplayMetrics());
    }

    /**
     * dp转px
     * @param dpValue
     * @return
     */
    public static int dp2px(Context context,int dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dpValue,context.getResources().getDisplayMetrics());
    }

    /**
     * 获取状态栏高度
     * @param context
     * @return
     */
    public static int getStatubarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);
        } catch(Exception e1) {
            e1.printStackTrace();

        }

        return sbar;
    }

}
