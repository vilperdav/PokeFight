package com.example.pokefight;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.media.MediaPlayer;
import android.widget.Toast;

import java.util.*;

public class activity_Fight extends AppCompatActivity {

    Handler handler = new Handler();
    static int MAX = 1000;
    static int MIN = -1000;
    static int agentMarks, playerMarks;

    //static int playerAction = -1;
    TextView playerHpTextView, agentHpTextView, textPokemonPlayerName, textPokemonAgentName;
    ImageView pokemonPlayerImg, pokemonAgentImg, typePokemonPlayerIMG, typePokemonAgentIMG;

    public static ArrayList<pokemon> agentPokemonsPased = new ArrayList<pokemon>();
    public static ArrayList<pokemon> playerPokemonsPased = new ArrayList<pokemon>();
    private MediaPlayer backGroundMusic, attackSound;

    private pokemon p1, p2;

    private static int fightSize = 0;

    private static int animDelay = 500; //ms

    @SuppressLint("ResourceAsColor")
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight);

        // Inicializa el MediaPlayer con el archivo de música
        backGroundMusic = MediaPlayer.create(this, R.raw.pokemon_music);

        // Configura el bucle
        backGroundMusic.setLooping(true);
        playMusic();

        // Variable compartida para los switches
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Recupero el estado de los botones de activity_Info
        boolean shinyState = preferences.getBoolean("shinySwitch_state", false);

        // TODO - SELECCIONAR LA IA
        boolean IAState = preferences.getBoolean("IASwitch_state", false);

        // Inicializo las variables de puntos a 0
        playerMarks = 0;
        agentMarks = 0;

        // Recojo los argumentos que han llegado aqui
        playerPokemonsPased = (ArrayList<pokemon>) getIntent().getSerializableExtra("playerPokemonsKey");
        agentPokemonsPased = (ArrayList<pokemon>) getIntent().getSerializableExtra("agentPokemonsKey");

        // Comprobamos si se esta jugando con pokemon shinys o no
        if (shinyState) {
            // Para cada pokemon, cambio su nombre como _s para actualizar la fotografia
            for (pokemon pokemon : playerPokemonsPased) {
                pokemon.setName(pokemon.getName() + "_s");
            }
            for (pokemon pokemon : agentPokemonsPased) {
                pokemon.setName(pokemon.getName() + "_s");
            }
        }

        // Type of battle 1vs1, 3vs3, 6vs6
        fightSize = playerPokemonsPased.size();

        Button specialAttackButton = findViewById(R.id.specialAttackButton);
        Button changePokemonButton = findViewById(R.id.changePokemonButton);
        Button exitGameButton = findViewById(R.id.surrenderButton);
        Button tackleButton = findViewById(R.id.tackleButton);

        p1 = playerPokemonsPased.get(0);
        p2 = agentPokemonsPased.get(0);

        // Actualizamos la GUI con el primer pokemon pasado de cada jugador
        updateFightGuiPlayer(p1);
        updateFightGuiAgent(p2);
        updateSpecialAttackButton(p1, p2);
        printPokemonsInGame(playerPokemonsPased, agentPokemonsPased);

        // Only in 1º start!
        String text = "[PLAYER] - GO AND WIN " + p1.getName() + " !!!";
        // Obtén una referencia al TextView mediante su identificador
        TextView text2WriteGUI = findViewById(R.id.PlayerTextLog);
        // Modifica el texto del TextView
        text2WriteGUI.setText(text);

        text = "[AGENT] - GO AND WIN " + p2.getName() + " !!!";
        // Obtén una referencia al TextView mediante su identificador
        text2WriteGUI = findViewById(R.id.IATextLog);
        // Modifica el texto del TextView
        text2WriteGUI.setText(text);
        tackleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //playerAction = 0;
                nextTurn(playerPokemonsPased, agentPokemonsPased, 0);
                // Animation of attack
                startAnimation();
                // Reproduce el sonido del ataque
                playAttackSound(p1.getMovements().get(0).toString().toLowerCase());

            }
        });

        specialAttackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //playerAction = 1;
                nextTurn(playerPokemonsPased, agentPokemonsPased, 1);
                // Animation of attack
                startAnimation();
                // Reproduce el sonido del ataque
                String attackName = p1.getMovements().get(1).toString().toLowerCase();
                // Reemplazar espacios por guiones bajos
                String attackToPlay = attackName.replace(" ", "_");
                playAttackSound(attackToPlay);
            }
        });

        changePokemonButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //playerAction = 1;
                nextTurn(playerPokemonsPased, agentPokemonsPased, 3);
            }
        });

        exitGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //playerAction = 4;
                nextTurn(playerPokemonsPased, agentPokemonsPased, 4);
                vibrate();
            }
        });

    }

    private void playAttackSound(String attack) {

        int attackSoundId = getResources().getIdentifier(attack, "raw", getPackageName());
        attackSound = MediaPlayer.create(this, attackSoundId);

        // Verificar si se creó el reproductor de medios correctamente
        if (attackSound != null) {
            // Iniciar la reproducción del sonido
            attackSound.start();

            // Configurar un listener para liberar recursos después de la reproducción
            attackSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // Liberar recursos del reproductor de medios
                    mp.release();
                }
            });
        }
    }

    private void playMusic() {

        // Comprobamos el estado del boton antes de reproducir la musica
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean musicState = preferences.getBoolean("music_state", false);

        // Si se quiere escuchar musica
        if (musicState) {
            // Comienza la reproducción
            if (!backGroundMusic.isPlaying()) {
                backGroundMusic.start();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Libera recursos cuando la actividad se destruye
        if (backGroundMusic != null) {
            backGroundMusic.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Comprobamos el estado del boton antes de reproducir la musica
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean musicState = preferences.getBoolean("music_state", false);

        // Si se quiere escuchar musica
        if (musicState) {
            // Comienza la reproducción
            if (!backGroundMusic.isPlaying()) {
                backGroundMusic.start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Libera recursos cuando la actividad se destruye
        if (backGroundMusic != null) {
            backGroundMusic.release();
            backGroundMusic = null;
        }
    }

    public int nextTurn(ArrayList<pokemon> playerPokemons, ArrayList<pokemon> agentPokemons, int playerDecision) {

        // Comprobamos que los arrays tienen pokemons
        if (!(playerPokemons.isEmpty()) && (!agentPokemons.isEmpty())) {

            // Status of the Pokemon
            System.out.println("\n[HP] - PLAYER - " + p1.getName() + ": " + p1.getHealth() + " hp");
            System.out.println("[HP] - AGENT - " + p2.getName() + ": " + p2.getHealth() + " hp");

            switch (chooseFirstMove(p1, p2)) {

                // The Player Pokemon is Faster
                case 0:

                    System.out.println(
                            "\n[START-FIGHT] - Player starts first - SPEED: " + p1.getSpeed() + " > " + p2.getSpeed());

                    // *****************************************
                    // PLAYER 1 CODE
                    // *****************************************

                    if (p1.getHealth() > 0) {

                        // It chooses surrender
                        if (playerDecision == 4) {

                            System.out.println("\n[SURRENDER] - PLAYER lose and go out of the activity_Fight.");
                            System.out.println("\n[REWARD] - Better luck next time :).");

                            Intent intent = new Intent(activity_Fight.this, activity_Lose.class);

                            // Establece la bandera FLAG_ACTIVITY_CLEAR_TOP para limpiar la pila
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            // Inicia la actividad
                            startActivity(intent);

                            // Cierra la actividad actual
                            finish();

                            return 2;
                        }

                        // It chooses change pokemon
                        if (playerDecision == 3) {

                            String text = "Choose one pokemon!";

                            if (fightSize == 1) {
                                text = "You can't change a pokemon in 1vs1 match.";

                            } else if (playerPokemons.size() == 1) {
                                text = "You don't have more pokemon to change.";

                            } else {

                                // Choose Change pokemon
                                Intent intent = new Intent(getApplicationContext(), activity_ChangePokemon.class);

                                // Agregar el array al Intent
                                intent.putExtra("playerPokemonsKey", playerPokemonsPased);

                                // Iniciar la Activity
                                selectAPokemonToChange.launch(intent);

                            }

                            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

                        } else {
                            // Choose Attack

                            // Say what atack chooses the player
                            System.out.println(
                                    "\n[ATACK] - Player Select '" + p1.getMovements().get(playerDecision).toString()
                                            + "'");

                            // Obtains the attackEfectivity of the Atack
                            double playerAtackEfective = atackEfective(p1.getType(), p2.getType(),
                                    p1.getMovements().get(playerDecision).toString());

                            // Obtains the realDamage of the atack
                            int realDamagePlayer = updateHP(p2, (int) Math.round(p1.getAtack() * playerAtackEfective), 2);

                            // Se inicializa el valor del texto con el valor del daño realizado
                            String text = "[ATACK] - Player made " + realDamagePlayer + " points of damage to "
                                    + p2.getName() + " using " + p1.getMovements().get(playerDecision).toString() + ".";

                            // To say a notification of the damage done
                            if (realDamagePlayer == -1) {
                                // -1 -> Pokemon Debilitado
                                text = "[ATACK] - The agent pokemon was defeated!.";

                            } else if (realDamagePlayer == -2) {
                                // -2 -> Ningun pokemon tiene vida -> ERROR
                                text = "[ERROR] - Take a Screenshot and report to the developers. Thanks.";
                            }

                            // Say the damage done in the pokemon log
                            System.out.println(text);

                            // Obtén una referencia al TextView mediante su identificador
                            TextView text2WriteGUI = findViewById(R.id.PlayerTextLog);
                            // Modifica el texto del TextView
                            text2WriteGUI.setText(text);

                        }
                    }

                    // *****************************************
                    // PLAYER 1 CODE
                    // *****************************************

                    // *****************************************
                    // RANDOM AGENT CODE
                    // *****************************************

                    // El Handler es para que la ejecución del agente sea un poco mas lenta
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            // If the p2 has more than 0 points of life it chooses its atack
                            if (p2.getHealth() > 0 && playerDecision != 3) {

                                int agentType = 0;

                                if (agentType == 0) {

                                    // Means randomAgent
                                    int agentAction = randomAgent.chooseAction(fightSize, agentPokemons);

                                    if (agentAction == 2) {
                                        // Choose Change

                                        String oldPokemonName = p2.getName();
                                        // Agent wants to change its pokemon
                                        int pokemonPosition = randomAgent.choosePokemonToChange(agentPokemons);
                                        p2 = agentPokemons.get(pokemonPosition);

                                        // To say a notification of the damage done
                                        String text = "[CHANGE] - Agent wants to change the pokemon " + oldPokemonName + " -> " + p2.getName() + ".";

                                        // Say the damage done in the pokemon log
                                        System.out.println(text);

                                        // Obtén una referencia al TextView mediante su identificador
                                        TextView text2WriteGUI = findViewById(R.id.IATextLog);
                                        // Modifica el texto del TextView
                                        text2WriteGUI.setText(text);

                                        // Actualizamos la GUI con el primer pokemon pasado de cada jugador
                                        updateFightGuiAgent(p2);
                                        updateSpecialAttackButton(p1, p2);

                                    } else {
                                        // Escoge atacar

                                        String attackName = p2.getMovements().get(agentAction).toString();
                                        // Reemplazar espacios por guiones bajos
                                        String attackToPlay = attackName.replace(" ", "_").toLowerCase();
                                        playAttackSound(attackToPlay);
                                        // Animación del ataque
                                        startAnimationInv();
                                        vibrate();

                                        // Say what atack chooses the agent
                                        System.out.println(
                                                "\n[ATACK] - Agent Select '" + attackName + "'");

                                        // Obtains the attackEfectivity of the Atack
                                        double agentAtackEfective = atackEfective(p2.getType(), p1.getType(),
                                                p2.getMovements().get(agentAction).toString());

                                        // Updates the current life of the pokemons
                                        int realDamageAgent = updateHP(p1, (int) Math.round(p2.getAtack() * agentAtackEfective), 1);

                                        // To say a notification of the damage done
                                        String text = "[ATACK] - Agent made " + realDamageAgent + " points of damage to "
                                                + p1.getName() + " using " + p2.getMovements().get(agentAction).toString() + ".";

                                        // To say a notification of the damage done
                                        if (realDamageAgent == -1) {
                                            // -1 -> Pokemon Debilitado
                                            text = "[ATACK] - The player pokemon was defeated!.";

                                        } else if (realDamageAgent == -2) {
                                            // -2 -> Ningun pokemon tiene vida -> ERROR
                                            text = "[ERROR] - Take a Screenshot and report to the developers. Thanks.";
                                        }

                                        // Say the damage done in the pokemon log
                                        System.out.println(text);

                                        // Obtén una referencia al TextView mediante su identificador
                                        TextView text2WriteGUI = findViewById(R.id.IATextLog);
                                        // Modifica el texto del TextView
                                        text2WriteGUI.setText(text);
                                    }


                                } else if (agentType == 1) {
                                    // Means MIN-MAX AGENT - EASY
                                    // TODO - MIN-MAX EASY


                                } else if (agentType == 2) {
                                    // Means MIN-MAX AGENT - HARD
                                    // TODO - MIN-MAX HARD

                                }

                            }
                        }
                    }, animDelay);

                    // *****************************************
                    // RANDOM AGENT CODE
                    // *****************************************

                    break;
                case 1:

                    // The Agent Pokemon is Faster
                    System.out.println(
                            "\n[START-FIGHT] - Agent starts first - SPEED: " + p2.getSpeed() + " > " + p1.getSpeed());

                    // *****************************************
                    // RANDOM AGENT CODE
                    // *****************************************

                    // El Handler es para que la ejecución del agente sea un poco mas lenta
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            // If the p2 has more than 0 points of life it chooses its atack
                            if (p2.getHealth() > 0 && playerDecision != 3) {

                                int agentType = 0;

                                if (agentType == 0) {

                                    // Means randomAgent
                                    int agentAction = randomAgent.chooseAction(fightSize, agentPokemons);

                                    if (agentAction == 2) {
                                        // Choose Change

                                        String oldPokemonName = p2.getName();
                                        // Agent wants to change its pokemon
                                        int pokemonPosition = randomAgent.choosePokemonToChange(agentPokemons);
                                        p2 = agentPokemons.get(pokemonPosition);

                                        // To say a notification of the damage done
                                        String text = "[CHANGE] - Agent wants to change the pokemon " + oldPokemonName + " -> " + p2.getName() + ".";

                                        // Say the damage done in the pokemon log
                                        System.out.println(text);

                                        // Obtén una referencia al TextView mediante su identificador
                                        TextView text2WriteGUI = findViewById(R.id.IATextLog);
                                        // Modifica el texto del TextView
                                        text2WriteGUI.setText(text);

                                        // Actualizamos la GUI con el primer pokemon pasado de cada jugador
                                        updateFightGuiAgent(p2);
                                        updateSpecialAttackButton(p1, p2);

                                    } else {
                                        // Escoge atacar
                                        String attackName = p2.getMovements().get(agentAction).toString();
                                        // Reemplazar espacios por guiones bajos
                                        String attackToPlay = attackName.replace(" ", "_").toLowerCase();
                                        playAttackSound(attackToPlay);
                                        // Animación del ataque
                                        startAnimationInv();
                                        vibrate();

                                        // Say what atack chooses the agent
                                        System.out.println(
                                                "\n[ATACK] - Agent Select '" + attackName + "'");

                                        // Obtains the attackEfectivity of the Atack
                                        double agentAtackEfective = atackEfective(p2.getType(), p1.getType(),
                                                p2.getMovements().get(agentAction).toString());

                                        // Updates the current life of the pokemons
                                        int realDamageAgent = updateHP(p1, (int) Math.round(p2.getAtack() * agentAtackEfective), 1);

                                        // To say a notification of the damage done
                                        String text = "[ATACK] - Agent made " + realDamageAgent + " points of damage to "
                                                + p1.getName() + " using " + p2.getMovements().get(agentAction).toString() + ".";

                                        // To say a notification of the damage done
                                        if (realDamageAgent == -1) {
                                            // -1 -> Pokemon Debilitado
                                            text = "[ATACK] - The player pokemon was defeated!.";

                                        } else if (realDamageAgent == -2) {
                                            // -2 -> Ningun pokemon tiene vida -> ERROR
                                            text = "[ERROR] - Take a Screenshot and report to the developers. Thanks.";
                                        }

                                        // Say the damage done in the pokemon log
                                        System.out.println(text);

                                        // Obtén una referencia al TextView mediante su identificador
                                        TextView text2WriteGUI = findViewById(R.id.IATextLog);
                                        // Modifica el texto del TextView
                                        text2WriteGUI.setText(text);
                                    }


                                } else if (agentType == 1) {
                                    // Means MIN-MAX AGENT - EASY
                                    // TODO - MIN-MAX EASY


                                } else if (agentType == 2) {
                                    // Means MIN-MAX AGENT - HARD
                                    // TODO - MIN-MAX HARD

                                }

                            }
                        }
                    }, animDelay);

                    // *****************************************
                    // RANDOM AGENT CODE
                    // *****************************************

                    // *****************************************
                    // PLAYER 1 CODE
                    // *****************************************

                    if (p1.getHealth() > 0) {

                        // It chooses surrender
                        if (playerDecision == 4) {

                            System.out.println("\n[SURRENDER] - PLAYER lose and go out of the activity_Fight.");
                            System.out.println("\n[REWARD] - Better luck next time :).");

                            Intent intent = new Intent(activity_Fight.this, activity_Lose.class);

                            // Establece la bandera FLAG_ACTIVITY_CLEAR_TOP para limpiar la pila
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            // Inicia la actividad
                            startActivity(intent);

                            // Cierra la actividad actual
                            finish();

                            return 2;
                        }

                        // It chooses change pokemon
                        if (playerDecision == 3) {

                            String text = "Choose one pokemon!";

                            if (fightSize == 1) {
                                text = "You can't change a pokemon in 1vs1 match.";

                            } else if (playerPokemons.size() == 1) {
                                text = "You don't have more pokemon to change.";

                            } else {

                                // Choose Change pokemon
                                Intent intent = new Intent(getApplicationContext(), activity_ChangePokemon.class);

                                // Agregar el array al Intent
                                intent.putExtra("playerPokemonsKey", playerPokemonsPased);

                                // Iniciar la Activity
                                selectAPokemonToChange.launch(intent);

                            }

                            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

                        } else {
                            // Choose Attack

                            // Say what atack chooses the player
                            System.out.println(
                                    "\n[ATACK] - Player Select '" + p1.getMovements().get(playerDecision).toString()
                                            + "'");

                            // Obtains the attackEfectivity of the Atack
                            double playerAtackEfective = atackEfective(p1.getType(), p2.getType(),
                                    p1.getMovements().get(playerDecision).toString());

                            // Obtains the realDamage of the atack
                            int realDamagePlayer = updateHP(p2, (int) Math.round(p1.getAtack() * playerAtackEfective), 2);

                            // To say a notification of the damage done
                            String text = "[ATACK] - Player made " + realDamagePlayer + " points of damage to "
                                    + p2.getName() + " using " + p1.getMovements().get(playerDecision).toString() + ".";

                            // To say a notification of the damage done
                            if (realDamagePlayer == -1) {
                                // -1 -> Pokemon Debilitado
                                text = "[ATACK] - The agent pokemon was defeated!.";

                            } else if (realDamagePlayer == -2) {
                                // -2 -> Ningun pokemon tiene vida -> ERROR
                                text = "[ERROR] - Take a Screenshot and report to the developers. Thanks.";
                            }

                            // Say the damage done in the pokemon log
                            System.out.println(text);

                            // Obtén una referencia al TextView mediante su identificador
                            TextView text2WriteGUI = findViewById(R.id.PlayerTextLog);
                            // Modifica el texto del TextView
                            text2WriteGUI.setText(text);

                        }
                    }

                    // *****************************************
                    // PLAYER 1 CODE
                    // *****************************************

                    break;

                default:
                    break;

            }

            // One of the pokemons has 0 hp soo we delete if from the array

            // The agent pokemon has 0 hp or less
            if (p2.getHealth() <= 0) {

                int pokePos = agentPokemons.indexOf(p2);
                playerMarks++;

                // We delete the tired pokemon from the array
                String text = "[GO-OUT] - Agent Pokemon " + p2.getName() + " go out of the battle.";
                // Say the damage done in the pokemon log
                System.out.println(text);
                // Obtén una referencia al TextView mediante su identificador
                TextView text2WriteGUI = findViewById(R.id.IATextLog);
                // Modifica el texto del TextView
                text2WriteGUI.setText(text);

                agentPokemons.remove(pokePos);

                // We obtain a new pokemon
                if (!agentPokemons.isEmpty()) {
                    p2 = null;
                    p2 = agentPokemons.get(0);
                    System.out.println(
                            "\n[GO-INT] - Agent Pokemon chooses " + p2.getName()
                                    + " as the new pokemon.");

                    // Actualizamos el pokemon actual en pantalla
                    updateFightGuiAgent(p2);
                    updateSpecialAttackButton(p1, p2);

                } else {
                    // Entramos aqui cuando uno de los arrays esta vacio de pokemons
                    chooseWinner();
                }
            }

            // The player pokemon has 0 hp
            if (p1.getHealth() <= 0) {

                int pokePos = playerPokemons.indexOf(p1);
                agentMarks++;

                // We delete the tired pokemon from the array
                String text = "[GO-OUT] - Player Pokemon " + p1.getName() + " go out of the battle.";
                // Say the damage done in the pokemon log
                System.out.println(text);
                // Obtén una referencia al TextView mediante su identificador
                TextView text2WriteGUI = findViewById(R.id.PlayerTextLog);
                // Modifica el texto del TextView
                text2WriteGUI.setText(text);

                playerPokemons.remove(pokePos);

                // We obtain a new pokemon
                if (!playerPokemons.isEmpty()) {
                    p1 = null;
                    p1 = playerPokemons.get(0);
                    System.out.println(
                            "\n[GO-INT] - Player Pokemon chooses " + p1.getName()
                                    + " as the new pokemon.");

                    // Actualizamos el pokemon actual en pantalla
                    updateFightGuiPlayer(p1);
                    updateSpecialAttackButton(p1, p2);

                } else {
                    // Entramos aqui cuando uno de los arrays esta vacio de pokemons
                    chooseWinner();
                }

            }

            // Print the current pokemons in the game
            printPokemonsInGame(playerPokemons, agentPokemons);

        } else {

            // Entramos aqui cuando uno de los arrays esta vacio de pokemons
            chooseWinner();
        }

        return 0;
    }


    private void chooseWinner() {

        // Choose the winner based on the marks of the pokemon defeated
        if (playerMarks > agentMarks) {

            System.out.println("\n[GO-OUT] - AGENT Pokemon go out of the activity_Fight.");
            System.out.println("\n[REWARD] - Congrats you recibe a GYM medal!.");

            Intent intent = new Intent(activity_Fight.this, activity_Win.class);

            // Establece la bandera FLAG_ACTIVITY_CLEAR_TOP para limpiar la pila
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Inicia la actividad
            startActivity(intent);

            // Cierra la actividad actual
            finish();

        } else {

            System.out.println("\n[GO-OUT] - PLAYER Pokemon go out of the activity_Fight.");
            System.out.println("\n[REWARD] - Better luck next time :).");

            Intent intent = new Intent(activity_Fight.this, activity_Lose.class);

            // Establece la bandera FLAG_ACTIVITY_CLEAR_TOP para limpiar la pila
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Inicia la actividad
            startActivity(intent);

            // Cierra la actividad actual
            finish();

        }
    }

    private void printPokemonsInGame(ArrayList<pokemon> playerPokemons, ArrayList<pokemon> agentPokemons) {

        int numberOfPokeballs = 6;

        // Lleno con pokeballs vacias del jugador.
        for (int i = 1; i <= numberOfPokeballs; i++) {
            String pokeballID = "pokeballP" + i;
            int resID = getResources().getIdentifier(pokeballID, "id", getPackageName());
            ImageView pokeball = findViewById(resID);
            // Cambia la imagen usando el recurso drawable
            pokeball.setImageResource(R.drawable.pokeball);
        }

        // Lleno con pokeballs vacias del agente.
        for (int i = 1; i <= numberOfPokeballs; i++) {
            String pokeballID = "pokeballIA" + i;
            int resID = getResources().getIdentifier(pokeballID, "id", getPackageName());
            ImageView pokeball = findViewById(resID);
            // Cambia la imagen usando el recurso drawable
            pokeball.setImageResource(R.drawable.pokeball);
        }

        // See all pokemons currently in game
        int i = 0;
        System.out.print("\n[P1-POKEMONS] - Player Pokemons => [ ");
        for (pokemon pokemonInList : playerPokemons) {
            System.out.print(pokemonInList.getName() + " [HP]: " + (pokemonInList.getHealth()) + ", ");

            // Obtén una referencia al ImageView utilizando el ID dinámico
            i++;
            String pokeballID = "pokeballP" + i;
            int resID = getResources().getIdentifier(pokeballID, "id", getPackageName());
            ImageView pokeball = findViewById(resID);
            // Asegúrate de que el ID del recurso drawable sea minúsculas
            String pokemonName = pokemonInList.getName().toLowerCase();
            // Obtiene el ID del recurso drawable dinámicamente
            int drawableID = getResources().getIdentifier(pokemonName, "drawable", getPackageName());
            // Cambia la imagen usando el recurso drawable
            pokeball.setImageResource(drawableID);

        }
        System.out.print("]\n");

        i = 0;
        // See all pokemons selected
        System.out.print("\n[P2-POKEMONS] - Agent Pokemons => [ ");
        for (pokemon pokemonInList : agentPokemons) {
            System.out.print(pokemonInList.getName() + "  [HP]: " + (pokemonInList.getHealth()) + ", ");

            // Obtén una referencia al ImageView utilizando el ID dinámico
            i++;
            String pokeballID = "pokeballIA" + i;
            int resID = getResources().getIdentifier(pokeballID, "id", getPackageName());
            ImageView pokeball = findViewById(resID);
            // Asegúrate de que el ID del recurso drawable sea minúsculas
            String pokemonName = pokemonInList.getName().toLowerCase();
            // Obtiene el ID del recurso drawable dinámicamente
            int drawableID = getResources().getIdentifier(pokemonName, "drawable", getPackageName());
            // Cambia la imagen usando el recurso drawable
            pokeball.setImageResource(drawableID);
        }
        System.out.print("]\n");

    }

    private static int chooseFirstMove(pokemon p1, pokemon p2) {

        // Choose First Player to Play based on Speed Parameter

        // p1 > p2
        if (p1.getSpeed() > p2.getSpeed()) {
            return 0;

            // p2 > p1
        } else if (p2.getSpeed() > p1.getSpeed()) {
            return 1;

            // p1 == p2
        } else {

            // Selection of the first movement random
            // Betwen 0-1
            Random azar = new Random();
            return azar.nextInt(2);
        }
    }

    // Function for reduce the HP points
    private int updateHP(pokemon p1, int atack, int player) {

        // 30% of defense reduction atack
        double defenseFactor = 0.03;
        int defense = (int) Math.round(p1.getDefense() * defenseFactor);
        int newLife = p1.getHealth() - atack + defense;
        p1.setHealth(newLife);
        int health = p1.getHealth();
        int maxHealth = p1.getMaxHealth();

        // Update the progress bar of life
        ProgressBar yourPokemonBarHealth = (ProgressBar) findViewById(R.id.YourPokemonHealthBar);
        ProgressBar IAPokemonBarHealth = (ProgressBar) findViewById(R.id.IAPokemonHealthBar);

        if (player == 1) {
            if (health > 0) {

                // Si no es menos de la mitad se queda en verde
                this.playerHpTextView.setText("HP: " + health + " / " + maxHealth);
                yourPokemonBarHealth.setMax(maxHealth); // Establece la vida máxima
                yourPokemonBarHealth.setProgress(health); // Establece la vida actual
                yourPokemonBarHealth.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN); // Establece el color inicial de la barra de vida
                playerHpTextView.setTextColor(ContextCompat.getColor(this, R.color.LightGreen));

                if (health < (maxHealth * 0.3)) {

                    // Menos del 30% cambiamos el color del texto a LightRed
                    this.playerHpTextView.setTextColor(ContextCompat.getColor(this, R.color.LightRed));
                    yourPokemonBarHealth.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

                } else if (health < (maxHealth * 0.5)) {

                    // Menos del 50% cambiamos el color del texto a LightYellow
                    this.playerHpTextView.setTextColor(ContextCompat.getColor(this, R.color.LightYellow));
                    yourPokemonBarHealth.getProgressDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
                }

                return (atack - defense);

            } else {

                p1.setHealth(0);
                this.playerHpTextView.setText("HP: 0");
                this.playerHpTextView.setTextColor(ContextCompat.getColor(this, R.color.Red));
                yourPokemonBarHealth.setMax(maxHealth); // Establece la vida máxima
                yourPokemonBarHealth.setProgress(0); // Establece la vida actual
                yourPokemonBarHealth.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                return -1;

            }
        } else if (player == 2) {
            if (health > 0) {

                // Si no es menos de la mitad se queda en verde
                this.agentHpTextView.setText("HP: " + health + " / " + maxHealth);
                IAPokemonBarHealth.setMax(maxHealth); // Establece la vida máxima
                IAPokemonBarHealth.setProgress(health); // Establece la vida actual
                IAPokemonBarHealth.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN); // Establece el color inicial de la barra de vida
                playerHpTextView.setTextColor(ContextCompat.getColor(this, R.color.LightGreen));

                if (health < (maxHealth * 0.3)) {

                    // Menos del 30% cambiamos el color del texto a LightRed
                    IAPokemonBarHealth.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN); // Establece el color inicial de la barra de vida
                    this.agentHpTextView.setTextColor(ContextCompat.getColor(this, R.color.LightRed));

                } else if (health < (maxHealth * 0.5)) {

                    // Menos del 50% cambiamos el color del texto a LightYellow
                    this.agentHpTextView.setTextColor(ContextCompat.getColor(this, R.color.LightYellow));
                    IAPokemonBarHealth.getProgressDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN); // Establece el color inicial de la barra de vida
                }

                return (atack - defense);

            } else {

                p2.setHealth(0);
                this.agentHpTextView.setText("HP: 0");
                this.agentHpTextView.setTextColor(ContextCompat.getColor(this, R.color.Red));

                IAPokemonBarHealth.setMax(maxHealth); // Establece la vida máxima
                IAPokemonBarHealth.setProgress(0); // Establece la vida actual
                IAPokemonBarHealth.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN); // Establece el color inicial de la barra de vida

                return -1;

            }
        }

        return -2;
    }

    // Function for know if the atack its efective or not
    private static double atackEfective(String type1, String type2, String attack) {

        // Fire >>> Plant
        // Plant >>> Water
        // Water >>> Fire

        // Tackle always do x1
        if (attack.equals("Tackle")) {
            return 0.1;
            // Same type of Pokemon and diferent atack from tackle
        } else if (type1.equals(type2)) {
            return 0.05;
            // Fire vs Water
        } else if (type1.equals("fire") && type2.equals("water")) {
            return 0.05;
            // Fire vs Plant
        } else if (type1.equals("fire") && type2.equals("plant")) {
            return 0.2;
            // Water vs Fire
        } else if (type1.equals("water") && type2.equals("fire")) {
            return 0.2;
            // Water vs Plant
        } else if (type1.equals("water") && type2.equals("plant")) {
            return 0.05;
            // Plant vs Water
        } else if (type1.equals("plant") && type2.equals("water")) {
            return 0.2;
            // Plant vs Fire
        } else if (type1.equals("plant") && type2.equals("fire")) {
            return 0.05;
            // In other case the attack dont do damage
        } else {
            return 0;
        }
    }

    private void updateSpecialAttackButton(pokemon p1, pokemon p2) {
        Button specialAttackButton = findViewById(R.id.specialAttackButton);
        double efect = atackEfective(p1.getType(), p2.getType(), "null");
        specialAttackButton.setText(p1.getMovements().get(1).toString().toUpperCase() + "\n x " + efect * 10);
    }

    private void updateFightGuiPlayer(pokemon playerPokemon) {

        // Valores obtenidos de cada pokemon para actualizar
        String pokemonPlayerName = playerPokemon.getName();
        int pokemonPlayerHealth = playerPokemon.getHealth();
        int pokemonPlayerMaxHealth = playerPokemon.getMaxHealth();
        String pokemonPlayerType = playerPokemon.getType();

        // Botones a los que vamos a acceder desde la funcion
        Button specialAttackButton = findViewById(R.id.specialAttackButton);

        // Actualizo el nombre del pokemon en la ventana del combate
        textPokemonPlayerName = findViewById(R.id.textPokemonPlayerName);
        this.textPokemonPlayerName.setText(pokemonPlayerName);

        // Actualizo la vida maxima del pokemon en la ventana del combate y su color
        ProgressBar yourPokemonBarHealth = (ProgressBar) findViewById(R.id.YourPokemonHealthBar);
        /*vida.setMax(pokemonPlayerMaxHealth); // Establece la vida máxima
        vida.setProgress(pokemonPlayerHealth); // Establece la vida
        vida.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN); // Establece el color inicial de la barra de vida

        this.playerHpTextView.setText("HP: " + pokemonPlayerHealth + " / " + pokemonPlayerMaxHealth);
        this.playerHpTextView.setTextColor(ContextCompat.getColor(this, R.color.LightGreen));*/

        // Si no es menos de la mitad se queda en verde
        playerHpTextView = findViewById(R.id.textPlayerHealth);

        playerHpTextView.setText("HP: " + pokemonPlayerHealth + " / " + pokemonPlayerMaxHealth);
        yourPokemonBarHealth.setMax(pokemonPlayerMaxHealth); // Establece la vida máxima
        yourPokemonBarHealth.setProgress(pokemonPlayerHealth); // Establece la vida actual
        yourPokemonBarHealth.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN); // Establece el color inicial de la barra de vida
        playerHpTextView.setTextColor(ContextCompat.getColor(this, R.color.LightGreen));

        if (pokemonPlayerHealth < (pokemonPlayerMaxHealth * 0.3)) {

            // Menos del 30% cambiamos el color del texto a LightRed
            playerHpTextView.setTextColor(ContextCompat.getColor(this, R.color.LightRed));
            yourPokemonBarHealth.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

        } else if (pokemonPlayerHealth < (pokemonPlayerMaxHealth * 0.5)) {

            // Menos del 50% cambiamos el color del texto a LightYellow
            playerHpTextView.setTextColor(ContextCompat.getColor(this, R.color.LightYellow));
            yourPokemonBarHealth.getProgressDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
        }


        // Actualizo los tipos de Pokemon en la foto y en el ataque especial
        typePokemonPlayerIMG = findViewById(R.id.typePokemonPlayerIMG);

        // Cambia el color del boton de ataque especial dependiendo del tipo de ataque
        if (pokemonPlayerType.equals("fire")) {
            //specialAttackButton.setTextColor(ContextCompat.getColor(this, R.color.Red));
            specialAttackButton.setBackgroundColor(ContextCompat.getColor(this, R.color.LightRed));
            typePokemonPlayerIMG.setImageResource(getResources().getIdentifier("type_fire", "drawable", getPackageName()));
        } else if (pokemonPlayerType.equals("water")) {
            //specialAttackButton.setTextColor(ContextCompat.getColor(this, R.color.Blue));
            specialAttackButton.setBackgroundColor(ContextCompat.getColor(this, R.color.LightBlue));
            typePokemonPlayerIMG.setImageResource(getResources().getIdentifier("type_water", "drawable", getPackageName()));
        } else if (pokemonPlayerType.equals("plant")) {
            //specialAttackButton.setTextColor(ContextCompat.getColor(this, R.color.Green));
            specialAttackButton.setBackgroundColor(ContextCompat.getColor(this, R.color.LightGreen));
            typePokemonPlayerIMG.setImageResource(getResources().getIdentifier("type_plant", "drawable", getPackageName()));
        }

        // Actualizamos las imagenes de los pokemon
        pokemonPlayerImg = findViewById(R.id.imagePlayerPokemon);
        fadeIn(pokemonPlayerImg);
        this.pokemonPlayerImg.setImageResource(getResources().getIdentifier(pokemonPlayerName.toLowerCase(), "drawable", getPackageName()));
    }


    private void updateFightGuiAgent(pokemon agentPokemon) {

        String pokemonAgentName = agentPokemon.getName();
        int pokemonAgentHealth = agentPokemon.getHealth();
        int pokemonAgentMaxHealth = agentPokemon.getMaxHealth();
        String pokemonAgentType = agentPokemon.getType();
        String pokemonAgentSpecialAtack = (String) agentPokemon.getMovements().get(1);

        // Actualizo el nombre del pokemon en la ventana del combate
        textPokemonAgentName = findViewById(R.id.textPokemonAgentName);
        this.textPokemonAgentName.setText(pokemonAgentName);

        // Actualizo la vida maxima del pokemon en la ventana del combate y su color
        ProgressBar IAPokemonBarHealth = (ProgressBar) findViewById(R.id.IAPokemonHealthBar);
        /*vidaIA.setMax(pokemonAgentMaxHealth); // Establece la vida máxima
        vidaIA.setProgress(pokemonAgentHealth); // Establece la vida
        vidaIA.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN); // Establece el color inicial de la barra de vida

        this.agentHpTextView.setText("HP: " + pokemonAgentHealth + " / " + pokemonAgentMaxHealth);
        this.agentHpTextView.setTextColor(ContextCompat.getColor(this, R.color.LightGreen));*/

        // Si no es menos de la mitad se queda en verde

        agentHpTextView = findViewById(R.id.textAgentHealth);

        agentHpTextView.setText("HP: " + pokemonAgentHealth + " / " + pokemonAgentMaxHealth);
        IAPokemonBarHealth.setMax(pokemonAgentMaxHealth); // Establece la vida máxima
        IAPokemonBarHealth.setProgress(pokemonAgentHealth); // Establece la vida actual
        IAPokemonBarHealth.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN); // Establece el color inicial de la barra de vida
        agentHpTextView.setTextColor(ContextCompat.getColor(this, R.color.LightGreen));

        if (pokemonAgentHealth < (pokemonAgentMaxHealth * 0.3)) {

            // Menos del 30% cambiamos el color del texto a LightRed
            IAPokemonBarHealth.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN); // Establece el color inicial de la barra de vida
            agentHpTextView.setTextColor(ContextCompat.getColor(this, R.color.LightRed));

        } else if (pokemonAgentHealth < (pokemonAgentMaxHealth * 0.5)) {

            // Menos del 50% cambiamos el color del texto a LightYellow
            agentHpTextView.setTextColor(ContextCompat.getColor(this, R.color.LightYellow));
            IAPokemonBarHealth.getProgressDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN); // Establece el color inicial de la barra de vida
        }

        // Actualizo los tipos de Pokemon en la foto y en el ataque especial
        typePokemonAgentIMG = findViewById(R.id.typePokemonAgentIMG);

        // Cambiamos el tipo del pokemon que se ve en pantalla
        if (pokemonAgentType.equals("fire")) {
            typePokemonAgentIMG.setImageResource(getResources().getIdentifier("type_fire", "drawable", getPackageName()));
        } else if (pokemonAgentType.equals("water")) {
            typePokemonAgentIMG.setImageResource(getResources().getIdentifier("type_water", "drawable", getPackageName()));
        } else if (pokemonAgentType.equals("plant")) {
            typePokemonAgentIMG.setImageResource(getResources().getIdentifier("type_plant", "drawable", getPackageName()));
        }

        // Actualizamos las imagenes de los pokemon
        pokemonAgentImg = findViewById(R.id.imageAgentPokemon);
        fadeIn(pokemonAgentImg);
        this.pokemonAgentImg.setImageResource(getResources().getIdentifier(pokemonAgentName.toLowerCase(), "drawable", getPackageName()));
    }

    // Funcion que permite cambiar el pokemon durante una batalla
    ActivityResultLauncher<Intent> selectAPokemonToChange = registerForActivityResult(

            new ActivityResultContracts.StartActivityForResult(),

            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        // Obtenemos la posicion del pokemon y la cambiamos al pokemon actual
                        int pokemonPosition = (int) data.getSerializableExtra("selectedPokemonPosition");
                        p1 = playerPokemonsPased.get(pokemonPosition);
                    }
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Actualizamos la GUI con el primer pokemon pasado de cada jugador
                            updateFightGuiPlayer(p1);
                            updateSpecialAttackButton(p1, p2);
                        }
                    }, animDelay);

                }
            }
    );

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Comprobar si el dispositivo admite la vibración y si no, salir
        if (vibrator == null || !vibrator.hasVibrator()) {
            return;
        }

        // API 26 y versiones posteriores
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            // Versiones anteriores a la API 26
            vibrator.vibrate(200);
        }
    }

    private void startAnimation() {
        // Cargar la animación desde el archivo de recursos
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake_animation);

        // Aplicar la animación a la imagen
        pokemonPlayerImg.startAnimation(shake);
    }

    private void startAnimationInv() {

        // Cargar la animación desde el archivo de recursos
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake_animation_inv);

        // Aplicar la animación a la imagen
        pokemonAgentImg.startAnimation(shake);
    }

    private void fadeIn(ImageView imgChange) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(1000); // Duración de la animación en milisegundos
        imgChange.setVisibility(View.VISIBLE);
        imgChange.startAnimation(alphaAnimation);
    }
}