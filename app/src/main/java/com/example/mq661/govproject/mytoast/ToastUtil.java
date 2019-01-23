package com.example.mq661.govproject.mytoast;


import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mq661.govproject.R;

import java.util.List;
import java.util.Stack;
import java.util.Vector;


/**
 * <b>类   名：</b>${TYPE_NAME} <br/>
 * <b>类描述：</b> <br/>
 * <b>创建人：</b>zhangqi<br/>
 * <b>创建时间：</b>2017/6/6<br/>
 * <b>修改人：</b>zhangqi<br/>
 * <b>修改时间：</b>2017/6/6<br/>
 * <b>修改备注：</b><br/>
 *
 * @version 1.0.0<br/>
 */
public class ToastUtil {
    public static final int LENGTH_SHORT = 0x00;
    public static final int LENGTH_LONG = 0x01;

    private final int ANIMATION_DURATION = 600;

    public Context mContext;
    public String msg;
    private int HIDE_DELAY = 2000;

    public static boolean isRunning=false;

    private Handler mHandler = new Handler();

    public static Stack<ToastUtil> stack = new Stack();
    // 表示吐司里显示的文字
    public static ToastUtil makeText(Context context, String message,
                                     int HIDE_DELAY) {
        ToastUtil utils=new ToastUtil();
        utils.mContext=context;
        utils.msg=message;

        if (HIDE_DELAY == LENGTH_LONG) {
            utils.HIDE_DELAY= 2500;
        } else {
            utils.HIDE_DELAY= 1500;
        }

        return utils;
    }

    public static void wakeUp(){
        isRunning=true;
        if (!stack.empty()) {
            ToastUtil util=stack.pop();
            util.doshow();

        }else{
            isRunning=false;
        }


    }

    public void doshow(){
        final ViewGroup container = (ViewGroup) ((Activity) mContext)
                .findViewById(android.R.id.content);
        final View mView = ((Activity) mContext).getLayoutInflater().inflate(
                R.layout.toast_layout, null);
        container.addView(mView);

        final LinearLayout mContainer = (LinearLayout) mView.findViewById(R.id.mbContainer);
        mContainer.setVisibility(View.GONE);
        TextView mTextView = (TextView) mView.findViewById(R.id.mbMessage);
        mTextView.setText(msg);

        // 显示动画
        AlphaAnimation mFadeInAnimation = new AlphaAnimation(0.0f, 1.0f);
        // 消失动画
        final AlphaAnimation mFadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
        mFadeOutAnimation.setDuration(ANIMATION_DURATION);
        mFadeOutAnimation
                .setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        // 消失动画后更改状态为 未显示

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // 隐藏布局，不使用remove方法为防止多次创建多个布局
                        mContainer.setVisibility(View.GONE);
                        container.removeView(mView);
                        wakeUp();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
        mContainer.setVisibility(View.VISIBLE);

        mFadeInAnimation.setDuration(ANIMATION_DURATION);

        mContainer.startAnimation(mFadeInAnimation);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mContainer.startAnimation(mFadeOutAnimation);
            }
        }, HIDE_DELAY);
    }

    public void show() {
        stack.push(this);
        if(!isRunning){
            wakeUp();

        }
    }

}