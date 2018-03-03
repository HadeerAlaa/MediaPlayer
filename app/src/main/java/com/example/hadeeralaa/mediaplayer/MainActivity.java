package com.example.hadeeralaa.mediaplayer;

import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private long startTime = 0;
    private long finalTime = 0;
    private SeekBar seekbar;
    private Button playPauseSong;
    private TextView songName;
    private MediaMetadataRetriever metaRetriever;
    private TextView songDuration;
    private TextView currentSongPosition;
    public static int oneTimeOnly = 0;
    private android.os.Handler myHandlerPosition = new android.os.Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initiateMediaPlayer();
    }

    private  void initiateMediaPlayer()
    {
        mediaPlayer = MediaPlayer.create(this, R.raw.mwana);
        playPauseSong = (Button) findViewById(R.id.play_stop);
        songName = (TextView) findViewById(R.id.song_name_text_view);
        songDuration = (TextView) findViewById(R.id.song_duration);
        currentSongPosition = (TextView) findViewById(R.id.song_time);
        seekbar = (SeekBar)findViewById(R.id.seekBar);
        seekbar.setClickable(false);

        playPauseSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title ="";
                AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.mwana);
                if (afd != null) {
                    metaRetriever = new MediaMetadataRetriever();
                    metaRetriever.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    title = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                }
                songName.setText(title);
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    playPauseSong.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24dp);
                } else {
                    mediaPlayer.start();
                    playPauseSong.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_24dp);
                }
                finalTime = mediaPlayer.getDuration();
                startTime = mediaPlayer.getCurrentPosition();
                if (oneTimeOnly == 0) {
                    seekbar.setMax((int) finalTime);
                    oneTimeOnly = 1;
                }
                songDuration.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(finalTime),
                        TimeUnit.MILLISECONDS.toSeconds(finalTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime))));
                currentSongPosition.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(startTime),
                        TimeUnit.MILLISECONDS.toSeconds(startTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime))));
                seekbar.setProgress((int)startTime);
                myHandlerPosition.postDelayed(UpdateSongTime,100);
                /*mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Toast.makeText(MainActivity.this,"Finished",Toast.LENGTH_SHORT).show();
                    }
                });*/
            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            int progress;
            @Override
            public  void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                if(mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            currentSongPosition.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(startTime),
                    TimeUnit.MILLISECONDS.toSeconds(startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime)))
            );
            seekbar.setProgress((int)startTime);
            myHandlerPosition.postDelayed(this, 100);
        }
    };
}
