package com.frank.ffmpeg.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.frank.ffmpeg.R;
import com.frank.ffmpeg.kotlin.activity.MainKotlinActivity;

/**
 * 启动页面
 * Created by frank on 2020/2/28.
 */
public class LauncherActivity extends BaseActivity {

    private final static boolean useKotlin = true;

    @Override
    int getLayoutId() {
        return R.layout.activity_launcher;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
    }

    private void initView() {
        ImageView imgFFmpeg = getView(R.id.img_ffmpeg);
        TextView txtAndroid = getView(R.id.txt_android);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        height /= 2;
        TranslateAnimation animationDown = new TranslateAnimation(0, 0, -height, 0);
        animationDown.setDuration(1500);
        imgFFmpeg.setAnimation(animationDown);
        animationDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                LauncherActivity.this.finish();
                jumpToMain();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animationDown.start();
        TranslateAnimation animationUp = new TranslateAnimation(0, 0, height, 0);
        animationUp.setDuration(1500);
        txtAndroid.setAnimation(animationUp);
        animationUp.start();
    }

    private void jumpToMain() {
        Intent intent = new Intent();
        intent.setClass(LauncherActivity.this, useKotlin ? MainKotlinActivity.class : MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onViewClick(View v) {

    }

    @Override
    void onSelectedFile(String filePath) {

    }

}
