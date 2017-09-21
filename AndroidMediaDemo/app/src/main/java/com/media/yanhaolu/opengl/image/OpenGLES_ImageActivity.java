package com.media.yanhaolu.opengl.image;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by yanhaolu on 2017/9/18.
 */

public class OpenGLES_ImageActivity extends AppCompatActivity{

    private MyGLSrufaceView_Image myGLSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myGLSurfaceView = new MyGLSrufaceView_Image(this);
        setContentView(myGLSurfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        myGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myGLSurfaceView.onPause();
    }
}
