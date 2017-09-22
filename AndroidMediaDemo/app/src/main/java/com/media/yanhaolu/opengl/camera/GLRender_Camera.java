package com.media.yanhaolu.opengl.camera;

import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.View;

import com.media.yanhaolu.utils.Gl2Utils;
import com.media.yanhaolu.utils.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by yanhaolu on 2017/9/20.
 */

public class GLRender_Camera implements GLSurfaceView.Renderer{

    private Context context;

    private String gl_FragColor;
    private String gl_Position;

    private KitkatCamera camera;
    private int cameraId=1; //0是后置
    private int mProgram;

    private Runnable mRunnable;

    //顶点坐标
    private float pos[] = {
            -1.0f,  1.0f,
            -1.0f, -1.0f,
            1.0f, 1.0f,
            1.0f,  -1.0f,
    };

    //纹理坐标
    private float[] coord={
            0.0f, 0.0f,
            0.0f,  1.0f,
            1.0f,  0.0f,
            1.0f, 1.0f,
    };

    private float[] mCoordMatrix= new float[]{
            1,0,0,0,
            0,1,0,0,
            0,0,1,0,
            0,0,0,1
    };

    /**
     * 顶点坐标Buffer
     */
    protected FloatBuffer mVerBuffer;

    /**
     * 纹理坐标Buffer
     */
    protected FloatBuffer mTexBuffer;

    public GLRender_Camera(View view){
        this.context = view.getContext();
        //顶点着色器
        gl_Position = ShaderUtils.loadFromAssetsFile("shader/oes_base_vertex.sh", context.getResources());
        //片元着色器
        gl_FragColor = ShaderUtils.loadFromAssetsFile("shader/oes_base_fragment.sh", context.getResources());

        ByteBuffer a=ByteBuffer.allocateDirect(32);
        a.order(ByteOrder.nativeOrder());
        mVerBuffer=a.asFloatBuffer();
        mVerBuffer.put(pos);
        mVerBuffer.position(0);
        ByteBuffer b=ByteBuffer.allocateDirect(32);
        b.order(ByteOrder.nativeOrder());
        mTexBuffer=b.asFloatBuffer();
        mTexBuffer.put(coord);
        mTexBuffer.position(0);

        camera = new KitkatCamera();
    }

    /**
     * 顶点坐标句柄
     */
    protected int mHPosition;
    /**
     * 纹理坐标句柄
     */
    protected int mHCoord;
    /**
     * 总变换矩阵句柄
     */
    protected int mHMatrix;
    /**
     * 默认纹理贴图句柄
     */
    protected int mHTexture;

    private int mHCoordMatrix;

    private SurfaceTexture surfaceTexture;
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
//        if(mRunnable!=null){
//            mRunnable.run();
//            mRunnable=null;
//        }
//        GLES20.glClearColor(1.0f,1.0f,1.0f,1.0f);
//        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        textureId = createTextureID();
        surfaceTexture = new SurfaceTexture(textureId);
        surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                if(onFrameAvailableListener != null){
                    onFrameAvailableListener.onFrameAvailable(surfaceTexture);
                }
            }
        });

        mProgram = ShaderUtils.createProgram(gl_Position, gl_FragColor);
        mHPosition= GLES20.glGetAttribLocation(mProgram, "vPosition");
        mHCoord=GLES20.glGetAttribLocation(mProgram,"vCoord");
        mHMatrix=GLES20.glGetUniformLocation(mProgram,"vMatrix");
        mHTexture=GLES20.glGetUniformLocation(mProgram,"vTexture");
        mHCoordMatrix=GLES20.glGetUniformLocation(mProgram,"vCoordMatrix");

        camera.open(cameraId);
        Camera.Size preSize = camera.getPreviewSize();
        imgWidth = preSize.height;
        imgHeight = preSize.width;
        camera.setPreviewTexture(surfaceTexture);
        camera.preview();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        viewWidth = width;
        viewHeight = height;
        calculateMatrix();
        GLES20.glViewport(0,0,width,height);
    }

    private int textureType=0;      //默认使用Texture2D0
    private int textureId;
    @Override
    public void onDrawFrame(GL10 gl10) {
        if(surfaceTexture!=null){
            surfaceTexture.updateTexImage();
        }
        /** 清除画布 **/
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        /** userProgram **/
        GLES20.glUseProgram(mProgram);
        /** 设置扩展数据 **/
        GLES20.glUniformMatrix4fv(mHMatrix,1,false,matrix,0);
        GLES20.glUniformMatrix4fv(mHCoordMatrix,1,false,mCoordMatrix,0);
        /** bindTexture **/
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0+textureType);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(mHTexture,textureType);
        /** onDraw **/
        GLES20.glEnableVertexAttribArray(mHPosition);
        GLES20.glVertexAttribPointer(mHPosition,2, GLES20.GL_FLOAT, false, 0,mVerBuffer);
        GLES20.glEnableVertexAttribArray(mHCoord);
        GLES20.glVertexAttribPointer(mHCoord, 2, GLES20.GL_FLOAT, false, 0, mTexBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
        GLES20.glDisableVertexAttribArray(mHPosition);
        GLES20.glDisableVertexAttribArray(mHCoord);

    }

    private int createTextureID(){
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }

    private int imgWidth, imgHeight;
    private int viewWidth, viewHeight;
    private float[] matrix=new float[16];
    private void calculateMatrix(){
        Gl2Utils.getShowMatrix(matrix,this.imgWidth,this.imgHeight,this.viewWidth,this.viewHeight);
        if(cameraId==1){
            Gl2Utils.flip(matrix,true,false);
            Gl2Utils.rotate(matrix,90);
        }else{
            Gl2Utils.rotate(matrix,270);
        }
    }

    public SurfaceTexture getSurfaceTexture(){
        return surfaceTexture;
    }

    public void setCameraId(int cameraId){
        this.cameraId = cameraId;
    }

    public void closeCamera(){
        camera.close();
    }

    public void runSwitch(){
        mRunnable.run();
    }

    public void switchCamera(){
        mRunnable=new Runnable() {
            @Override
            public void run() {
                camera.close();
                cameraId=cameraId==1?0:1;
            }
        };
    }

    OnFrameAvailableListener onFrameAvailableListener;

    public OnFrameAvailableListener getOnFrameAvailableListener() {
        return onFrameAvailableListener;
    }

    public void setOnFrameAvailableListener(OnFrameAvailableListener onFrameAvailableListener) {
        this.onFrameAvailableListener = onFrameAvailableListener;
    }

    public interface OnFrameAvailableListener {
        public void onFrameAvailable(SurfaceTexture surfaceTexture);
    }
}
