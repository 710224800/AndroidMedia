package com.media.yanhaolu.opengl.camra2mp4;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by yanhaolu on 2017/9/20.
 */

public class GLSurfaceView_Camera2mp4 extends GLSurfaceView {

    private GLRender_Camera2mp4 render;

    public GLSurfaceView_Camera2mp4(Context context) {
        this(context, null);
    }

    public GLSurfaceView_Camera2mp4(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        setEGLContextClientVersion(2);
        render = new GLRender_Camera2mp4(this);
        setRenderer(render);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        render.setOnFrameAvailableListener(new GLRender_Camera2mp4.OnFrameAvailableListener() {
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

    public void setOnPreviewFrameCallbackWithBuffer(final ICamera2mp4.PreviewFrameCallback callback){
        render.setOnPreviewFrameCallbackWithBuffer(callback);
    }

    public void setFrameCallback(int frameCallbackWidth, int frameCallbackHeight, GLRender_Camera2mp4.FrameCallback frameCallback) {
        render.setFrameCallback(frameCallbackWidth,frameCallbackHeight,frameCallback);
    }

}
