package com.example.reproductor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    TextView tvTitle, tvAthor;

    SeekBar volumeBar;

    Button btnPlay;

    ImageButton btnBack, btnNext, btnFb, btnTw, btnShare, btnLink;

    ImageView logo, imgBocinaA, imgBocinaB;

    MediaPlayer mediaPlayer;

    String stream1 = "http://stream.radioreklama.bg:80/radio1rock128";
    String stream = "http://162.251.160.57:8028/";

    boolean prepared = false;
    boolean started = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setLayout();
        btnPlay.setEnabled(false);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                Log.i("Buffering",""+percent);
            }
        });
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        initializeVolume();

        try {
            mediaPlayer.setDataSource(stream1);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    prepared = true;
                    started = true;
                    btnPlay.setEnabled(true);
                    btnPlay.setText("PAUSE");
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException | SecurityException e) {
            e.printStackTrace();
        }

        //new PlayerTask().execute(stream);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(started){
                    started = false;
                    mediaPlayer.pause();
                    btnPlay.setText("PLAY");
                }
                else{
                    started = true;
                    mediaPlayer.start();
                    btnPlay.setText("PAUSE");
                }
            }
        });

    }

    private void setLayout() {

        btnPlay = findViewById(R.id.btnPlay);
        btnFb = findViewById(R.id.imageButton4);
        btnTw = findViewById(R.id.imageButton5);
        btnShare = findViewById(R.id.imageButton6);
        btnLink = findViewById(R.id.imageButton7);

        logo = findViewById(R.id.imageView);
        imgBocinaA = findViewById(R.id.imageView2);
        imgBocinaB = findViewById(R.id.imageView3);

        btnPlay.setText("LOADING");
    }

    private void initializeVolume(){
        try{
            final SeekBar volumeBar = (SeekBar) findViewById(R.id.seekBar);
            final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            volumeBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

            final SeekBar.OnSeekBarChangeListener eventListener = new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            };
            volumeBar.setOnSeekBarChangeListener(eventListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class PlayerTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {

            try {
                mediaPlayer.setDataSource(strings[0]);
                mediaPlayer.prepare();
                prepared = true;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            btnPlay.setEnabled(true);
            btnPlay.setText("PLAY");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(started){
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(started){
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(prepared){
            mediaPlayer.release();
        }
    }
}
