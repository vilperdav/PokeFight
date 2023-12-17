package com.example.pokefight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class activity_Medals extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medals);

        ImageButton tuBoton = findViewById(R.id.ReturnButton);

        tuBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_Medals.this, activity_Main.class);

                // Establece la bandera FLAG_ACTIVITY_CLEAR_TOP para limpiar la pila
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Inicia la actividad
                startActivity(intent);

                // Cierra la actividad actual
                finish();
            }
        });


    }
}