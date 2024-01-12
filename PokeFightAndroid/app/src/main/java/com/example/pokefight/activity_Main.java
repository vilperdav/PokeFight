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
import android.widget.ImageView;

import java.util.ArrayList;


public class activity_Main extends AppCompatActivity implements fragment_1vs1.OnDataPass, fragment_3vs3.OnDataPass, fragment_6vs6.OnDataPass {

    public static ArrayList<pokemon> agentPokemonsPased = new ArrayList<pokemon>();
    public static ArrayList<pokemon> playerPokemonsPased = new ArrayList<pokemon>();

    private MediaPlayer mediaPlayer;

    /*
     * ********************************************************************************************
     * * playMusic                                                                                  *
     * ********************************************************************************************
     * */
    private void playMusic() {

        // Check if the user wants music or not
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean musicState = preferences.getBoolean("music_state", true);

        // Starts the reproduction and change the button
        ImageButton musicButton = findViewById(R.id.musicButton);
        if (musicState) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                musicButton.setImageResource(R.drawable.music_play);
            }
        } else {
            musicButton.setImageResource(R.drawable.music_stop);
        }
    }

    /*
     * ********************************************************************************************
     * * onPause                                                                                  *
     * ********************************************************************************************
     * */
    @Override
    public void onPause() {
        super.onPause();
        // Pause the music
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    /*
     * ********************************************************************************************
     * * onResume                                                                                  *
     * ********************************************************************************************
     * */
    @Override
    public void onResume() {

        super.onResume();

        // Call to the music function
        playMusic();
    }

    /*
     * ********************************************************************************************
     * * onResume                                                                                  *
     * ********************************************************************************************
     * */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Free resources
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /*
     * ********************************************************************************************
     * * onDataPass                                                                                  *
     * ********************************************************************************************
     * */
    @Override
    public void onDataPass(ArrayList<pokemon> playerPokemons, ArrayList<pokemon> agentPokemons) {
        // Aquí puedes manejar los datos recibidos del Fragment
        agentPokemonsPased = agentPokemons;
        playerPokemonsPased = playerPokemons;

    }

    /*
     * ********************************************************************************************
     * * onCreate                                                                                  *
     * ********************************************************************************************
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Starts the music player with the background song
        mediaPlayer = MediaPlayer.create(this, R.raw.pokemon_intro_music);
        mediaPlayer.setLooping(true);
        playMusic();

        // Shared varaible for Shiny mode in case of use it
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("shinySwitch_state", false)) {
            ImageView shinyIMG1 = findViewById(R.id.shinyMode);
            ImageView shinyIMG2 = findViewById(R.id.shinyMode2);
            shinyIMG1.setVisibility(View.VISIBLE);
            shinyIMG2.setVisibility(View.VISIBLE);
        }

        // Fragment of start
        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            fragment_1vs1 firstFragment = new fragment_1vs1();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }

        // playGameButton
        ImageButton playGameButton = findViewById(R.id.PlayButton);
        playGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), activity_Fight.class);

                // Add the two arrays to the intent of the game button
                intent.putExtra("agentPokemonsKey", agentPokemonsPased);
                intent.putExtra("playerPokemonsKey", playerPokemonsPased);

                // Pause the music and starts the next activity
                mediaPlayer.pause();
                startActivity(intent);
            }
        });

        // medalsButton
        ImageButton medalsButton = findViewById(R.id.MedalsButton);
        medalsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_Main.this, activity_Medals.class);

                // Pause the music and starts the next activity
                mediaPlayer.pause();
                startActivity(intent);
            }
        });

        // helpImageButton
        ImageButton helpImageButton = findViewById(R.id.helpInfoButton);
        helpImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_Main.this, activity_Info.class);

                // Pause the music and starts the next activity
                mediaPlayer.pause();
                startActivity(intent);
            }
        });

        // musicButton
        ImageButton musicButton = findViewById(R.id.musicButton);
        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pause the music if it's on
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



