package com.example.pokefight;

import static android.app.PendingIntent.getActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;


public class activity_Main extends AppCompatActivity implements fragment_1vs1.OnDataPass,  fragment_3vs3.OnDataPass, fragment_6vs6.OnDataPass {

    public static ArrayList<pokemon> agentPokemonsPased = new ArrayList<pokemon>();
    public static ArrayList<pokemon> playerPokemonsPased = new ArrayList<pokemon>();
    private String currentFragment = "fragment1";


    private MediaPlayer mediaPlayer;
    private void playMusic() {
        // Comienza la reproducción
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
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

        // Inicializa el MediaPlayer con el archivo de música
        mediaPlayer = MediaPlayer.create(this, R.raw.pokemon_intro_music);

        // Configura el bucle
        mediaPlayer.setLooping(true);
        playMusic();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    }

}



