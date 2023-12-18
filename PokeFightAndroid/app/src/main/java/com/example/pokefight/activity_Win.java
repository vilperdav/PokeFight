package com.example.pokefight;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class activity_Win extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win);

        // Boton para volver a la pestana anterior, osea al menu
        Button returnButton = findViewById(R.id.mainMenu);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_Win.this, activity_Main.class);

                // Establece la bandera FLAG_ACTIVITY_CLEAR_TOP para limpiar la pila
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Inicia la actividad
                startActivity(intent);

                // Cierra la actividad actual
                finish();
            }
        });

        // Boton para ir a ver las medallas
        Button btnGoToCheck = findViewById(R.id.goToCheckButton);
        btnGoToCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_Win.this, activity_Medals.class);

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