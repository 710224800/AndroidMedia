package com.media.yanhaolu.opengl.image;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import java.io.IOException;

/**
 * Created by yanhaolu on 2017/9/18.
 */

public class MyGLSrufaceView_Image extends GLSurfaceView{

    private MyRender_Image render;

    public MyGLSrufaceView_Image(Context context) {
        this(context, null);
    }

    public MyGLSrufaceView_Image(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        setEGLContextClientVersion(2);
        render=new MyRender_Image(this, MyRender_Image.Filter.NONE);
        setRenderer(render);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        try {
            render.setBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("texture/fengj.png")));
            requestRender();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MyRender_Image getRender(){
        return render;
    }

    public void setFilter(MyRender_Image.Filter filter){
        render.setFilter(filter);
    }
}
