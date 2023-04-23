package com.example.mysound;


import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    SoundPool soundPool;
    boolean loop;
    float speed = 1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        this.soundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .setMaxStreams(6)
                .build();

        loadSounds(this.soundPool);

        ImageButton playButton1 = findViewById(R.id.imageButton);
        ImageButton playButton2 = findViewById(R.id.imageButton2);
        ImageButton playButton3 = findViewById(R.id.imageButton3);
        ImageButton playButton4 = findViewById(R.id.imageButton4);
        ImageButton playButton5 = findViewById(R.id.imageButton5);
        ImageButton playButton6 = findViewById(R.id.imageButton6);

        Switch loopSwitch = findViewById(R.id.switch1);

        SeekBar seekBar = findViewById(R.id.seekBar2);

        playButton1.setOnClickListener(view -> playSound(1));
        playButton2.setOnClickListener(view -> playSound(2));
        playButton3.setOnClickListener(view -> playSound(3));
        playButton4.setOnClickListener(view -> playSound(4));
        playButton5.setOnClickListener(view -> playSound(5));
        playButton6.setOnClickListener(view -> playSound(6));

        loopSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            loop = isChecked;
            playSound(1);
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                speed = seekBar.getProgress() / 100f;
                Log.e("seekbar", String.valueOf(speed));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void playSound(int id) {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        float curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float leftVolume = curVolume / maxVolume;
        float rightVolume = curVolume / maxVolume;

        soundPool.play(id, leftVolume, rightVolume, 1, loop?10:0, speed);
    }

    private void loadSounds(SoundPool soundPool) {
        soundPool.load(this, R.raw.a, 1);
        soundPool.load(this, R.raw.b, 1);
        soundPool.load(this, R.raw.c, 1);
        soundPool.load(this, R.raw.d, 1);
        soundPool.load(this, R.raw.e, 1);
        soundPool.load(this, R.raw.f, 1);
    }
}