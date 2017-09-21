package com.media.yanhaolu.opengl.image;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.media.yanhaolu.androidmediademo.R;

/**
 * Created by yanhaolu on 2017/9/18.
 */

public class OpenGLES_ImageActivity extends AppCompatActivity{

    private boolean isHalf=false;
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.mDeal:
                isHalf=!isHalf;
                if(isHalf){
                    item.setTitle("处理一半");
                }else{
                    item.setTitle("全部处理");
                }
                myGLSurfaceView.getRender().refresh();
                break;
            case R.id.mDefault:
                myGLSurfaceView.setFilter(MyRender_Image.Filter.NONE);
                break;
            case R.id.mGray:
                myGLSurfaceView.setFilter(MyRender_Image.Filter.GRAY);
                break;
            case R.id.mCool:
                myGLSurfaceView.setFilter(MyRender_Image.Filter.COOL);
                break;
            case R.id.mWarm:
                myGLSurfaceView.setFilter(MyRender_Image.Filter.WARM);
                break;
            case R.id.mBlur:
                myGLSurfaceView.setFilter(MyRender_Image.Filter.BLUR);
                break;
            case R.id.mMagn:
                myGLSurfaceView.setFilter(MyRender_Image.Filter.MAGN);
                break;

        }
        myGLSurfaceView.getRender().sethIsHalf(isHalf);
        myGLSurfaceView.requestRender();
        return super.onOptionsItemSelected(item);
    }
}
