package com.media.yanhaolu.ffmpeg;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.media.yanhaolu.androidmediademo.R;
import com.media.yanhaolu.androidmediademo.databinding.MainBinding;

public class FFmpegActivity extends AppCompatActivity implements View.OnClickListener{

    private MainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ffmpeg);
        binding.btnProtocol.setOnClickListener(this);
        binding.btnCodec.setOnClickListener(this);
        binding.btnFilter.setOnClickListener(this);
        binding.btnFormat.setOnClickListener(this);
    }

    static {
        System.loadLibrary("ffmpeg_lib");
    }

    public native String stringFromJNI();

    public native String urlprotocolinfo();
    public native String avformatinfo();
    public native String avcodecinfo();
    public native String avfilterinfo();

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_protocol:
                binding.tvInfo.setText("result:\n" + urlprotocolinfo());
                break;
            case R.id.btn_format:
                binding.tvInfo.setText("result:\n" + avformatinfo());
                break;
            case R.id.btn_codec:
                binding.tvInfo.setText("result:\n" + avcodecinfo());
                break;
            case R.id.btn_filter:
                binding.tvInfo.setText("result:\n" + avfilterinfo());
                break;
            default:
                break;
        }
    }
}
