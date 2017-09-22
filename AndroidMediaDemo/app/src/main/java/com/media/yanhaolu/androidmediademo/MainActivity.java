package com.media.yanhaolu.androidmediademo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.media.yanhaolu.camera.CameraMainActivity;
import com.media.yanhaolu.image.Drag;
import com.media.yanhaolu.audio.AudioMainActivity;
import com.media.yanhaolu.media.MediaCodecActivity;
import com.media.yanhaolu.media.MediaExactorMediaMuxerActivity;
import com.media.yanhaolu.opengl.camera.OpenGL_Camera_Activiry;
import com.media.yanhaolu.opengl.camra2mp4.OpenGL_Camera2mp4_Activiry;
import com.media.yanhaolu.opengl.image.OpenGLES_ImageActivity;
import com.media.yanhaolu.opengl.opengles20.OpenGLES20Activity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private RecyclerView mList;
    private ArrayList<MenuBean> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mList= (RecyclerView)findViewById(R.id.mList);
        mList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        data=new ArrayList<>();
        add("surfaceView",Drag.class);
        add("录音与播放",AudioMainActivity.class);
        add("相机预览",CameraMainActivity.class);
        add("MediaExactor_Muxer",MediaExactorMediaMuxerActivity.class);
        add("MediaCodec采集Camera视频到文件",MediaCodecActivity.class);
        add("OpenGLES20Activity",OpenGLES20Activity.class);
        add("Opengl显示图片", OpenGLES_ImageActivity.class);
        add("Opengl预览Camera", OpenGL_Camera_Activiry.class);
        add("Opengl预览Camera生成mp4", OpenGL_Camera2mp4_Activiry.class);
        mList.setAdapter(new MenuAdapter());
    // Example of a call to a native method
//        TextView tv = (TextView) findViewById(R.id.sample_text);
//        tv.setText(stringFromJNI());
    }


    private void add(String name,Class<?> clazz){
        MenuBean bean=new MenuBean();
        bean.name=name;
        bean.clazz=clazz;
        data.add(bean);
    }

    private class MenuBean{

        String name;
        Class<?> clazz;

    }

    private class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuHolder>{


        @Override
        public MenuHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MenuHolder(getLayoutInflater().inflate(R.layout.item_button,parent,false));
        }

        @Override
        public void onBindViewHolder(MenuHolder holder, int position) {
            holder.setPosition(position);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class MenuHolder extends RecyclerView.ViewHolder{

            private Button mBtn;

            MenuHolder(View itemView) {
                super(itemView);
                mBtn= (Button)itemView.findViewById(R.id.mBtn);
                mBtn.setOnClickListener(MainActivity.this);
            }

            public void setPosition(int position){
                MenuBean bean=data.get(position);
                mBtn.setText(bean.name);
                mBtn.setTag(position);
            }
        }

    }

    @Override
    public void onClick(View view){
        int position= (int)view.getTag();
        MenuBean bean=data.get(position);
        startActivity(new Intent(this,bean.clazz));
    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
//    public native String stringFromJNI();
//
//    // Used to load the 'native-lib' library on application startup.
//    static {
//        System.loadLibrary("native-lib");
//    }


    static {
        System.loadLibrary("VideoConvert");
    }
}
