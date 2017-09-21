package com.media.yanhaolu.opengl.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.view.View;

import com.media.yanhaolu.utils.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by yanhaolu on 2017/9/18.
 */

public class MyRender_Image implements GLSurfaceView.Renderer {

    private Bitmap mBitmap;

    //顶点坐标
    private final float[] sPos={
            -1.0f,1.0f,
            -1.0f,-1.0f,
            1.0f,1.0f,
            1.0f,-1.0f
    };

    //纹理坐标
    private final float[] sCoord={
            0.0f,0.0f,
            0.0f,1.0f,
            1.0f,0.0f,
            1.0f,1.0f,
    };

    private FloatBuffer bPos;
    private FloatBuffer bCoord;

    //顶点着色器
    private String gl_Position;
    //片元着色器
    private String gl_FragColor;

    private Context context;
    public MyRender_Image(View view, Filter filter){
        this.context = view.getContext();
        this.filter = filter;//变幻图片效果的类，是个枚举
        //顶点着色器
        gl_Position = ShaderUtils.loadFromAssetsFile("filter/half_color_vertex.sh", context.getResources());
        //片元着色器
        gl_FragColor = ShaderUtils.loadFromAssetsFile("filter/half_color_fragment.sh", context.getResources());

        ByteBuffer bPosByte = ByteBuffer.allocateDirect(sPos.length * 4);
        bPosByte.order(ByteOrder.nativeOrder());
        bPos = bPosByte.asFloatBuffer();
        bPos.put(sPos);
        bPos.position(0);

        ByteBuffer bCoordByte = ByteBuffer.allocateDirect(sCoord.length * 4);
        bCoordByte.order(ByteOrder.nativeOrder());
        bCoord = bCoordByte.asFloatBuffer();
        bCoord.put(sCoord);
        bCoord.position(0);
    }

    public void setBitmap(Bitmap bitmap){
        this.mBitmap=bitmap;
    }

    private int mProgram;
    private EGLConfig eglConfig;
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        this.eglConfig = eglConfig;
        GLES20.glClearColor(1.0f,1.0f,1.0f,1.0f);
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        mProgram = ShaderUtils.createProgram(gl_Position, gl_FragColor);
        //获取顶点着色器的vPosition成员句柄
        glHPosition=GLES20.glGetAttribLocation(mProgram,"vPosition");
        //同上。。类似
        glHCoordinate=GLES20.glGetAttribLocation(mProgram,"vCoordinate");
        //获取变换矩阵vMatrix成员句柄
        glHMatrix=GLES20.glGetUniformLocation(mProgram,"vMatrix");
        glHTexture=GLES20.glGetUniformLocation(mProgram,"vTexture");
        hIsHalf=GLES20.glGetUniformLocation(mProgram,"vIsHalf");
        glHUxy=GLES20.glGetUniformLocation(mProgram,"uXY");

        hChangeType=GLES20.glGetUniformLocation(mProgram,"vChangeType");
        hChangeColor=GLES20.glGetUniformLocation(mProgram,"vChangeColor");
    }

    private float[] mViewMatrix=new float[16];
    private float[] mProjectMatrix=new float[16];
    private float[] mMVPMatrix=new float[16];

    private boolean isHalf;
    private float uXY;

    private int width, height;
    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        this.width = width;
        this.height = height;
        GLES20.glViewport(0,0,width,height);

        int w=mBitmap.getWidth();
        int h=mBitmap.getHeight();
        float sWH=w/(float)h;

        float sWidthHeight=width/(float)height;
        uXY=sWidthHeight;

        if(width>height){
            if(sWH>sWidthHeight){
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight*sWH, sWidthHeight*sWH, -1,1, 3, 7);
            }else{
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight/sWH, sWidthHeight/sWH, -1,1, 3, 7);
            }
        }else{
            if(sWH>sWidthHeight){
                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -1/sWidthHeight*sWH, 1/sWidthHeight*sWH,3, 7);
            }else{
                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -sWH/sWidthHeight, sWH/sWidthHeight,3, 7);
            }
        }
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);
    }

    private int glHPosition;
    private int glHTexture;
    private int glHCoordinate;
    private int glHMatrix;
    private int hIsHalf;
    private int glHUxy;

    private int hChangeType;
    private int hChangeColor;

    private Filter filter;
    @Override
    public void onDrawFrame(GL10 gl10) {
        if(refreshFrag&&width!=0&&height!=0){
            onSurfaceCreated(gl10, eglConfig);
            onSurfaceChanged(gl10,width,height);
            refreshFrag=false;
        }
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(mProgram);
        // onDrawSet();  //切换图片效果
        GLES20.glUniform1i(hChangeType,filter.getType());
        GLES20.glUniform3fv(hChangeColor,1, filter.data,0);

        //这两句是控制是否处理一半
        GLES20.glUniform1i(hIsHalf,isHalf?1:0);
        GLES20.glUniform1f(glHUxy,uXY);
        //启用句柄
        GLES20.glEnableVertexAttribArray(glHPosition);

        //同上。。类似
        GLES20.glEnableVertexAttribArray(glHCoordinate);

        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(glHMatrix,1,false,mMVPMatrix,0);

        GLES20.glUniform1i(glHTexture, 0);

        int textureId=createTexture();
        //传入顶点坐标
        GLES20.glVertexAttribPointer(glHPosition,2,GLES20.GL_FLOAT,false,0,bPos);
        //传入纹理坐标
        GLES20.glVertexAttribPointer(glHCoordinate,2,GLES20.GL_FLOAT,false,0,bCoord);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
    }

    private int createTexture(){
        int[] texture=new int[1];
        if(mBitmap!=null&&!mBitmap.isRecycled()){
            //生成纹理
            GLES20.glGenTextures(1,texture,0);
            //生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
            return texture[0];
        }
        return 0;
    }

    public void setFilter(Filter filter){
        this.filter = filter;
    }
    public void sethIsHalf(boolean isHalf){
        this.isHalf = isHalf;
    }
    private boolean refreshFrag = false;
    public void refresh(){
        refreshFrag = true;
    }

    public enum Filter{

        NONE(0,new float[]{0.0f,0.0f,0.0f}),
        GRAY(1,new float[]{0.299f,0.587f,0.114f}),
        COOL(2,new float[]{0.0f,0.0f,0.1f}),
        WARM(2,new float[]{0.1f,0.1f,0.0f}),
        BLUR(3,new float[]{0.006f,0.004f,0.002f}),
        MAGN(4,new float[]{0.0f,0.0f,0.4f});


        private int vChangeType;
        private float[] data;

        Filter(int vChangeType,float[] data){
            this.vChangeType=vChangeType;
            this.data=data;
        }

        public int getType(){
            return vChangeType;
        }

        public float[] data(){
            return data;
        }

    }
}
