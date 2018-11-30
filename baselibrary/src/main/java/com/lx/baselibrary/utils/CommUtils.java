package com.lx.baselibrary.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * com.lx.baselibrary.utils
 * DemoUtils
 * Created by lixiao2
 * 2018/11/29.
 */

public class CommUtils {

    private static Toast mToast;
    // 显示Toast
    public static void showToast(Context context, String message) {
        if (mToast == null) {
            mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(message);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }
    // 取消Toast
    public static void cancleToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }
}
