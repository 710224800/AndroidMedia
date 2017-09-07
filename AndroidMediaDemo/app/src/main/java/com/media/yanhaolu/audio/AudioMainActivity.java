package com.media.yanhaolu.audio;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.media.yanhaolu.utils.annotation.ContentView;
import com.media.yanhaolu.utils.annotation.OnClick;
import com.media.yanhaolu.utils.annotation.ViewInject;
import com.media.yanhaolu.utils.annotation.ViewUtil;
import com.media.yanhaolu.androidmediademo.R;

/**
 * Created by yanhaolu on 2017/8/24.
 */

@ContentView(value = R.layout.activity_audio_main)
public class AudioMainActivity extends Activity {
    public static final String TAG = "AudioMainActivity";

    @ViewInject(R.id.record_btn)
    private Button recordBtn;

    @ViewInject(R.id.play_btn)
    private Button playBtn;

    private RecordImpl recordImpl = null;
    private PlayImpl playImpl = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_audio_main);//用了注解的方式，注入布局文件
        ViewUtil.inject(this);
        new Thread(() -> Log.d(TAG,"测试Lambda")).start();//Lambda测试

        recordImpl = new RecordImpl();
        playImpl = new PlayImpl();
    }

    private boolean recording = false;

    @OnClick({R.id.record_btn, R.id.play_btn})
    public void btn (View view){
        switch (view.getId()){
            case R.id.record_btn:
                if(recording){
                    recordImpl.stop();
                    recordImpl.close();
                    recordBtn.setText("开始录音");
                    recording = false;
                } else {
                    new Thread(() -> recordImpl.start()).start();
                    recordBtn.setText("停止录音");
                    recording = true;
                }
                break;
            case R.id.play_btn:
                new Thread(() -> playImpl.play()).start();
                break;
        }
    }
}
