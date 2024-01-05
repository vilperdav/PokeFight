package com.example.pokefight;

import static android.app.PendingIntent.getActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


public class activity_Main extends AppCompatActivity implements fragment_1vs1.OnDataPass, fragment_3vs3.OnDataPass, fragment_6vs6.OnDataPass {

    public static ArrayList<pokemon> agentPokemonsPased = new ArrayList<pokemon>();
    public static ArrayList<pokemon> playerPokemonsPased = new ArrayList<pokemon>();

    private MediaPlayer mediaPlayer;

    private void playMusic() {

        // Comprobamos el estado del boton antes de reproducir la musica
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        ImageButton musicButton = findViewById(R.id.musicButton);
        boolean musicState = preferences.getBoolean("music_state", true);

        // Si se quiere escuchar musica
        if (musicState) {
            // Comienza la reproducción
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                musicButton.setImageResource(R.drawable.music_play);
            }
        } else {
            musicButton.setImageResource(R.drawable.music_stop);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Libera recursos cuando la actividad se destruye
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Comprobamos el estado del boton antes de reproducir la musica
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        ImageButton musicButton = findViewById(R.id.musicButton);
        boolean musicState = preferences.getBoolean("music_state", false);

        // Si se quiere escuchar musica
        if (musicState) {
            // Comienza la reproducción
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                musicButton.setImageResource(R.drawable.music_play);
            }
        } else {
            musicButton.setImageResource(R.drawable.music_stop);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Libera recursos cuando la actividad se destruye
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onDataPass(ArrayList<pokemon> playerPokemons, ArrayList<pokemon> agentPokemons) {
        // Aquí puedes manejar los datos recibidos del Fragment
        agentPokemonsPased = agentPokemons;
        playerPokemonsPased = playerPokemons;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa el MediaPlayer con el archivo de música
        mediaPlayer = MediaPlayer.create(this, R.raw.pokemon_intro_music);

        // Configura el bucle
        mediaPlayer.setLooping(true);
        playMusic();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Ver el fragmento de inicio
        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            fragment_1vs1 fragmentoDeInicio = new fragment_1vs1();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragmentoDeInicio).commit();
        }

        ImageButton imageButton = findViewById(R.id.PlayButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), activity_Fight.class);

                // Agregar el array al Intent
                intent.putExtra("agentPokemonsKey", agentPokemonsPased);
                intent.putExtra("playerPokemonsKey", playerPokemonsPased);

                // Iniciar la Activity
                startActivity(intent);

                // Pauso la musica
                mediaPlayer.pause();
            }
        });

        ImageButton imageButtonMedals = findViewById(R.id.MedalsButton);
        imageButtonMedals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_Main.this, activity_Medals.class);
                startActivity(intent);

                // Pauso la musica
                mediaPlayer.pause();
            }
        });

        // Boton para llevarnos a la ayuda del juego
        ImageButton helpImageButton = findViewById(R.id.helpInfoButton);
        helpImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_Main.this, activity_Info.class);
                startActivity(intent);

                // Pauso la musica
                mediaPlayer.pause();
            }
        });

        // Boton para pausar y continuar la musica
        ImageButton musicButton = findViewById(R.id.musicButton);
        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pauso la musica si esta en reproduccion
                if (!mediaPlayer.isPlaying()) {
                    preferences.edit().putBoolean("music_state", true).apply();
                    musicButton.setImageResource(R.drawable.music_play);
                    mediaPlayer.start();
                } else {
                    preferences.edit().putBoolean("music_state", false).apply();
                    musicButton.setImageResource(R.drawable.music_stop);
                    mediaPlayer.pause();
                }
            }
        });

    }

}



