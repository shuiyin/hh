package com.example.lance.mydemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by lance on 2017/2/9.
 */

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback{
    public RcActivity activity;
    public Paint mPaint;//画笔
    public int currentAlpha = 0;//当前的不透明值


    public int screenWidth = 720;//屏幕宽度
    public int screenHeight = 1000;//屏幕高度
    public int sleepSpan = 50;//动画的延时ms

    Bitmap[] logos = new Bitmap[2];//logo图片数组
    Bitmap currentLogo;//当前logo图片引用
    public int currentX;
    public int currentY;


    public MySurfaceView(RcActivity activity) {
        super(activity);
        this.activity = activity;
        this.getHolder().addCallback(this);//设置生命周期回调接口的实现者
        mPaint = new Paint();//创建画笔
        mPaint.setAntiAlias(true);//打开抗锯齿

        //加载图片
        logos[0] = BitmapFactory.decodeResource(activity.getResources(),R.drawable.dukea);
        logos[1] = BitmapFactory.decodeResource(activity.getResources(),R.drawable.dukeb);


    }

    public void onDraw(Canvas canvas){
        //绘制黑填充矩形清背景
        mPaint.setColor(Color.BLACK);//设置画笔颜色
        mPaint.setAlpha(255);
        canvas.drawRect(0,0,screenWidth,screenHeight,mPaint);

        //进行平面贴图
        if(currentLogo == null){
            return;
        }
        mPaint.setAlpha(currentAlpha);
        canvas.drawBitmap(currentLogo,currentX,currentY,mPaint);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {//创建时被调用
        new Thread(){
            public void run(){
                for(Bitmap bm : logos){
                    currentLogo = bm;
                    //计算图片位置
                    currentX = screenWidth/2 - bm.getWidth()/2;
                    currentY = screenHeight/2 - bm.getHeight()/2;

                    for(int i=255;i>-10;i=i-10){
                        //动态更改图片的透明度值并不断重绘
                        currentAlpha = i;
                        if(currentAlpha < 0){
                            currentAlpha = 0;
                        }
                        SurfaceHolder myholder = MySurfaceView.this.getHolder();
                        Canvas canvas = myholder.lockCanvas();//获取画布
                        try {
                            synchronized (myholder){
                                onDraw(canvas);//绘制
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }finally {
                            if(canvas != null){
                                myholder.unlockCanvasAndPost(canvas);
                            }
                        }

                        try {
                            if(i == 255){
                                //若是新图片，多等待一会
                                Thread.sleep(1000);
                            }
                            Thread.sleep(sleepSpan);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                activity.hd.sendEmptyMessage(0);
            }
        }.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {//销毁时被调用

    }
}
