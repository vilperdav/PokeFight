package com.example.pokefight;

import static android.widget.Toast.LENGTH_SHORT;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class activity_Info extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        // Variable compartida para los switches
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Switch shinyModeSwitch = findViewById(R.id.shinyModeSwitch);
        Switch IASwitch = findViewById(R.id.IASwitch);
        // Intenta obtener el estado actual del los switches sino los deja como desabilidatos.
        boolean shinyState = preferences.getBoolean("shinySwitch_state", false);
        boolean IAState = preferences.getBoolean("IASwitch_state", false);

        // Cambia a 'true' si los habiamos dejado activado previamente
        if (shinyState) {
            shinyModeSwitch.setChecked(true);
        }

        if (IAState) {
            IASwitch.setChecked(true);
        }


        ImageButton returnButton = findViewById(R.id.ReturnButton);
        // Boton para volver a la pestana anterior
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_Info.this, activity_Main.class);

                // Establece la bandera FLAG_ACTIVITY_CLEAR_TOP para limpiar la pila
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Inicia la actividad
                startActivity(intent);

                // Cierra la actividad actual
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

        // Boton para reiniciar las medallas del juego
        // More Info: https://developer.android.com/training/snackbar/showing?hl=es-419#java
        // More Info: https://es.stackoverflow.com/questions/46179/a%C3%B1adir-elementos-a-un-array-en-un-archivo-json-en-android
        Button btnResetGameData = findViewById(R.id.resetGameData);
        btnResetGameData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // El nombre de tu archivo JSON
                String jsonFileName = "medals.json";

                // Comprueba si el archivo existe en el almacenamiento interno
                File internalJsonFile = new File(getFilesDir(), jsonFileName);
                if (internalJsonFile.exists()) {
                    // Si existe, borra el archivo del almacenamiento interno
                    internalJsonFile.delete();
                    System.out.println("File deleted from internal storage");
                }

                // Copia el archivo JSON con las medallas apagadas al almacenamiento interno
                int error = CopyRawToSDCard(R.raw.medals, getFilesDir() + "/" + jsonFileName);
                Snackbar mySnackbar;
                if (error == 0) {
                    // Preparamos el mensaje de reseteo
                    mySnackbar = Snackbar.make(view, "Game Medals Reset!", LENGTH_SHORT);
                } else {
                    // Preparamos el mensaje de error
                    mySnackbar = Snackbar.make(view, "!Error! Enable Storage Permissions", LENGTH_SHORT);
                }
                mySnackbar.show();
            }
        });


        shinyModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Checks the status of the button
                if (isChecked) {
                    // El Switch está activado
                    Log.d("Switch", "Shiny True");
                    preferences.edit().putBoolean("shinySwitch_state", true).apply();
                } else {
                    // El Switch está desactivado
                    Log.d("Switch", "Shiny False");
                    preferences.edit().putBoolean("shinySwitch_state", false).apply();
                }
            }
        });


        IASwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Checks the status of the button
                if (isChecked) {
                    // El Switch está activado
                    Log.d("Switch", "IA Switch True");
                    preferences.edit().putBoolean("IASwitch_state", true).apply();
                } else {
                    // El Switch está desactivado
                    Log.d("Switch", "IA Switch False");
                    preferences.edit().putBoolean("IASwitch_state", false).apply();
                }
            }
        });
    }

    public void openBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private int CopyRawToSDCard(int id, String path) {
        InputStream in = getResources().openRawResource(id);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            byte[] buff = new byte[1024];
            int read = 0;
            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
            in.close();
            out.close();
            Log.i(TAG, "copyFile, success!");
            return 0;
        } catch (FileNotFoundException e) {
            Log.e(TAG, "copyFile FileNotFoundException " + e.getMessage());
            return -1;
        } catch (IOException e) {
            Log.e(TAG, "copyFile IOException " + e.getMessage());
            return -2;
        }
    }
}