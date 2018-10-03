package com.example.eduardo.reproductor;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import com.obsez.android.lib.filechooser.ChooserDialog;

import org.w3c.dom.Text;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Explorador extends AppCompatActivity {

    ListView listaCanciones;
    List<String> list;
    List<String> listPath;
    ListAdapter adapter;

    MediaPlayer mp;

    Uri u;
    int posicion;
    //Shared Preferences
    private final String fileName = "myPlaylist";
    private final String fileNamePath = "myPlaylistPath";
    private SharedPreferences myPlaylist;
    private SharedPreferences.Editor myEditor;
    private SharedPreferences myPlaylistP;
    private SharedPreferences.Editor myEditorP;


    Button play_pause, btn_repetir;
    SeekBar positionBar;
    TextView TiempoInicio, TiempoFinal, titulo;
    int totalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorador);

        myPlaylist = getSharedPreferences(fileName, 0);
        myEditor = myPlaylist.edit();
        myPlaylistP = getSharedPreferences(fileNamePath, 0);
        myEditorP = myPlaylistP.edit();

        play_pause = (Button)findViewById(R.id.btnPlay_Pause);
        listaCanciones = findViewById(R.id.lv);
        TiempoInicio = (TextView)findViewById(R.id.txtTiempoInicio);
        TiempoFinal = (TextView)findViewById(R.id.txtTiempoFinal);
        titulo = (TextView)findViewById(R.id.txtTitulo);

        list = new ArrayList<>();
        listPath = new ArrayList<>();

        Field[] fields = R.raw.class.getFields();
        for (int i = 0; i < fields.length; i++){
            list.add(fields[i].getName());
            listPath.add(fields[i].getName());
        }
        save(list, listPath,1);
        listPath=loadP();
        list = load();
        adapter = new ArrayAdapter<>(this, R.layout.list_view_configuracion, list);
        listaCanciones.setAdapter(adapter);

        registerForContextMenu(listaCanciones);


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
                posicion = i;
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
    ////////////////// MENU CONTEXTUAL //////////////////////////////////////
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);


        final MenuInflater inflater = getMenuInflater();

        if(v.getId() == R.id.lv){

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(listaCanciones.getAdapter().getItem(info.position).toString());

            inflater.inflate(R.menu.menu, menu);
        }
    }
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()){
          //Reproducir
            case R.id.CtxtstOpc1:
                if(mp != null ){
                    mp.stop();
                    mp.release();
                }
                int resID = getResources().getIdentifier(list.get(posicion), "raw", getPackageName());
                mp = MediaPlayer.create(getApplicationContext(), resID);
                mp.start();
                play_pause.setBackgroundResource(R.drawable.pausa);

                return true;
            //Pausar
            case R.id.CtxtstOpc2:
                if(mp != null ){
                    mp.stop();
                    mp.release();
                }
                int id2 = getResources().getIdentifier(list.get(posicion), "raw", getPackageName());
                mp = MediaPlayer.create(getApplicationContext(), id2);
                mp.pause();
                play_pause.setBackgroundResource(R.drawable.reproducir);

                return true;
            //Detener
            case R.id.CtxtstOpc3:
                if(mp != null ){
                    mp.stop();
                    mp.release();
                }
                int id3 = getResources().getIdentifier(list.get(posicion), "raw", getPackageName());
                mp = MediaPlayer.create(getApplicationContext(), id3);
                mp.stop();

                play_pause.setBackgroundResource(R.drawable.reproducir);

                return true;
            default:
                return super.onContextItemSelected(item);
        }

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
        posicion = (posicion + 1)%list.size();;
        int resID = getResources().getIdentifier(list.get(posicion), "raw", getPackageName());
        mp = MediaPlayer.create(getApplicationContext(), resID);
        mp.start();

    }

    //Metodo para boton Anterior
    public void Anterior(View view){
        mp.stop();
        mp.release();
        posicion = (posicion - 1 < 0)? list.size() - 1: posicion-1;
        int resID = getResources().getIdentifier(list.get(posicion), "raw", getPackageName());
        mp = MediaPlayer.create(getApplicationContext(), resID);
        mp.start();
    }

    //Metodo para buscar archivos
    public void openFile(View view){
        new ChooserDialog().with(this)
                .withFilter(false, false, "mp3", "wma", "wav", "jpg")// para agregar mas formatos solo agregar un nuevo elemento despues de "wav" eje: "wav", "mp4" ....
                .withStartFile(Environment.getExternalStorageDirectory().getPath()) // ruta en la que inicia el buscador
                .withChosenListener(new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(String path, File pathFile) {
                        list.add(pathFile.getName().replace(".mp3",""));
                        listPath.add(path);
                        save(list, listPath,0);
                        list = load();
                        listPath = loadP();
                        //Toast.makeText(Explorador.this, "FILE: " + list.get(list.size()-1), Toast.LENGTH_SHORT).show();
                    }
                })
                .build()
                .show();
    }

    //Carga los elementos guardados en preferencias
    public List<String> load() {
        // Create new array to be returned
        List<String> savedSongs = new ArrayList<>();

        // Get the number of saved songs
        int numOfSavedSongs = myPlaylist.getInt("listSize", 0);

        // Get saved songs by their index
        for (int i = 0; i < numOfSavedSongs; i++) {
            savedSongs.add(myPlaylist.getString(String.valueOf(i), ""));
        }

        return savedSongs;
    }

    public List<String> loadP() {
        // Create new array to be returned
        List<String> savedSongs = new ArrayList<>();

        // Get the number of saved songs
        int numOfSavedSongs = myPlaylistP.getInt("listSize", 0);

        // Get saved songs by their index
        for (int i = 0; i < numOfSavedSongs; i++) {
            savedSongs.add(myPlaylistP.getString(String.valueOf(i), ""));
        }

        return savedSongs;
    }

    //Guarda en Preferencias de la Aplicacion
    public void save(List<String> mySongs, List<String> mySongsP, int primerCarga){
        // Save the size so that you can retrieve the whole list later
        if(primerCarga==1 && myPlaylist.getInt("listSize", 0)<=3 ){
            myEditor.putInt("listSize", mySongs.size());
            myEditorP.putInt("listSize", mySongs.size());

            for(int i = 0; i < mySongs.size(); i++){
                // Save each song with its index in the list as a key
                myEditor.putString(String.valueOf(i), mySongs.get(i));
                myEditorP.putString(String.valueOf(i), mySongs.get(i));
            }
        }else if(primerCarga==0){
            myEditor.putInt("listSize", mySongs.size());
            myEditorP.putInt("listSize", mySongs.size());

            for(int i = 0; i < mySongs.size(); i++){
                // Save each song with its index in the list as a key
                myEditor.putString(String.valueOf(i), mySongs.get(i));
                myEditorP.putString(String.valueOf(i), mySongs.get(i));
            }
        }

        myEditor.commit();
        myEditorP.commit();
    }

    }


