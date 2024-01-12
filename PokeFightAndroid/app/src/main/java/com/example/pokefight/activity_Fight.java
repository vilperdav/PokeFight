package com.example.pokefight;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.media.MediaPlayer;
import android.widget.Toast;

import java.util.*;

public class activity_Fight extends AppCompatActivity {

    // Parameters for min-max agent
    static pokeAgent a = new pokeAgent();
    private static pokeAgent.GameState ia = a.new GameState(null, null);
    private static pokeAgent.Action move = a.new Action();

    // Common parameters for fight
    Handler handler = new Handler();
    static int agentMarks, playerMarks;
    TextView playerHpTextView, agentHpTextView, textPokemonPlayerName, textPokemonAgentName;
    ImageView pokemonPlayerImg, pokemonAgentImg, typePokemonPlayerIMG, typePokemonAgentIMG;
    public static ArrayList<pokemon> agentPokemonsPased = new ArrayList<pokemon>();
    public static ArrayList<pokemon> playerPokemonsPased = new ArrayList<pokemon>();
    private MediaPlayer backGroundMusic, attackSound;
    private pokemon p1, p2;
    private static int fightSize = 0, deepTree = 0;
    private boolean IAState, specialAttackButtonCheck, tackleButtonCheck, changePokemonButtonCheck, exitGameButtonCheck;
    private static int animDelay = 500; //ms

    @SuppressLint("ResourceAsColor")
    @Override
    /*
     * ********************************************************************************************
     * * onCreate                                                                                 *
     * ********************************************************************************************
     * */
    public void onCreate(Bundle savedInstanceState) {

        // Method on create related to activity_fight
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight);

        // Starts background music if the user want it
        backGroundMusic = MediaPlayer.create(this, R.raw.pokemon_music);
        backGroundMusic.setLooping(true);
        playMusic();

        // Initialize the variables to 0
        playerMarks = 0;
        agentMarks = 0;

        // Get the ArrayList of pokemons
        playerPokemonsPased = (ArrayList<pokemon>) getIntent().getSerializableExtra("playerPokemonsKey");
        agentPokemonsPased = (ArrayList<pokemon>) getIntent().getSerializableExtra("agentPokemonsKey");

        // Shared variable to get the shiny mode and and IAState
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        IAState = preferences.getBoolean("IASwitch_state", false);

        // Check the shiny mode, false for default
        if (preferences.getBoolean("shinySwitch_state", false)) {

            // For all the pokemon, change the name+_s and img
            for (pokemon pokemon : playerPokemonsPased) {
                if (!pokemon.getName().equals(pokemon.getName() + "_s")) {
                    pokemon.setName(pokemon.getName() + "_s");
                }

            }
            for (pokemon pokemon : agentPokemonsPased) {
                if (!pokemon.getName().equals(pokemon.getName() + "_s")) {
                    pokemon.setName(pokemon.getName() + "_s");
                }
            }

            // Change the background
            ImageView backGroundImageView = findViewById(R.id.backGroundIMG);
            backGroundImageView.setImageResource(R.drawable.background_s);

        }

        // Get the first pokemon of each arraylist
        p1 = playerPokemonsPased.get(0);
        p2 = agentPokemonsPased.get(0);

        // Update the GUI
        updateFightGuiPlayer(p1);
        updateFightGuiAgent(p2);
        updateSpecialAttackButton(p1, p2);
        printPokemonsInGame(playerPokemonsPased, agentPokemonsPased);

        // Set the pokemons to the IA Min-Max
        if (!IAState) {
            // We set all the pokemons to the min-max agent
            ia.setAgentPokemons(agentPokemonsPased);
            ia.setPlayerPokemons(playerPokemonsPased);
            System.out.println("\n[IA] - Using Min-MAX IA");
        } else {
            System.out.println("\n[IA] - Using Random Agent");
        }

        // Type of battle 1vs1, 3vs3, 6vs6
        fightSize = playerPokemonsPased.size();

        // 1 vs 1 battle
        if (fightSize == 1) {
            deepTree = 1;
            // 3 vs 3 battle
        } else if (fightSize == 3) {
            deepTree = 2;
            // 6 vs 6 battle
        } else if (fightSize == 6) {
            deepTree = 3;
        }

        // Say GO and WIN for both players
        String text = "[PLAYER] - GO AND WIN " + p1.getName() + " !!!";
        TextView text2WriteGUI = findViewById(R.id.PlayerTextLog);
        text2WriteGUI.setText(text);
        text = "[AGENT] - GO AND WIN " + p2.getName() + " !!!";
        text2WriteGUI = findViewById(R.id.IATextLog);
        text2WriteGUI.setText(text);

        // Tackle Button
        Button tackleButton = findViewById(R.id.tackleButton);
        tackleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tackleButton.isActivated()) {
                    nextTurn(playerPokemonsPased, agentPokemonsPased, 0);
                    // Animation and sound
                    startAnimation();
                    playAttackSound(p1.getMovements().get(0).toString().toLowerCase());
                }
            }
        });

        // SpecialAttack Button
        Button specialAttackButton = findViewById(R.id.specialAttackButton);
        specialAttackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!specialAttackButton.isActivated()) {
                    nextTurn(playerPokemonsPased, agentPokemonsPased, 1);
                    // Animation and sound
                    startAnimation();
                    String attackName = p1.getMovements().get(1).toString().toLowerCase();
                    String attackToPlay = attackName.replace(" ", "_");
                    playAttackSound(attackToPlay);
                }
            }
        });

        // ChangePokemon Button
        Button changePokemonButton = findViewById(R.id.changePokemonButton);
        changePokemonButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!changePokemonButton.isActivated()) {
                    nextTurn(playerPokemonsPased, agentPokemonsPased, 3);
                }
            }
        });

        // exitGame Button
        Button exitGameButton = findViewById(R.id.surrenderButton);
        exitGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!exitGameButton.isActivated()) {
                    nextTurn(playerPokemonsPased, agentPokemonsPased, 4);
                    vibrate(500);
                }
            }
        });

    }

    /*
     * ********************************************************************************************
     * * playAttackSound                                                                          *
     * ********************************************************************************************
     * */
    private void playAttackSound(String attack) {

        int attackSoundId = getResources().getIdentifier(attack, "raw", getPackageName());
        attackSound = MediaPlayer.create(this, attackSoundId);

        if (attackSound != null) {
            // Starting song attack
            attackSound.start();
            attackSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // Liberate resources
                    mp.release();
                }
            });
        }
    }

    /*
     * ********************************************************************************************
     * * playMusic                                                                                *
     * ********************************************************************************************
     * */
    private void playMusic() {

        // Play background music based on the preferences of the user
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean musicState = preferences.getBoolean("music_state", false);

        if (musicState) {
            if (!backGroundMusic.isPlaying()) {
                // Starting music
                backGroundMusic.start();
            }
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
        if (backGroundMusic != null) {
            backGroundMusic.pause();
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

        // Check the status of the music befor start
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean musicState = preferences.getBoolean("music_state", false);

        if (musicState) {
            if (!backGroundMusic.isPlaying()) {
                // Start music
                backGroundMusic.start();
            }
        }
    }

    /*
     * ********************************************************************************************
     * * onDestroy                                                                                  *
     * ********************************************************************************************
     * */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Liberate the resources when the player is distroyed
        if (backGroundMusic != null) {
            backGroundMusic.release();
            backGroundMusic = null;
        }
    }

    /*
     * ********************************************************************************************
     * * nextTurn                                                                                 *
     * ********************************************************************************************
     * */
    public void nextTurn(ArrayList<pokemon> playerPokemons, ArrayList<pokemon> agentPokemons, int playerDecision) {

        // Check the length of the arrays
        if (!(playerPokemons.isEmpty()) && (!agentPokemons.isEmpty())) {

            // Status of the Pokemon
            System.out.println("\n[HP] - PLAYER - " + p1.getName() + ": " + p1.getHealth() + " hp");
            System.out.println("[HP] - AGENT - " + p2.getName() + ": " + p2.getHealth() + " hp");

            switch (chooseFirstMove(p1, p2)) {

                // The Player Pokemon is Faster
                case 0:

                    System.out.println(
                            "\n[START-FIGHT] - Player starts first - SPEED: " + p1.getSpeed() + " > " + p2.getSpeed());

                    // Turn of the player
                    playerTurn(playerPokemons, playerDecision);

                    // Turn of the Agent
                    agentTurn(agentPokemons, playerDecision);

                    break;

                // The Agent Pokemon is Faster
                case 1:

                    System.out.println(
                            "\n[START-FIGHT] - Agent starts first - SPEED: " + p2.getSpeed() + " > " + p1.getSpeed());

                    // Turn of the Agent
                    agentTurn(agentPokemons, playerDecision);

                    // Turn of the player
                    playerTurn(playerPokemons, playerDecision);

                    break;

                default:
                    break;

            }

            if (checkP1Health(playerPokemons) == -2 || checkP2Health(agentPokemons) == -2) {
                // We enter when one of the pokemons is defeated
                chooseWinner();
            }

            // Print the current pokemons in the game
            printPokemonsInGame(playerPokemons, agentPokemons);

        } else {

            // We enter when one of the pokemons is defeated
            chooseWinner();
        }

    }

    /*
     * ********************************************************************************************
     * * playerTurn                                                                               *
     * ********************************************************************************************
     * */
    private void playerTurn(ArrayList<pokemon> playerPokemons, int playerDecision) {

        // *****************************************
        // PLAYER 1 CODE
        // *****************************************

        if (p1.getHealth() > 0) {

            // It chooses surrender
            if (playerDecision == 4) {

                System.out.println("\n[SURRENDER] - PLAYER lose and go out of the activity_Fight.");
                System.out.println("\n[REWARD] - Better luck next time :).");

                // Changes the activity in the correct way
                Intent intent = new Intent(activity_Fight.this, activity_Lose.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

                return;
            }

            // The AI evaluates before the actions of the player
            if (!IAState) {
                move.setScore(ia.evaluate(ia));
                move = ia.minMax(deepTree, ia, true);
            }

            if (playerDecision == 3) {
                // It chooses change pokemon

                ia.setPermitChange(true);

                String text = "Choose one pokemon!";

                if (fightSize == 1) {
                    text = "You can't change a pokemon in 1vs1 match.";

                } else if (playerPokemons.size() == 1) {
                    text = "You don't have more pokemon to change.";

                } else {

                    // Choose Change pokemon
                    Intent intent = new Intent(getApplicationContext(), activity_ChangePokemon.class);
                    intent.putExtra("playerPokemonsKey", playerPokemonsPased);
                    selectAPokemonToChange.launch(intent);
                }

                Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

            } else {
                // Choose Attack

                // Obtains the attackEfectivity of the Atack
                double playerAtackEfective = atackEfective(p1.getType(), p2.getType(),
                        p1.getMovements().get(playerDecision).toString());

                // Obtains the realDamage of the atack
                int realDamagePlayer = updateHP(p2, (int) Math.round(p1.getAtack() * playerAtackEfective), 2);

                // Initializes the text with the damage done
                String text = "[ATACK] - Player made " + realDamagePlayer + " points of damage to "
                        + p2.getName() + " using " + p1.getMovements().get(playerDecision).toString() + ".";

                // To say a notification of the damage done
                if (realDamagePlayer == -1) {
                    // -1 -> Pokemon Defeated
                    text = "[ATACK] - The agent pokemon was defeated!.";

                } else if (realDamagePlayer == -2) {
                    // -2 -> Nobody has life
                    text = "[ERROR] - Take a Screenshot and report to the developers. Thanks.";
                }

                // Say the damage done in the pokemon log
                System.out.println(text);

                // Modifies the text of the player
                TextView text2WriteGUI = findViewById(R.id.PlayerTextLog);
                text2WriteGUI.setText("");
                text2WriteGUI.setText(text);
            }
        }

        // *****************************************
        // PLAYER 1 CODE
        // *****************************************

    }

    /*
     * ********************************************************************************************
     * * agentTurn                                                                               *
     * ********************************************************************************************
     * */
    private void agentTurn(ArrayList<pokemon> agentPokemons, int playerDecision) {

        // *****************************************
        // AGENT CODE
        // *****************************************

        // Handler for do the execution of the agent slowly
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                // If the p2 has more than 0 points of life it chooses its atack
                if (p2.getHealth() > 0 && playerDecision != 3 && playerDecision != 4) {

                    if (IAState) {

                        // *****************************************
                        // RANDOM AGENT CODE
                        // *****************************************

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

                            // Modifies the text of the agent
                            TextView text2WriteGUI = findViewById(R.id.IATextLog);
                            text2WriteGUI.setText("");
                            text2WriteGUI.setText(text);

                            // Updates the GUI
                            updateFightGuiAgent(p2);
                            updateSpecialAttackButton(p1, p2);

                        } else {
                            // Choose Attack

                            // Get the attack, animation and sound
                            String attackName = p2.getMovements().get(agentAction).toString();
                            String attackToPlay = attackName.replace(" ", "_").toLowerCase();
                            playAttackSound(attackToPlay);
                            startAnimationInv();
                            vibrate(200);

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
                                // -1 -> Pokemon Defeated
                                text = "[ATACK] - The player pokemon was defeated!.";

                            } else if (realDamageAgent == -2) {
                                // -2 -> Nobody has life
                                text = "[ERROR] - Take a Screenshot and report to the developers. Thanks.";
                            }

                            // Say the damage done in the pokemon log
                            System.out.println(text);

                            // Modifies the text of the agent
                            TextView text2WriteGUI = findViewById(R.id.IATextLog);
                            text2WriteGUI.setText("");
                            text2WriteGUI.setText(text);

                            // *****************************************
                            // RANDOM AGENT CODE
                            // *****************************************
                        }

                    } else if (!IAState) {

                        // *****************************************
                        // MIN-MAX AGENT CODE
                        // *****************************************

                        if (move.getIsSwitch() && agentPokemons.size() > 1) {

                            // Choose Change
                            String oldPokemonName = p2.getName();

                            // Agent wants to change its pokemon
                            ia.setPermitChange(false);

                            // Changes the pokemon
                            int nextPokemonToChangeIndex = ia.getAgentPokemonChangeIndex();
                            // pokemon nextPokemonAgent = ia.getAgentPokemons().get(nextPokemonToChangeIndex);
                            // System.out.println("Next Pokemon: " + nextPokemonAgent);
                            ia.setAgentPokemonIndex(nextPokemonToChangeIndex);
                            // Obtain new pokemon
                            p2 = agentPokemons.get(nextPokemonToChangeIndex);

                            // To say a notification of the damage done
                            String text = "[CHANGE] - Agent wants to change the pokemon " + oldPokemonName + " -> " + p2.getName() + ".";

                            // Say the damage done in the pokemon log
                            System.out.println(text);

                            // Modifies the text of the agent
                            TextView text2WriteGUI = findViewById(R.id.IATextLog);
                            text2WriteGUI.setText("");
                            text2WriteGUI.setText(text);

                            // Update the GUI
                            updateFightGuiAgent(p2);
                            updateSpecialAttackButton(p1, p2);

                        } else {
                            // Choose Attack

                            // Get the attack, animation and sound
                            String attackName = p2.getMovements().get(move.getAttack()).toString();
                            String attackToPlay = attackName.replace(" ", "_").toLowerCase();
                            playAttackSound(attackToPlay);
                            startAnimationInv();
                            vibrate(200);

                            // Obtains the attackEfectivity of the Atack
                            double agentAtackEfective = atackEfective(p2.getType(), p1.getType(),
                                    attackName);

                            // Updates the current life of the pokemons
                            int realDamageAgent = updateHP(p1, (int) Math.round(p2.getAtack() * agentAtackEfective), 1);

                            // To say a notification of the damage done
                            String text = "[ATACK] - Agent made " + realDamageAgent + " points of damage to "
                                    + p1.getName() + " using " + attackName + ".";

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

                            // Modifies the text of the agent
                            TextView text2WriteGUI = findViewById(R.id.IATextLog);
                            text2WriteGUI.setText("");
                            text2WriteGUI.setText(text);
                        }

                    }

                    // *****************************************
                    // MIN-MAX AGENT CODE
                    // *****************************************

                }
            }
        }, animDelay);

        // *****************************************
        // AGENT CODE
        // *****************************************

    }

    /*
     * ********************************************************************************************
     * * checkP1Health                                                                            *
     * ********************************************************************************************
     * */
    private int checkP1Health(ArrayList<pokemon> playerPokemons) {

        // The player pokemon has 0 hp
        if (p1.getHealth() <= 0 && playerPokemons.size() > 0) {

            // Obtain the index of the pokemon and add points to agent
            int pokePos = playerPokemons.indexOf(p1);
            agentMarks++;

            // Delete the pokemon from the arraylist
            playerPokemons.remove(pokePos);
            playerPokemonsPased = playerPokemons;

            String text = "[GO-OUT] - Player Pokemon " + p1.getName() + " go out of the battle.";
            System.out.println(text);
            TextView text2WriteGUI = findViewById(R.id.PlayerTextLog);
            text2WriteGUI.setText("");
            text2WriteGUI.setText(text);

            // We obtain a new pokemon
            if (!playerPokemons.isEmpty()) {

                // Set the new pokemon to the ai
                if (!IAState) {
                    ia.setPermitChange(true);
                    ia.setPlayerPokemons(playerPokemons);
                }

                // Chose the new pokemon
                Intent intent = new Intent(getApplicationContext(), activity_ChangePokemon.class);
                intent.putExtra("playerPokemonsKey", playerPokemonsPased);
                selectAPokemonToChange.launch(intent);

                Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

                // Changed on pokemon
                return -1;

            } else {

                // All the pokemons are defeated
                return -2;
            }
        }
        // No changes
        return 0;
    }

    /*
     * ********************************************************************************************
     * * checkP2Health                                                                            *
     * ********************************************************************************************
     * */
    private int checkP2Health(ArrayList<pokemon> agentPokemons) {

        // The agent pokemon has 0 hp or less
        if (p2.getHealth() <= 0 && agentPokemons.size() > 0) {

            // Obtain the index of the pokemon and add points to player
            int pokePos = agentPokemons.indexOf(p2);
            playerMarks++;

            // Delete the pokemon from the arraylist
            agentPokemons.remove(pokePos);
            agentPokemonsPased = agentPokemons;

            // We delete the tired pokemon from the array
            String text = "[GO-OUT] - Agent Pokemon " + p2.getName() + " go out of the battle.";
            System.out.println(text);
            TextView text2WriteGUI = findViewById(R.id.IATextLog);
            text2WriteGUI.setText("");
            text2WriteGUI.setText(text);

            // We obtain a new pokemon
            if (!agentPokemons.isEmpty()) {

                // Set the new pokemon to the ai
                if (!IAState) {
                    ia.setPermitChange(true);
                    ia.setAgentPokemons(agentPokemons);
                }

                // Obtain the next pokemon on the array and set it to the ai
                p2 = agentPokemons.get(0);
                if (!IAState) {
                    ia.setAgentPokemonIndex(0);
                }

                System.out.println(
                        "\n[GO-INT] - Agent Pokemon chooses " + p2.getName()
                                + " as the new pokemon.");

                // Update the GUI
                updateFightGuiAgent(p2);
                updateSpecialAttackButton(p1, p2);

                // Changed on pokemon
                return -1;

            } else {

                // All the pokemons are defeated
                return -2;
            }
        }

        // No changes
        return 0;
    }

    /*
     * ********************************************************************************************
     * * chooseWinner                                                                             *
     * ********************************************************************************************
     * */
    private void chooseWinner() {

        // Choose the winner based on the marks of the pokemon defeated
        if (playerMarks > agentMarks) {

            System.out.println("\n[GO-OUT] - AGENT Pokemon go out of the activity_Fight.");
            System.out.println("\n[REWARD] - Congrats you recibe a GYM medal!.");

            // Change the activity in the correct way
            Intent intent = new Intent(activity_Fight.this, activity_Win.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        } else {

            System.out.println("\n[GO-OUT] - PLAYER Pokemon go out of the activity_Fight.");
            System.out.println("\n[REWARD] - Better luck next time :).");

            // Change the activity in the correct way
            Intent intent = new Intent(activity_Fight.this, activity_Lose.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        }


    }

    /*
     * ********************************************************************************************
     * * printPokemonsInGame                                                                      *
     * ********************************************************************************************
     * */
    private void printPokemonsInGame(ArrayList<pokemon> playerPokemons, ArrayList<pokemon> agentPokemons) {

        int numberOfPokeballs = 6;

        // Put all the Pokeballs of the player and the agent
        for (int i = 1; i <= numberOfPokeballs; i++) {
            String pokeballID = "pokeballP" + i;
            int resID = getResources().getIdentifier(pokeballID, "id", getPackageName());
            ImageView pokeball = findViewById(resID);
            pokeball.setImageResource(R.drawable.pokeball);
        }
        for (int i = 1; i <= numberOfPokeballs; i++) {
            String pokeballID = "pokeballIA" + i;
            int resID = getResources().getIdentifier(pokeballID, "id", getPackageName());
            ImageView pokeball = findViewById(resID);
            // Cambia la imagen usando el recurso drawable
            pokeball.setImageResource(R.drawable.pokeball);
        }

        // See what pokemons currently in game and change the picture
        int i = 0;
        System.out.print("\n[P1-POKEMONS] - Player Pokemons => [ ");
        for (pokemon pokemonInList : playerPokemons) {
            // Get HP Points
            System.out.print(pokemonInList.getName() + " [HP]: " + (pokemonInList.getHealth()) + ", ");
            // Change the img if the pokemon exist in the arraylist
            i++;
            String pokeballID = "pokeballP" + i;
            int resID = getResources().getIdentifier(pokeballID, "id", getPackageName());
            ImageView pokeball = findViewById(resID);
            String pokemonName = pokemonInList.getName().toLowerCase();
            int drawableID = getResources().getIdentifier(pokemonName, "drawable", getPackageName());
            pokeball.setImageResource(drawableID);
        }
        System.out.print("]\n");

        i = 0;
        // See what pokemons currently in game and change the picture
        System.out.print("\n[P2-POKEMONS] - Agent Pokemons => [ ");
        for (pokemon pokemonInList : agentPokemons) {
            // Get HP Points
            System.out.print(pokemonInList.getName() + "  [HP]: " + (pokemonInList.getHealth()) + ", ");
            // Change the img if the pokemon exist in the arraylist
            i++;
            String pokeballID = "pokeballIA" + i;
            int resID = getResources().getIdentifier(pokeballID, "id", getPackageName());
            ImageView pokeball = findViewById(resID);
            String pokemonName = pokemonInList.getName().toLowerCase();
            int drawableID = getResources().getIdentifier(pokemonName, "drawable", getPackageName());
            pokeball.setImageResource(drawableID);
        }
        System.out.print("]\n");

    }

    /*
     * ********************************************************************************************
     * * chooseFirstMove                                                                          *
     * ********************************************************************************************
     * */
    private static int chooseFirstMove(pokemon p1, pokemon p2) {

        // Choose the player with more speed

        // p1 > p2
        if (p1.getSpeed() > p2.getSpeed()) {
            return 0;

            // p2 > p1
        } else if (p2.getSpeed() > p1.getSpeed()) {
            return 1;

            // p1 == p2
        } else {

            // Random start
            Random azar = new Random();
            return azar.nextInt(2);
        }
    }

    /*
     * ********************************************************************************************
     * * updateHP                                                                                 *
     * ********************************************************************************************
     * */
    private int updateHP(pokemon p1, int attack, int player) {

        // 30% of defense reduction atack
        double defenseFactor = 0.03;
        int defense = (int) Math.round(p1.getDefense() * defenseFactor);
        int newLife = p1.getHealth() - attack + defense;
        p1.setHealth(newLife);
        int currentHealth = p1.getHealth();
        int maxHealth = p1.getMaxHealth();

        // Update the progress bar of life
        ProgressBar yourPokemonBarHealth = (ProgressBar) findViewById(R.id.YourPokemonHealthBar);
        ProgressBar IAPokemonBarHealth = (ProgressBar) findViewById(R.id.IAPokemonHealthBar);

        if (player == 1) {
            if (currentHealth > 0) {

                // If not lees then the middle -> GREEN
                this.playerHpTextView.setText("HP: " + currentHealth + " / " + maxHealth);
                yourPokemonBarHealth.setMax(maxHealth); // Establece la vida máxima
                yourPokemonBarHealth.setProgress(currentHealth); // Establece la vida actual
                yourPokemonBarHealth.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN); // Establece el color inicial de la barra de vida
                playerHpTextView.setTextColor(ContextCompat.getColor(this, R.color.LightGreen));

                if (currentHealth < (maxHealth * 0.3)) {

                    // Lees than 30% -> LightRed
                    this.playerHpTextView.setTextColor(ContextCompat.getColor(this, R.color.LightRed));
                    yourPokemonBarHealth.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

                } else if (currentHealth < (maxHealth * 0.5)) {

                    // Lees than 50% -> LightYellow
                    this.playerHpTextView.setTextColor(ContextCompat.getColor(this, R.color.LightYellow));
                    yourPokemonBarHealth.getProgressDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
                }

                return (attack - defense);

            } else {

                // Lees than 0% -> RED
                p1.setHealth(0);
                this.playerHpTextView.setText("HP: 0");
                this.playerHpTextView.setTextColor(ContextCompat.getColor(this, R.color.Red));
                yourPokemonBarHealth.setMax(maxHealth); // Establece la vida máxima
                yourPokemonBarHealth.setProgress(0); // Establece la vida actual
                yourPokemonBarHealth.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

                return -1;

            }
        } else if (player == 2) {
            if (currentHealth > 0) {

                // If not lees then the middle -> GREEN
                this.agentHpTextView.setText("HP: " + currentHealth + " / " + maxHealth);
                IAPokemonBarHealth.setMax(maxHealth); // Establece la vida máxima
                IAPokemonBarHealth.setProgress(currentHealth); // Establece la vida actual
                IAPokemonBarHealth.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                playerHpTextView.setTextColor(ContextCompat.getColor(this, R.color.LightGreen));

                if (currentHealth < (maxHealth * 0.3)) {

                    // Lees than 30% -> LightRed
                    IAPokemonBarHealth.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                    this.agentHpTextView.setTextColor(ContextCompat.getColor(this, R.color.LightRed));

                } else if (currentHealth < (maxHealth * 0.5)) {

                    // Lees than 50% -> LightYellow
                    this.agentHpTextView.setTextColor(ContextCompat.getColor(this, R.color.LightYellow));
                    IAPokemonBarHealth.getProgressDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
                }

                return (attack - defense);

            } else {

                // Lees than 0% -> RED
                p2.setHealth(0);
                this.agentHpTextView.setText("HP: 0");
                this.agentHpTextView.setTextColor(ContextCompat.getColor(this, R.color.Red));
                IAPokemonBarHealth.setMax(maxHealth);
                IAPokemonBarHealth.setProgress(0);
                IAPokemonBarHealth.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

                return -1;

            }
        }

        return -2;
    }

    /*
     * ********************************************************************************************
     * * atackEfective                                                                            *
     * ********************************************************************************************
     * */
    public static double atackEfective(String type1, String type2, String attack) {

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
            // In other case the attack don't do damage
        } else {
            return 0;
        }
    }

    /*
     * ********************************************************************************************
     * * updateSpecialAttackButton                                                                *
     * ********************************************************************************************
     * */
    private void updateSpecialAttackButton(pokemon p1, pokemon p2) {

        // Set the help effective on the special attack button
        Button specialAttackButton = findViewById(R.id.specialAttackButton);
        double effect = atackEfective(p1.getType(), p2.getType(), "null");
        specialAttackButton.setText(p1.getMovements().get(1).toString().toUpperCase() + "\n x " + effect * 10);
    }

    /*
     * ********************************************************************************************
     * * updateFightGuiPlayer                                                                     *
     * ********************************************************************************************
     * */
    private void updateFightGuiPlayer(pokemon playerPokemon) {

        // Values for update the player
        String pokemonPlayerName = playerPokemon.getName();
        int pokemonPlayerHealth = playerPokemon.getHealth();
        int pokemonPlayerMaxHealth = playerPokemon.getMaxHealth();
        String pokemonPlayerType = playerPokemon.getType();

        // Buttons that we will update
        Button specialAttackButton = findViewById(R.id.specialAttackButton);

        // Update the name of the pokemon
        textPokemonPlayerName = findViewById(R.id.textPokemonPlayerName);
        this.textPokemonPlayerName.setText(pokemonPlayerName);

        // Update the progressBar
        ProgressBar yourPokemonBarHealth = (ProgressBar) findViewById(R.id.YourPokemonHealthBar);

        // More than 50% of life -> Green
        playerHpTextView = findViewById(R.id.textPlayerHealth);
        playerHpTextView.setText("HP: " + pokemonPlayerHealth + " / " + pokemonPlayerMaxHealth);
        yourPokemonBarHealth.setMax(pokemonPlayerMaxHealth);
        yourPokemonBarHealth.setProgress(pokemonPlayerHealth);
        yourPokemonBarHealth.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
        playerHpTextView.setTextColor(ContextCompat.getColor(this, R.color.LightGreen));

        if (pokemonPlayerHealth < (pokemonPlayerMaxHealth * 0.3)) {

            // Less than 30% of life -> LightRed
            playerHpTextView.setTextColor(ContextCompat.getColor(this, R.color.LightRed));
            yourPokemonBarHealth.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

        } else if (pokemonPlayerHealth < (pokemonPlayerMaxHealth * 0.5)) {

            // Less than 50% of life -> LightYellow
            playerHpTextView.setTextColor(ContextCompat.getColor(this, R.color.LightYellow));
            yourPokemonBarHealth.getProgressDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
        }

        // Change the color of the special button depending of the type and the type img
        typePokemonPlayerIMG = findViewById(R.id.typePokemonPlayerIMG);

        if (pokemonPlayerType.equals("fire")) {
            specialAttackButton.setBackgroundColor(ContextCompat.getColor(this, R.color.LightRed));
            typePokemonPlayerIMG.setImageResource(getResources().getIdentifier("type_fire", "drawable", getPackageName()));
        } else if (pokemonPlayerType.equals("water")) {
            specialAttackButton.setBackgroundColor(ContextCompat.getColor(this, R.color.LightBlue));
            typePokemonPlayerIMG.setImageResource(getResources().getIdentifier("type_water", "drawable", getPackageName()));
        } else if (pokemonPlayerType.equals("plant")) {
            specialAttackButton.setBackgroundColor(ContextCompat.getColor(this, R.color.LightGreen));
            typePokemonPlayerIMG.setImageResource(getResources().getIdentifier("type_plant", "drawable", getPackageName()));
        }

        // Update the image of the pokemon
        pokemonPlayerImg = findViewById(R.id.imagePlayerPokemon);
        fadeIn(pokemonPlayerImg);
        this.pokemonPlayerImg.setImageResource(getResources().getIdentifier(pokemonPlayerName.toLowerCase(), "drawable", getPackageName()));
    }

    /*
     * ********************************************************************************************
     * * updateFightGuiAgent                                                                      *
     * ********************************************************************************************
     * */
    private void updateFightGuiAgent(pokemon agentPokemon) {

        // Values for update the agent
        String pokemonAgentName = agentPokemon.getName();
        int pokemonAgentHealth = agentPokemon.getHealth();
        int pokemonAgentMaxHealth = agentPokemon.getMaxHealth();
        String pokemonAgentType = agentPokemon.getType();

        // Update the name of the pokemon
        textPokemonAgentName = findViewById(R.id.textPokemonAgentName);
        this.textPokemonAgentName.setText(pokemonAgentName);

        // Update the progressBar
        ProgressBar IAPokemonBarHealth = (ProgressBar) findViewById(R.id.IAPokemonHealthBar);

        // More than 50% of life -> GREEN
        agentHpTextView = findViewById(R.id.textAgentHealth);
        agentHpTextView.setText("HP: " + pokemonAgentHealth + " / " + pokemonAgentMaxHealth);
        IAPokemonBarHealth.setMax(pokemonAgentMaxHealth);
        IAPokemonBarHealth.setProgress(pokemonAgentHealth);
        IAPokemonBarHealth.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
        agentHpTextView.setTextColor(ContextCompat.getColor(this, R.color.LightGreen));

        if (pokemonAgentHealth < (pokemonAgentMaxHealth * 0.3)) {

            // Less than 30% of life -> LightRed
            IAPokemonBarHealth.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            agentHpTextView.setTextColor(ContextCompat.getColor(this, R.color.LightRed));

        } else if (pokemonAgentHealth < (pokemonAgentMaxHealth * 0.5)) {

            // Less than 50% of life -> LightYellow
            agentHpTextView.setTextColor(ContextCompat.getColor(this, R.color.LightYellow));
            IAPokemonBarHealth.getProgressDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
        }

        // Change the color of the special button depending of the type and the type img
        typePokemonAgentIMG = findViewById(R.id.typePokemonAgentIMG);

        if (pokemonAgentType.equals("fire")) {
            typePokemonAgentIMG.setImageResource(getResources().getIdentifier("type_fire", "drawable", getPackageName()));
        } else if (pokemonAgentType.equals("water")) {
            typePokemonAgentIMG.setImageResource(getResources().getIdentifier("type_water", "drawable", getPackageName()));
        } else if (pokemonAgentType.equals("plant")) {
            typePokemonAgentIMG.setImageResource(getResources().getIdentifier("type_plant", "drawable", getPackageName()));
        }

        // Update the image of the pokemon
        pokemonAgentImg = findViewById(R.id.imageAgentPokemon);
        fadeIn(pokemonAgentImg);
        this.pokemonAgentImg.setImageResource(getResources().getIdentifier(pokemonAgentName.toLowerCase(), "drawable", getPackageName()));
    }

    /*
     * ********************************************************************************************
     * * selectAPokemonToChange                                                                   *
     * ********************************************************************************************
     * */
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

                        // Para que la IA sepa que pokemon esta usando el jugador
                        if (!IAState) {
                            // We set all the pokemons to the min-max agent
                            ia.setPlayerPokemonIndex(pokemonPosition);
                        }
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

    /*
     * ********************************************************************************************
     * * vibrate                                                                                  *
     * ********************************************************************************************
     * */
    private void vibrate(int vibrateTime) {

        // Access to the vibrator engine of the phone
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Check if the device is compatible or has vibrator
        if (vibrator == null || !vibrator.hasVibrator()) {
            return;
        }

        // Check if the device is compatible for API 26 and later
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(vibrateTime, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            // Previous API 26 versions
            vibrator.vibrate(vibrateTime);
        }
    }

    /*
     * ********************************************************************************************
     * * startAnimation                                                                           *
     * ********************************************************************************************
     * */
    private void startAnimation() {
        // Animation of shake in right
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake_animation);
        pokemonPlayerImg.startAnimation(shake);
    }

    /*
     * ********************************************************************************************
     * * startAnimationInv                                                                        *
     * ********************************************************************************************
     * */
    private void startAnimationInv() {
        // Animation of shake inverse
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake_animation_inv);
        pokemonAgentImg.startAnimation(shake);
    }

    /*
     * ********************************************************************************************
     * * fadeIn                                                                                   *
     * ********************************************************************************************
     * */
    private void fadeIn(ImageView imgChange) {
        // FadeIn when change a pokemon
        int duration = 1000; //ms
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(duration);
        imgChange.setVisibility(View.VISIBLE);
        imgChange.startAnimation(alphaAnimation);
    }
}