package com.example.pokefight;

import static android.app.PendingIntent.getActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;


public class activity_Main extends AppCompatActivity implements fragment_1vs1.OnDataPass {

    public static ArrayList<pokemon> agentPokemonsPased = new ArrayList<pokemon>();
    public static ArrayList<pokemon> playerPokemonsPased = new ArrayList<pokemon>();
    private String currentFragment = "fragment1";


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
            }
        });

        ImageButton imageButtonMedals = findViewById(R.id.MedalsButton);
        imageButtonMedals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_Main.this, activity_Medals.class);
                startActivity(intent);
            }
        });

        // Boton para llevarnos a la ayuda del juego
        ImageButton helpImageButton = findViewById(R.id.helpInfoButton);
        helpImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_Main.this, activity_Info.class);
                startActivity(intent);
            }
        });

    }

}



