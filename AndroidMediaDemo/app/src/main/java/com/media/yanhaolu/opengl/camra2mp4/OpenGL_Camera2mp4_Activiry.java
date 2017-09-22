package com.media.yanhaolu.opengl.camra2mp4;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.media.yanhaolu.utils.PermissionUtils;

/**
 * Created by yanhaolu on 2017/9/20.
 */

public class OpenGL_Camera2mp4_Activiry extends AppCompatActivity implements GLRender_Camera2mp4.FrameCallback{

    public static final String TAG = "OpenGL_Camera_Activiry";

    private GLSurfaceView_Camera2mp4 glSurfaceView;

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
            glSurfaceView = new GLSurfaceView_Camera2mp4(OpenGL_Camera2mp4_Activiry.this);
            setContentView(glSurfaceView);
            glSurfaceView.setOnPreviewFrameCallbackWithBuffer(callback);
            glSurfaceView.setFrameCallback(384,640,OpenGL_Camera2mp4_Activiry.this);
        }
    };

    private final ICamera2mp4.PreviewFrameCallback callback = new ICamera2mp4.PreviewFrameCallback(){

        @Override
        public void onPreviewFrame(byte[] bytes, int width, int height) {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("切换摄像头").setTitle("切换摄像头").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String name=item.getTitle().toString();
        if(name.equals("切换摄像头")){
            glSurfaceView.switchCamera();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFrame(byte[] bytes, long time) {
        Log.i(TAG, bytes.toString() + time);
    }
}
