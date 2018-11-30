package com.lx.demoutils.utils;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * com.lx.demoutils.utils
 * DemoUtils
 * Created by lixiao2
 * 2018/10/8.
 */

public class PhoneUtils {

    // 显示或隐藏状态栏
    public static void setStatusBarVisible(Activity activity, boolean isShow) {
        int uiFlags;
        if (isShow) {
            uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        } else {
            uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
        }
        activity.getWindow().getDecorView().setSystemUiVisibility(uiFlags);
    }

    // 显示或隐藏导航栏
    public static  void setNavigationBarVisible(Activity activity, boolean isShow) {
        int uiFlags;
        if (isShow) {
            uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        } else {
            uiFlags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        }
        activity.getWindow().getDecorView().setSystemUiVisibility(uiFlags);
    }

    // 状态栏透明
    public static void setStatusBarTranslucent(Activity activity) {
        // android 4.4以下不支持状态栏透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //android 5.0以上 - android 6.0(不包括)半透明 但是国内大部分手机厂商都改为了全透明,android 6.0以上全透明
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // android 6.0以上支持状态栏颜色翻转
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            window.getDecorView().setSystemUiVisibility(flags);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // android4.4以上-android5.0(不包括)渐变透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public static void setNavigationTranslucent(Activity activity) {
        // android 4.4以下不支持导航栏透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //android 5.0以上 - android 6.0(不包括)半透明 但是国内大部分手机厂商都改为了全透明,android 6.0以上全透明
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            int flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // android 6.0以上支持状态栏颜色翻转
                flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
            window.getDecorView().setSystemUiVisibility(flags);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // android4.4以上-android5.0(不包括)渐变透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }
}
