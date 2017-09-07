package com.media.yanhaolu.camera;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.media.yanhaolu.androidmediademo.R;
import com.media.yanhaolu.utils.annotation.OnClick;
import com.media.yanhaolu.utils.annotation.ViewInject;
import com.media.yanhaolu.utils.annotation.ViewUtil;

public class CameraMainActivity extends AppCompatActivity {

    @ViewInject(R.id.camera_surface_view)
    private Button camera_surface_view;

    @ViewInject(R.id.camera_texture_view)
    private Button camera_texture_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_main);
        ViewUtil.inject(this);
    }

    @OnClick({R.id.camera_surface_view, R.id.camera_texture_view})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.camera_surface_view:
                startActivity(new Intent(this, Camera1SurfaceViewActivity.class));
                break;

            case R.id.camera_texture_view:
                startActivity(new Intent(this, Camera1TextureViewActivity.class));
                break;

        }
    }
}
