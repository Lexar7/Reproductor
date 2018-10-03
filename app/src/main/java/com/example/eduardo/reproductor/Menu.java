package com.example.eduardo.reproductor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Menu extends AppCompatActivity {

    Button btnSongs, btnAcerde;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnSongs = (Button)findViewById(R.id.btnCanciones);
        btnAcerde = (Button)findViewById(R.id.btnAbout);
    }

    public void songs(View view){
        Intent intent = new Intent(this, Explorador.class);
        startActivity(intent);
    }

    public void acercaDe(View view){
        Intent intent = new Intent(this, acercaDe.class);
        startActivity(intent);
    }

}
