package com.media.yanhaolu.opengl.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by yanhaolu on 2017/9/20.
 */

public class GLSurfaceView_Camera extends GLSurfaceView {

    private GLRender_Camera render;

    public GLSurfaceView_Camera(Context context) {
        this(context, null);
    }

    public GLSurfaceView_Camera(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        setEGLContextClientVersion(2);
        render = new GLRender_Camera(this);
        setRenderer(render);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        render.setOnFrameAvailableListener(new GLRender_Camera.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                requestRender();
            }
        });
    }

    public void switchCamera(){
        render.switchCamera();
        render.runSwitch();
        onPause();
        onResume();
        requestRender();
    }

    @Override
    public void onPause() {
        super.onPause();
        render.closeCamera();
    }
}
