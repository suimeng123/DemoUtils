package com.lx.demoutils;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.lx.demoutils.activity.LiveDataActivity;
import com.lx.demoutils.activity.MyViewActivity;
import com.lx.demoutils.utils.PhoneUtils;

public class MainActivity extends AppCompatActivity implements LifecycleOwner {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
//        PhoneUtils.setStatusBarTranslucent(this);
//        PhoneUtils.setNavigationTranslucent(this);
        PhoneUtils.setStatusBarVisible(this, false);
//        PhoneUtils.setNavigationBarVisible(this, false);
        setContentView(R.layout.activity_main);
    }

    public void BtnClick(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                // LiveData实例
                startActivity(new Intent(this, LiveDataActivity.class));
                break;
            case R.id.btn2:
                // 自定义组件
                startActivity(new Intent(this, MyViewActivity.class));
                break;
            default:
                break;
        }
    }
}
