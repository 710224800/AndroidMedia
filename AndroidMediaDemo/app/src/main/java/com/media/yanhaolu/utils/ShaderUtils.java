/*
 *
 * ShaderUtils.java
 * 
 * Created by Wuwang on 2016/10/8
 */
package com.media.yanhaolu.utils;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

import java.io.InputStream;

/**
 * Description:
 */
public class ShaderUtils {

    private static final String TAG="ShaderUtils";

    private ShaderUtils(){
    }

    public static void checkGLError(String op){
        Log.e("wuwang",op);
    }

    public static int loadShader(int shaderType,String source){
        //1.创建一个着色器, 并记录所创建的着色器的id, 如果id==0, 那么创建失败
        int shader= GLES20.glCreateShader(shaderType);
        if(0!=shader){
            //2.如果着色器创建成功, 为创建的着色器加载脚本代码
            GLES20.glShaderSource(shader,source);
            //3.编译已经加载脚本代码的着色器
            GLES20.glCompileShader(shader);
            int[] compiled=new int[1];
            //4.获取着色器的编译情况, 如果结果为0, 说明编译失败
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS,compiled,0);
            if(compiled[0]==0){
                Log.e(TAG,"Could not compile shader:"+shaderType);
                Log.e(TAG,"GLES20 Error:"+ GLES20.glGetShaderInfoLog(shader));
                //编译失败的话, 删除着色器, 并显示log
                GLES20.glDeleteShader(shader);
                shader=0;
            }
        }
        return shader;
    }

    public static int loadShader(Resources res, int shaderType, String resName){
        return loadShader(shaderType,loadFromAssetsFile(resName,res));
    }

    public static int createProgram(String vertexSource, String fragmentSource){
        //1. 加载顶点着色器, 返回0说明加载失败
        int vertex=loadShader(GLES20.GL_VERTEX_SHADER,vertexSource);
        if(vertex==0)return 0;
        //2. 加载片元着色器, 返回0说明加载失败
        int fragment=loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentSource);
        if(fragment==0)return 0;
        //3. 创建着色程序, 返回0说明创建失败
        int program= GLES20.glCreateProgram();
        if(program!=0){
            //4. 向着色程序中加入顶点着色器
            GLES20.glAttachShader(program,vertex);
            //检查glAttachShader操作有没有失败
            checkGLError("Attach Vertex Shader");
            //5. 向着色程序中加入片元着色器
            GLES20.glAttachShader(program,fragment);
            //检查glAttachShader操作有没有失败
            checkGLError("Attach Fragment Shader");
            //6. 链接程序
            GLES20.glLinkProgram(program);
            int[] linkStatus=new int[1];
            //获取链接程序结果
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS,linkStatus,0);
            if(linkStatus[0]!= GLES20.GL_TRUE){
                Log.e(TAG,"Could not link program:"+ GLES20.glGetProgramInfoLog(program));
                //如果链接程序失败删除程序
                GLES20.glDeleteProgram(program);
                program=0;
            }
        }
        return program;
    }

    public static int createProgram(Resources res, String vertexRes, String fragmentRes){
        return createProgram(loadFromAssetsFile(vertexRes,res),loadFromAssetsFile(fragmentRes,res));
    }

    public static String loadFromAssetsFile(String fname, Resources res){
        StringBuilder result=new StringBuilder();
        try{
            InputStream is=res.getAssets().open(fname);
            int ch;
            byte[] buffer=new byte[1024];
            while (-1!=(ch=is.read(buffer))){
                result.append(new String(buffer,0,ch));
            }
        }catch (Exception e){
            return null;
        }
        return result.toString().replaceAll("\\r\\n","\n");
    }

}
