package com.media.yanhaolu.media;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.media.yanhaolu.androidmediademo.R;
import com.media.yanhaolu.utils.annotation.OnClick;
import com.media.yanhaolu.utils.annotation.ViewInject;
import com.media.yanhaolu.utils.annotation.ViewUtil;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * 采集Camera视频源，存到文件里
 */
public class MediaCodecActivity extends AppCompatActivity {
    public static final String TAG = "MediaCodecActivity";
    String encodeType = "video/avc";

    @ViewInject(R.id.start)
    private Button start;

    @ViewInject(R.id.textureView)
    private TextureView textureView;

    @ViewInject(R.id.ratate)
    private Button bt_rotate;

    @ViewInject(R.id.alpha)
    private Button bt_alpha;

    @ViewInject(R.id.surfaceView)
    private SurfaceView surfaceView;

    private Camera mCamera;
    private boolean isPreview = false;
    private SurfaceTexture mSurfaceTexture;

    private MediaCodec mediaCodec;
    private MediaMuxer mediaMuxer;
    private MediaCodec decodCodec;

    private int videoTrackIndex;
    private String filePath = Environment.getExternalStorageDirectory() + "/video_encoded.264";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_codec);
        ViewUtil.inject(this);
        textureView.setSurfaceTextureListener(new MySurfaceTextureListener());
    }

    private int cameraWidth, cameraHeight;
    public void initCamera(){
        if(!isPreview && null != mSurfaceTexture){
            mCamera = Camera.open();
            mCamera.setDisplayOrientation(90);
        }
        Camera.Parameters parameters = mCamera.getParameters();
        Camera.Size mSize = parameters.getSupportedPreviewSizes().get(0);
        cameraWidth = mSize.width;
        cameraHeight = mSize.height;
        parameters.setPreviewSize(mSize.width, mSize.height);
//        parameters.setPreviewFpsRange(4, 10);
//        parameters.setPictureFormat(ImageFormat.JPEG);
//        parameters.setJpegQuality(80);
        parameters.setPictureSize(mSize.width, mSize.height);
        parameters.setPreviewFormat(ImageFormat.YV12);
        try {
            mCamera.setPreviewTexture(mSurfaceTexture);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte[] buffer = new byte[mSize.width*mSize.height*3/2];
        mCamera.addCallbackBuffer(buffer);
        mCamera.setPreviewCallbackWithBuffer(mJpegPreviewCallback);
        mCamera.setParameters(parameters);
    }

//    private BufferedOutputStream outputStream;
    private RandomAccessFile outputFile;
    private void initMediaCodec(){
        try {
            File f = new File(filePath);
            outputFile = new RandomAccessFile(f, "rw");
                //touch (f);
            try {
//                outputStream = new BufferedOutputStream(new FileOutputStream(f, true));
                Log.i("AvcEncoder", "outputStream initialized");
            } catch (Exception e){
                e.printStackTrace();
            }


            mediaCodec = MediaCodec.createEncoderByType(encodeType);
            MediaFormat mediaFormat = MediaFormat.createVideoFormat(encodeType, cameraWidth, cameraHeight);
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 125000);
            mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
            mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5);
            mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        } catch (Exception e){
            Log.e(TAG, e.toString());
        }
    }

    private void initDecodCodec(){
        try {
            mediaCodec = MediaCodec.createDecoderByType(encodeType);
            MediaFormat mediaFormat = MediaFormat.createVideoFormat(encodeType, cameraWidth, cameraHeight);
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 125000);
            mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
            mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5);
            mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        } catch (Exception e){
            Log.e(TAG, e.toString());
        }
    }

    // called from Camera.setPreviewCallbackWithBuffer(...) in other class
    private int mCount = 0;
    public void offerEncoder(byte[] input) {
        try {
            ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
            ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
            int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
            Log.d("asdf", "inputBufferIndex" + inputBufferIndex);
            if (inputBufferIndex >= 0) {
                ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                inputBuffer.clear();
                inputBuffer.put(input);
                Log.i(TAG, "mediaCodec.queueInputBuffer");
                mediaCodec.queueInputBuffer(inputBufferIndex, 0, input.length, 1000000 * mCount / 15, 0);
                mCount++;
            }

            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int outputBufferIndex = -1;
            do {
                outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
                if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    videoTrackIndex = mediaMuxer.addTrack(mediaCodec.getOutputFormat());
                    mediaMuxer.start();
                } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    outputBuffers = mediaCodec.getOutputBuffers();
                } else if (outputBufferIndex >= 0) {

                    ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                    mediaMuxer.writeSampleData(videoTrackIndex, outputBuffer, bufferInfo);

                    mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                }
            } while (outputBufferIndex >= 0);
//            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
//            int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo,0);
//            Log.d("asdf", "outputBufferIndex" + outputBufferIndex);
//            while (outputBufferIndex >= 0) {
//                ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
//                byte[] outData = new byte[bufferInfo.size];
//                outputBuffer.get(outData);
//                long fileLen = outputFile.length();
//                Log.d("asdf", fileLen + "");
//                outputFile.seek(fileLen);
//                outputFile.write(outData);
////                outputStream.write(outData, , outData.length);
//                Log.i(TAG, outData.length + " bytes written");
//
//                mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
//                outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
//
//            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    private final class MySurfaceTextureListener implements TextureView.SurfaceTextureListener {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface,
                                              int width, int height) {
            mSurfaceTexture = surface;
            initCamera();
            initMediaCodec();
            initMediaMuxer();
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

    private void initMediaMuxer() {
        try {
            mediaMuxer = new MediaMuxer(filePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void releaseMediaMuxer() {
        try {
            mediaMuxer.stop();
            mediaMuxer.release();
        } catch (Exception e){
            Log.e(TAG, e.toString());
        }
    }

    private boolean isRotation = false;
    private boolean isAlpha = false;

    @OnClick({R.id.alpha, R.id.ratate, R.id.start})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                if(!isPreview) {
                    mCamera.startPreview();
                    isPreview = true;
                    mediaCodec.start();
                } else {
                    isPreview = false;
                    releaseMediaEncoder();
                    releaseCamera();
                }
                break;

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

    private void releaseMediaEncoder() {
        if (mediaCodec != null) {
            mediaCodec.stop();
            mediaCodec.release();
            mediaCodec = null;
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private void swapYV12toYUV420SemiPlanar(byte[] yv12bytes, byte[] i420bytes, int width, int height) {
        System.arraycopy(yv12bytes, 0, i420bytes, 0, width * height);
        int startPos = width * height;
        int yv_start_pos_v = width * height + width;
        int yv_start_pos_u = width * height + width * height / 4;
        for (int i = 0; i < width * height / 4; i++) {
            i420bytes[startPos + 2 * i + 0] = yv12bytes[yv_start_pos_u + i];
            i420bytes[startPos + 2 * i + 1] = yv12bytes[yv_start_pos_v + i];
        }
    }
    // 【获取视频预览帧的接口】
    Camera.PreviewCallback mJpegPreviewCallback = new Camera.PreviewCallback(){
        @Override
        public void onPreviewFrame(byte[] data, Camera camera)
        {
            camera.addCallbackBuffer(data);
            byte[] mediaInputByte = new byte[cameraWidth * cameraHeight / 2 * 3];
            swapYV12toYUV420SemiPlanar(data, mediaInputByte, cameraWidth, cameraHeight);
            offerEncoder(mediaInputByte);
            //传递进来的data,默认是YUV420SP的
            try
            {
                Log.i(TAG, "going into onPreviewFrame" + data);

            } catch (Exception e)
            {
                Log.v(TAG, e.toString());
            }// endtry
        }// endonPriview
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        close();
        releaseMediaMuxer();
    }

    public void close() {
        try {
            if(mediaCodec != null) {
                mediaCodec.stop();
                mediaCodec.release();
            }
//            if(outputStream != null) {
//                outputStream.flush();
//                outputStream.close();
//            }
            if(outputFile != null) {
                outputFile.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
