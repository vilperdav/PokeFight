package com.example.pokefight;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class activity_Info extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        ImageButton returnButton = findViewById(R.id.ReturnButton);

        // Boton para volver a la pestana anterior
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Boton para ver el readme del repositorio
        Button btnHowToPlay = findViewById(R.id.howToPlayButton);
        btnHowToPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://github.com/vilperdav/PokeFight/blob/main/README.md";
                openBrowser(url);
            }
        });

        // Boton para ver las versiones del juego
        Button btnGitHubLink = findViewById(R.id.gitHubLink);
        btnGitHubLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://github.com/vilperdav/PokeFight/releases";
                openBrowser(url);
            }
        });

        // TODO - Faltan añadir eventos para el Switch Shiny y para el RESET DEL JUEGO

    }

    private void openBrowser(String url) {

        // Crear un Intent con la acción ACTION_VIEW y la URL como datos
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        // Verificar si hay aplicaciones que pueden manejar el Intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Iniciar la actividad con el Intent
            startActivity(intent);
        }
    }
}