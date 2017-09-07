package com.media.yanhaolu.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by yanhaolu on 2017/8/25.
 */

public class PlayImpl {
    protected static int FREQUENCY = 16000;
    private static int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private static int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private final String FILEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/demo.wav";
    private  AudioTrack audioTrack;

    public void play() {
        File file = new File(FILEPATH);
        int musicLength = (int) (file.length() / 2);
        short[] music = new short[musicLength];

        try {
            InputStream is = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            DataInputStream dis = new DataInputStream(bis);

            int i = 0;
            while (dis.available() > 0) {
                music[i] = dis.readShort();
                i++;
            }

            dis.close();

            open(musicLength);
            audioTrack.play();
            audioTrack.write(music, 0, musicLength);
            audioTrack.stop();

        } catch (Exception e) {
            Log.e("AudioTrack", "Playback Failed" + e.toString());
        }
    }

    public void open(int musicLength){
        int bufferSize = AudioTrack.getMinBufferSize(FREQUENCY, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                FREQUENCY,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,//musicLength * 2,
                AudioTrack.MODE_STREAM);
    }

    public void stop() {
        audioTrack.stop();
    }
}
