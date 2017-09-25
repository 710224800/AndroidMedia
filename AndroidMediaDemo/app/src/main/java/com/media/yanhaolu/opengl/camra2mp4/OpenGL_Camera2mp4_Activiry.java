package com.media.yanhaolu.opengl.camra2mp4;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.media.yanhaolu.androidmediademo.R;
import com.media.yanhaolu.opengl.coder.CameraRecorder;
import com.media.yanhaolu.utils.PermissionUtils;
import com.media.yanhaolu.utils.annotation.ViewInject;
import com.media.yanhaolu.utils.annotation.ViewUtil;
import com.media.yanhaolu.widget.CircularProgressView;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yanhaolu on 2017/9/20.
 */

public class OpenGL_Camera2mp4_Activiry extends AppCompatActivity implements GLRender_Camera2mp4.FrameCallback{

    public static final String TAG = "OpenGL_Camera_Activiry";

    @ViewInject(R.id.gl_surface_view)
    private GLSurfaceView_Camera2mp4 glSurfaceView;

    @ViewInject(R.id.mCapture)
    private CircularProgressView mCapture;

    private ExecutorService mExecutor;
    private long maxTime=20000;
    private long timeStep=50;
    private boolean recordFlag=false;
    private int type;       //1为拍照，0为录像
    private long time;

    private CameraRecorder mp4Recorder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionUtils.askPermission(this,new String[]{Manifest.permission.CAMERA,Manifest
                .permission.WRITE_EXTERNAL_STORAGE},10,initViewRunnable);
    }

    private Runnable initViewRunnable=new Runnable() {
        @Override
        public void run() {
            mExecutor= Executors.newSingleThreadExecutor();
//            setContentView(R.layout.activity_camera);
//            mCameraView= (CameraView)findViewById(R.id.mCameraView);
            setContentView(R.layout.activity_camera2mp4);
            ViewUtil.inject(OpenGL_Camera2mp4_Activiry.this);

            glSurfaceView.setOnPreviewFrameCallbackWithBuffer(callback);
            glSurfaceView.setFrameCallback(384,640,OpenGL_Camera2mp4_Activiry.this);

            mCapture.setTotal((int)maxTime);
            mCapture.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            recordFlag=false;
                            time=System.currentTimeMillis();
                            mCapture.postDelayed(captureTouchRunnable,500);
                            break;
                        case MotionEvent.ACTION_UP:
                            recordFlag=false;
                            if(System.currentTimeMillis()-time<500){
                                mCapture.removeCallbacks(captureTouchRunnable);
//                                glSurfaceView.setFrameCallback(720,1280,OpenGL_Camera2mp4_Activiry.this);
//                                glSurfaceView.takePhoto();
                            }
                            break;
                    }
                    return false;
                }
            });
        }
    };


    //录像的Runnable
    private Runnable captureTouchRunnable=new Runnable() {
        @Override
        public void run() {
            recordFlag=true;
            mExecutor.execute(recordRunnable);
        }
    };

    private Runnable recordRunnable=new Runnable() {

        @Override
        public void run() {
            type=0;
            long timeCount=0;
//                    if(mRecorder==null){
//                        mRecorder=new CameraRecorder();
//                        mAudioEncoder=new AudioEncoder();
//                    }
            if(mp4Recorder==null){
                mp4Recorder=new CameraRecorder();
            }
            long time=System.currentTimeMillis();
            String savePath=getPath("video/",time+".mp4");
            mp4Recorder.setSavePath(getPath("video/",time+""),"mp4");
//                    mAudioEncoder.setSavePath(getVideoPath(time+".aac"));
            try {
//                        mRecorder.prepare(360,640);
//                        mRecorder.start();
                mp4Recorder.prepare(384,640);
                mp4Recorder.start();
                glSurfaceView.setFrameCallback(384,640,OpenGL_Camera2mp4_Activiry.this);
                glSurfaceView.startRecord();
                while (timeCount<=maxTime&&recordFlag){
                    long start=System.currentTimeMillis();
                    mCapture.setProcess((int)timeCount);
                    long end=System.currentTimeMillis();
                    try {
                        Thread.sleep(timeStep-(end-start));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    timeCount+=timeStep;
                }
                glSurfaceView.stopRecord();
//                    mRecorder.stop();

                if(timeCount<2000){
                    mp4Recorder.cancel();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCapture.setProcess(0);
                            Toast.makeText(OpenGL_Camera2mp4_Activiry.this,"录像时间太短了",Toast.LENGTH_SHORT).show();

                        }
                    });
                }else{
                    mp4Recorder.stop();
                    recordComplete(type,savePath);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    private String getBaseFolder(){
        String baseFolder= Environment.getExternalStorageDirectory()+"/Codec/";
        File f=new File(baseFolder);
        if(!f.exists()){
            boolean b=f.mkdirs();
            if(!b){
                baseFolder=getExternalFilesDir(null).getAbsolutePath()+"/";
            }
        }
        return baseFolder;
    }

    //获取VideoPath
    private String getPath(String path,String fileName){
        String p= getBaseFolder()+path;
        File f=new File(p);
        if(!f.exists()&&!f.mkdirs()){
            return getBaseFolder()+fileName;
        }
        return p+fileName;
    }

    private void recordComplete(int type,final String path){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCapture.setProcess(0);
                Toast.makeText(OpenGL_Camera2mp4_Activiry.this,"文件保存路径："+path,Toast.LENGTH_SHORT).show();
            }
        });
    }

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
        mp4Recorder.feedData(bytes, time);
    }
}
