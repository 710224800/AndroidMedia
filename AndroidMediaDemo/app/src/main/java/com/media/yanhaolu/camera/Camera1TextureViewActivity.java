package com.media.yanhaolu.camera;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.media.yanhaolu.androidmediademo.R;
import com.media.yanhaolu.utils.annotation.OnClick;
import com.media.yanhaolu.utils.annotation.ViewInject;
import com.media.yanhaolu.utils.annotation.ViewUtil;

import java.io.IOException;

public class Camera1TextureViewActivity extends AppCompatActivity {

    public static final String TAG = "Camera1TextureViewActivity";

    @ViewInject(R.id.textureView)
    private TextureView textureView;

    @ViewInject(R.id.ratate)
    private Button bt_rotate;

    @ViewInject(R.id.alpha)
    private Button bt_alpha;

    private Camera mCamera;
    private boolean isPreview = false;
    private SurfaceTexture mSurfaceTexture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera1_texture_view);
        ViewUtil.inject(this);
        textureView.setSurfaceTextureListener(new MySurfaceTextureListener());
    }

    public void initCamera(){
        if(!isPreview && null != mSurfaceTexture){
            mCamera = Camera.open();
            mCamera.setDisplayOrientation(90);
        }
        Camera.Parameters parameters = mCamera.getParameters();
        Camera.Size mSize = parameters.getSupportedPreviewSizes().get(0);
        Camera.Size pictureSize = parameters.getSupportedPictureSizes().get(0);
        parameters.setPreviewSize(mSize.width, mSize.height);
        parameters.setPictureSize(pictureSize.width, pictureSize.height);
        try {
            mCamera.setPreviewTexture(mSurfaceTexture);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mCamera.setPreviewCallbackWithBuffer(mJpegPreviewCallback);
        byte[] buffer = new byte[mSize.width*mSize.height*3/2];
        mCamera.addCallbackBuffer(buffer);
        //mCamera.setParameters(parameters);
        mCamera.startPreview();
        isPreview = true;
    }

    private final class MySurfaceTextureListener implements TextureView.SurfaceTextureListener {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface,
                                              int width, int height) {
            mSurfaceTexture = surface;
            initCamera();
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            if(null != mCamera){
                if(isPreview){
                    mCamera.setPreviewCallback(null); //！！这个必须在前，不然退出出错
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;
                    isPreview = false;
                }
            }
            return true;
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface,
                                                int width, int height) {

        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            // TODO Auto-generated method stub

        }
    }

    private boolean isRotation = false;
    private boolean isAlpha = false;

    @OnClick({R.id.alpha, R.id.ratate})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ratate:
                if(!isRotation){
                    textureView.setRotation(90f);
                    isRotation = true;
                }else{
                    textureView.setRotation(0f);
                    isRotation = false;
                }
                break;
            case R.id.alpha:
                if(!isAlpha){
                    textureView.setAlpha(0.5f);
                    isAlpha = true;
                }else{
                    textureView.setAlpha(1.0f);
                    isAlpha = false;
                    Toast.makeText(this, textureView.getAlpha()+"", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    // 【获取视频预览帧的接口】
    Camera.PreviewCallback mJpegPreviewCallback = new Camera.PreviewCallback(){
        @Override
        public void onPreviewFrame(byte[] data, Camera camera)
        {
            camera.addCallbackBuffer(data);
            //传递进来的data,默认是YUV420SP的
            try
            {
                Log.i(TAG, "going into onPreviewFrame" + data);

                //mYUV420sp = data;   // 获取原生的YUV420SP数据
//                YUVIMGLEN = data.length;
//
//                // 拷贝原生yuv420sp数据
//                mYuvBufferlock.acquire();
//                System.arraycopy(data, 0, mYUV420SPSendBuffer, 0, data.length);
//                //System.arraycopy(data, 0, mWrtieBuffer, 0, data.length);
//                mYuvBufferlock.release();
//
//                // 开启编码线程，如开启PEG编码方式线程
//                mSendThread1.start();

            } catch (Exception e)
            {
                Log.v("System.out", e.toString());
            }// endtry
        }// endonPriview
    };
}
