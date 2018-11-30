package com.lx.demoutils.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.lx.baselibrary.utils.CommUtils;
import com.lx.demoutils.R;
import com.lx.demoutils.utils.PhoneUtils;
import com.lx.demoutils.view.CropScaleImageView;

/**
 * com.lx.demoutils.activity
 * DemoUtils
 * Created by lixiao2
 * 2018/10/8.
 */

public class MyViewActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        PhoneUtils.setStatusBarVisible(this,false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_view);

        final CropScaleImageView cropImageView = findViewById(R.id.crop_img);
        final ImageView imageView = findViewById(R.id.show_img);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = cropImageView.getClipRectfBitmap();
                imageView.setImageBitmap(bitmap);
            }
        });
        CommUtils.showToast(this,"测试成功") ;
    }
}
