package com.media.yanhaolu.image;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.media.yanhaolu.androidmediademo.R;

/**
 * Created by yanhaolu on 2017/8/24.
 */

public class Drag extends Activity {
    DragImage view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view=new DragImage(this);
        setContentView(view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        view.stopRunning();
    }

    private class DragImage extends SurfaceView implements SurfaceHolder.Callback,Runnable,View.OnTouchListener {
        private Context context;
        private SurfaceHolder holder;
        private Bitmap icon;
        private Paint paint;
        private boolean running=true;

        public void stopRunning(){
            running = false;
        }

        public DragImage(Context context) {
            super(context);
            this.context=context;
            holder = this.getHolder();//获取holder
            holder.addCallback(this);
            this.setOnTouchListener(this);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {

            icon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.xiaoren);
            rect = new Rect(0, 0, 200, 400);
            paint=new Paint();
            running=true;
            new Thread(this).start();

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
        }


        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            running=false;
        }

        @Override
        public void run() {
            int SLEEP_TIME=100;
            while (running) {
                //开始画的时间    long start=System.currentTimeMillis();
                Canvas canvas = holder.lockCanvas();//获取画布
                if(canvas != null) {
                    canvas.drawColor(Color.BLACK);
                    canvas.drawBitmap(icon, rect.left, rect.top, null);
                    holder.unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
                    //结束的时间   long end=System.currentTimeMillis();
                }
            }
        }

        //      Region region=new Region();
        private Point point=new Point();//点击点
        private Rect rect;//图片的rect
        private boolean canDrag=false;//判断是否点击在图片上，否则拖动无效
        private int offsetX=0,offsetY=0;//点击点离图片左上角的距离
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            switch (event.getAction()) {

                //手按下的时候
                case MotionEvent.ACTION_DOWN:
                    point.x=(int)event.getX();
                    point.y=(int)event.getY();
                    if(rect.contains(point.x, point.y)){
                        canDrag=true;
                        offsetX=point.x-rect.left;
                        offsetY=point.y-rect.top;
                    }
                    break;

                //移动的时候
                case MotionEvent.ACTION_MOVE:
                    if(canDrag){
                        rect.left=(int)event.getX()-offsetX;
                        rect.top=(int)event.getY()-offsetY;
                        rect.right=rect.left+icon.getWidth();
                        rect.bottom=rect.top+icon.getHeight();
                        if (rect.left < 0) {
                            rect.left = 0;
                            rect.right =  rect.left+icon.getWidth();
                        }
                        if (rect.right >  getMeasuredWidth()) {
                            rect.right =  getMeasuredWidth();
                            rect.left = rect.right-icon.getWidth();
                        }
                        if (rect.top < 0) {
                            rect.top = 0;
                            rect.bottom = rect.top+icon.getHeight();
                        }
                        if (rect.bottom > getMeasuredHeight()) {
                            rect.bottom = getMeasuredHeight();
                            rect.top = rect.bottom-icon.getHeight();
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    canDrag=false;
                    break;

                default:
                    break;
            }
            return true;
        }

    }
}