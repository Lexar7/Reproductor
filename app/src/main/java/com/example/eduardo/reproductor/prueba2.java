package com.example.eduardo.reproductor;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import java.io.File;
import java.util.ArrayList;

public class prueba2 extends AppCompatActivity {

    static MediaPlayer mp;
    ArrayList<File> mySongs;
    int position;
    Uri u;

    Thread updateSeek;
    SeekBar sb;
    Button btnPlay, btnNext, btnPrevios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba2);

        btnPlay = (Button)findViewById(R.id.btnPlay);
        btnNext = (Button)findViewById(R.id.btnNext);
        btnPrevios = (Button)findViewById(R.id.btnPrevios);

        sb = (SeekBar) findViewById(R.id.seekBar);
        updateSeek = new Thread(){
            @Override
            public void run() {
                int totalDuration = mp.getDuration();
                int currentPosition = 0;
                while (currentPosition < totalDuration){
                    try{
                        sleep(500);
                        currentPosition = mp.getCurrentPosition();
                        sb.setProgress(currentPosition);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                //super.run();
            }
        };

        if (mp!= null){
            mp.stop();
            mp.release();
        }

        //recibir datos
        Bundle extras = getIntent().getExtras();
        int recibir = Integer.parseInt(extras.getString("data"));
        mp = MediaPlayer.create(getApplicationContext(), recibir);
        mp.start();


        /*Bundle b = i.getExtras();
        mySongs = (ArrayList) b.getParcelableArrayList("songlist");
        int position = b.getInt("posicion", 0);

        u = Uri.parse(mySongs.get(position).toString());
        mp = MediaPlayer.create(getApplicationContext(), u);
        mp.start();*/


        sb.setMax(mp.getDuration());

        updateSeek.start();

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(seekBar.getProgress());
            }
        });

    }

    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btnPlay:
                if(mp.isPlaying()){
                    mp.pause();
                }
                else mp.start();
                break;
            case R.id.btnNext:
                mp.stop();
                mp.release();
                position = (position + 1)%mySongs.size();
                u = Uri.parse(mySongs.get(position).toString());
                mp = MediaPlayer.create(getApplicationContext(), u);
                mp.start();
                sb.setMax(mp.getDuration());
                break;
            case R.id.btnPrevios:
                mp.stop();
                mp.release();
                position = (position - 1 < 0)? mySongs.size() - 1: position-1;
                /*if(position - 1 < 0 ){
                    position = mySongs.size()-1;
                }
                else {
                    position = position - 1;
                }*/
                u = Uri.parse(mySongs.get(position).toString());
                mp = MediaPlayer.create(getApplicationContext(), u);
                mp.start();
                sb.setMax(mp.getDuration());
                break;
        }
    }
}
