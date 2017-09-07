package com.media.yanhaolu.androidmediademo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.media.yanhaolu.camera.CameraMainActivity;
import com.media.yanhaolu.image.Drag;
import com.media.yanhaolu.audio.AudioMainActivity;
import com.media.yanhaolu.media.MediaCodecActivity;
import com.media.yanhaolu.media.MediaExactorMediaMuxerActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
    }

    /**
     * 跳转SurfaceView绘制图片
     */
    public void gotoDrag(View view){
        startActivity(new Intent(this, Drag.class));
    }

    /**
     * 跳转AudioRecord   AudioTrack测试
     */
    public void gotoAudio(View view){
        startActivity(new Intent(this, AudioMainActivity.class));
    }

    /**
     * 跳转相机预览
     */
    public void gotoCamera(View view){
        startActivity(new Intent(this, CameraMainActivity.class));
    }

    public void gotoMediaExactor_Muxer(View view){
        startActivity(new Intent(this, MediaExactorMediaMuxerActivity.class));
    }

    public void gotoMediaCodec(View view){
        startActivity(new Intent(this, MediaCodecActivity.class));
    }










    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
}
