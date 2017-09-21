package com.media.yanhaolu.opengl.camera;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.media.yanhaolu.utils.PermissionUtils;

/**
 * Created by yanhaolu on 2017/9/20.
 */

public class OpenGL_Camera_Activiry extends AppCompatActivity{

    private GLSurfaceView_Camera glSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionUtils.askPermission(this,new String[]{Manifest.permission.CAMERA,Manifest
                .permission.WRITE_EXTERNAL_STORAGE},10,initViewRunnable);
    }

    private Runnable initViewRunnable=new Runnable() {
        @Override
        public void run() {
//            setContentView(R.layout.activity_camera);
//            mCameraView= (CameraView)findViewById(R.id.mCameraView);
            glSurfaceView = new GLSurfaceView_Camera(OpenGL_Camera_Activiry.this);
            setContentView(glSurfaceView);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }
}
