package com.example.eduardo.reproductor;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

//import com.obsez.android.lib.filechooser.ChooserDialog;

import org.w3c.dom.Text;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Explorador extends AppCompatActivity {

    ListView listaCanciones;
    List<String> list;
    ListAdapter adapter;

    MediaPlayer mp;


    int posicion = 0;
    Button play_pause, btn_repetir;
    SeekBar positionBar;
    TextView TiempoInicio, TiempoFinal, titulo;
    int totalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorador);

        play_pause = (Button)findViewById(R.id.btnPlay_Pause);
        listaCanciones = findViewById(R.id.lv);
        TiempoInicio = (TextView)findViewById(R.id.txtTiempoInicio);
        TiempoFinal = (TextView)findViewById(R.id.txtTiempoFinal);
        titulo = (TextView)findViewById(R.id.txtTitulo);

        list = new ArrayList<>();

        Field[] fields = R.raw.class.getFields();
        for (int i = 0; i < fields.length; i++){
            list.add(fields[i].getName());
        }


        adapter = new ArrayAdapter<>(this, R.layout.list_view_configuracion, list);
        listaCanciones.setAdapter(adapter);

        listaCanciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(mp != null ){
                    mp.stop();
                    mp.release();
                }

                int resID = getResources().getIdentifier(list.get(i), "raw", getPackageName());
                mp = MediaPlayer.create(Explorador.this, resID);
                mp.start();
                play_pause.setBackgroundResource(R.drawable.pausa);
                //Toast.makeText(getApplicationContext(), "Reproduciendo", Toast.LENGTH_SHORT).show();

                //Poner el nombre de la cancion
                titulo.setText(listaCanciones.getItemAtPosition(i).toString());


                //////////////////////////////////

                mp.seekTo(0);
                totalTime = mp.getDuration();

                //Position Bar
                positionBar = (SeekBar)findViewById(R.id.positionBar);
                positionBar.setMax(totalTime);
                positionBar.setOnSeekBarChangeListener(
                        new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                if(fromUser){
                                    mp.seekTo(progress);
                                    positionBar.setProgress(progress);
                                }
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        });

                //Actualizar positionBar y labels
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(mp != null){
                            try{
                                Message msg = new Message();
                                msg.what = mp.getCurrentPosition();
                                handler.sendMessage(msg);
                                Thread.sleep(1000);
                            } catch (InterruptedException e){
                            }
                        }
                    }
                }).start();
            }
        });

    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int currentPosition = msg.what;
            // Actualizar la posicion de la barra
            positionBar.setProgress(currentPosition);

            //Actualizar labels
            String elapsedTime = createTimeLabel(currentPosition);
            TiempoInicio.setText(elapsedTime);

            String remainingTime = createTimeLabel(totalTime - currentPosition);
            TiempoFinal.setText("- " + remainingTime);
        }
    };

    public String createTimeLabel(int time){
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel= min +":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;
        return timeLabel;
    }

    //Metodo para el boton PlayPause
    public void play_pause(View view){
        if (mp.isPlaying()){
            mp.pause();
            play_pause.setBackgroundResource(R.drawable.reproducir);
            Toast.makeText(this, "Pausa", Toast.LENGTH_SHORT).show();
        }
        else {
            mp.start();
            play_pause.setBackgroundResource(R.drawable.pausa);
            Toast.makeText(this, "Reproduciendo", Toast.LENGTH_SHORT).show();
        }
    }

    //Metodo para boton Siguiente
    public void Siguiente(View view){
        mp.stop();
        mp.release();
        posicion = (posicion + 1)%list.size();
        int u = getResources().getIdentifier(list.get(posicion), "raw", getPackageName());
        mp = MediaPlayer.create(getApplicationContext(), u);
        mp.start();

    }

    //Metodo para boton Anterior
    public void Anterior(View view){
        mp.stop();
        mp.release();
        posicion = (posicion - 1 < 0)? list.size() - 1: posicion-1;
                /*if(position - 1 < 0 ){
                    position = mySongs.size()-1;
                }
                else {
                    position = position - 1;
                }*/
        int u = getResources().getIdentifier(list.get(posicion), "raw", getPackageName());
        mp = MediaPlayer.create(getApplicationContext(), u);
        mp.start();
    }

    //Metodo para buscar archivos
   /* public void openFile(View view){
        new ChooserDialog().with(this)
                .withFilter(false, false, "mp3", "wma", "wav", "jpg")// para agregar mas formatos solo agregar un nuevo elemento despues de "wav" eje: "wav", "mp4" ....
                .withStartFile(Environment.getExternalStorageDirectory().getPath()) // ruta en la que inicia el buscador
                .withChosenListener(new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(String path, File pathFile) {
                        Toast.makeText(Explorador.this, "FILE: " + path, Toast.LENGTH_SHORT).show();
                    }
                })
                .build()
                .show();
    }*/

}
