package com.lx.demoutils.activity;

import android.arch.lifecycle.LifecycleOwner;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.lx.demoutils.R;
import com.lx.demoutils.fragment.ViewModelFragment;
import com.lx.demoutils.fragment.ViewModelFragment2;

/**
 * com.lx.demoutils
 * DemoUtils
 * Created by lixiao2
 * 2018/9/25.
 */

public class LiveDataActivity extends AppCompatActivity implements LifecycleOwner {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livedata);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.fragment1,new ViewModelFragment(), "fragment1").add(R.id.fragment2,new ViewModelFragment2(),"fragment2");
        transaction.commit();
    }

    private void initData(){
    }
}
