package com.example.eduardo.reproductor;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class splashActivity extends AppCompatActivity {
    private Boolean botonBackPresionado=false;
    private static  final int DURACION_SPLASF=5000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler manejador = new Handler();
        manejador.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                if (!botonBackPresionado){
                    Intent intento = new Intent(splashActivity.this, Menu.class);
                    startActivity(intento);
                }
            }

        }, DURACION_SPLASF);
    }

    public void OnBackPressed(){
        botonBackPresionado=true;
        super.onBackPressed();
    }
}
