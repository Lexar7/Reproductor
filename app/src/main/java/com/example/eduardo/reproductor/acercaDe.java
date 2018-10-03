package com.example.eduardo.reproductor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class acercaDe extends AppCompatActivity {

    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acerca_de);

        btnBack = (Button)findViewById(R.id.btnBack);
    }

    public void back(View view){
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
    }
}
