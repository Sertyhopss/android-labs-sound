package com.example.mediarecorder;

import android.os.SystemClock;
import android.widget.Chronometer;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String fileName;
    private boolean isRecording = false;
    private Button recordButton, stopButton, playButton, stopPlayButton;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private Chronometer simpleChronometer;
    private final String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recordButton = findViewById(R.id.record_button);
        stopButton = findViewById(R.id.stop_button);
        playButton = findViewById(R.id.play_button);
        stopPlayButton = findViewById(R.id.stop_play_button);
        simpleChronometer = findViewById(R.id.simpleChronometer);
        stopButton.setEnabled(false);
        playButton.setEnabled(false);
        stopPlayButton.setEnabled(false);
        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/audiotest.3gp";

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            permissionToRecordAccepted = true;
        }
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    if (checkMicrophoneAvailability()) {
                        startRecording();
                    } else {
                        Toast.makeText(MainActivity.this, "Access denied", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Recording already in progress", Toast.LENGTH_SHORT).show();
                }
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
            }
        });
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playRecording();
            }
        });
        stopPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlayback();
            }
        });
    }

    private boolean checkMicrophoneAvailability() {
        PackageManager packageManager = getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    private void startRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(fileName);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        simpleChronometer.setBase(SystemClock.elapsedRealtime());
        simpleChronometer.start();

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            Log.e("MediaRecorder", "prepare() failed");
        }
        mediaRecorder.start();
        isRecording = true;
        recordButton.setEnabled(false);
        stopButton.setEnabled(true);
        playButton.setEnabled(false);
        stopPlayButton.setEnabled(false);
    }

    private void stopRecording() {
        if (isRecording) {
            simpleChronometer.stop();
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
            recordButton.setEnabled(true);
            stopButton.setEnabled(false);
            playButton.setEnabled(true);
            stopPlayButton.setEnabled(false);
        }
    }

    private void playRecording() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(fileName);
            mediaPlayer.prepare();
            mediaPlayer.start();
            recordButton.setEnabled(false);
            stopButton.setEnabled(false);
            playButton.setEnabled(false);
            stopPlayButton.setEnabled(true);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    recordButton.setEnabled(true);
                    stopButton.setEnabled(false);
                    playButton.setEnabled(true);
                    stopPlayButton.setEnabled(false);
                }
            });
        } catch (IOException e) {
            Log.e("MediaRecorder", "prepare() failed");
        }
    }

    private void stopPlayback() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            recordButton.setEnabled(true);
            stopButton.setEnabled(false);
            playButton.setEnabled(true);
            stopPlayButton.setEnabled(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
        if (!permissionToRecordAccepted) finish();
    }
}
