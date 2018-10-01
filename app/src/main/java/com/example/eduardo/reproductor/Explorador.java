package com.example.eduardo.reproductor;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

//import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Explorador extends AppCompatActivity {

    ListView listaCanciones;
    List<String> list;
    ListAdapter adapter;

    MediaPlayer mp; ;

    int posicion = 0;
    Button play_pause, btn_repetir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorador);

        play_pause = (Button)findViewById(R.id.btnPlay_Pause);
        listaCanciones = findViewById(R.id.lv);

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
            }
        });

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
