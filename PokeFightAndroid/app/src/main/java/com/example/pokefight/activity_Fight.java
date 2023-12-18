package com.example.pokefight;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.media.MediaPlayer;

import java.util.*;

public class activity_Fight extends AppCompatActivity {

    static int MAX = 1000;
    static int MIN = -1000;
    static int fullHealthPlayerPokemon, fullHealthAgentPokemon, agentMarks, playerMarks;

    //static int playerAction = -1;
    TextView playerHpTextView, agentHpTextView, textPokemonPlayerName, textPokemonAgentName;
    ImageView pokemonPlayerImg, pokemonAgentImg, typePokemonPlayerIMG, typePokemonAgentIMG;

    public static ArrayList<pokemon> agentPokemonsPased = new ArrayList<pokemon>();
    public static ArrayList<pokemon> playerPokemonsPased = new ArrayList<pokemon>();

    private MediaPlayer mediaPlayer;

    @SuppressLint("ResourceAsColor")
    @Override
    public void onCreate(Bundle savedInstanceState) {

        // Inicializa el MediaPlayer con el archivo de música
        mediaPlayer = MediaPlayer.create(this, R.raw.pokemon_music);

        // Configura el bucle
        mediaPlayer.setLooping(true);
        playMusic();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight);

        // Inicializo las variables de puntos a 0
        playerMarks = 0;
        agentMarks = 0;

        // Recojo los argumentos que han llegado aqui
        playerPokemonsPased = (ArrayList<pokemon>) getIntent().getSerializableExtra("playerPokemonsKey");
        agentPokemonsPased = (ArrayList<pokemon>) getIntent().getSerializableExtra("agentPokemonsKey");

        System.out.println(agentPokemonsPased + "\n");
        System.out.println(playerPokemonsPased + "\n");

        Button specialAttackButton = findViewById(R.id.specialAttackButton);
        Button changePokemonButton = findViewById(R.id.changePokemonButton);
        Button exitGameButton = findViewById(R.id.surrenderButton);
        Button tackleButton = findViewById(R.id.tackleButton);

        // Actualizo el nombre de los Pokemons
        textPokemonPlayerName = findViewById(R.id.textPokemonPlayerName);
        textPokemonAgentName = findViewById(R.id.textPokemonAgentName);
        this.textPokemonPlayerName.setText(playerPokemonsPased.get(0).getName());
        this.textPokemonAgentName.setText(agentPokemonsPased.get(0).getName());


        // Vida total de ambos pokemons
        fullHealthPlayerPokemon = playerPokemonsPased.get(0).getHealth();
        fullHealthAgentPokemon = agentPokemonsPased.get(0).getHealth();

        // Texto de vida
        playerHpTextView = findViewById(R.id.textPlayerHealth);
        agentHpTextView = findViewById(R.id.textAgentHealth);

        this.playerHpTextView.setText("HP: " + playerPokemonsPased.get(0).getHealth());
        this.agentHpTextView.setText("HP: " + agentPokemonsPased.get(0).getHealth());

        // Cambio el color de fondo y texto del ataque especial
        String pokeType = playerPokemonsPased.get(0).getType();
        String pokeSpecialAttack = (String) playerPokemonsPased.get(0).getMovements().get(1);
        specialAttackButton.setText(pokeSpecialAttack.toUpperCase());
        specialAttackButton.setTextColor(ContextCompat.getColor(this, R.color.Black));
        // Tipo del pokemon del jugador
        typePokemonPlayerIMG = findViewById(R.id.typePokemonPlayerIMG);
        typePokemonAgentIMG = findViewById(R.id.typePokemonAgentIMG);

        // Cambia el color del boton de ataque especial dependiendo del tipo de ataque
        if (pokeType.equals("fire")) {
            //specialAttackButton.setTextColor(ContextCompat.getColor(this, R.color.Red));
            specialAttackButton.setBackgroundColor(ContextCompat.getColor(this, R.color.LightRed));
            typePokemonPlayerIMG.setImageResource(getResources().getIdentifier("type_fire", "drawable", getPackageName()));
        } else if (pokeType.equals("water")) {
            //specialAttackButton.setTextColor(ContextCompat.getColor(this, R.color.Blue));
            specialAttackButton.setBackgroundColor(ContextCompat.getColor(this, R.color.LightBlue));
            typePokemonPlayerIMG.setImageResource(getResources().getIdentifier("type_water", "drawable", getPackageName()));
        } else if (pokeType.equals("plant")) {
            //specialAttackButton.setTextColor(ContextCompat.getColor(this, R.color.Green));
            specialAttackButton.setBackgroundColor(ContextCompat.getColor(this, R.color.LightGreen));
            typePokemonPlayerIMG.setImageResource(getResources().getIdentifier("type_plant", "drawable", getPackageName()));
        }

        // Cambiamos el tipo del pokemon que se ve en pantalla
        String pokeTypeAgent = agentPokemonsPased.get(0).getType();
        if (pokeTypeAgent.equals("fire")) {
            typePokemonAgentIMG.setImageResource(getResources().getIdentifier("type_fire", "drawable", getPackageName()));
        } else if (pokeTypeAgent.equals("water")) {
            typePokemonAgentIMG.setImageResource(getResources().getIdentifier("type_water", "drawable", getPackageName()));
        } else if (pokeTypeAgent.equals("plant")) {
            typePokemonAgentIMG.setImageResource(getResources().getIdentifier("type_plant", "drawable", getPackageName()));
        }

        // TODO - Esto es valido solo para 1 VS 1, si se cambia el pokemon habria que actualizarlo

        // Imagen de los pokemon
        pokemonPlayerImg = findViewById(R.id.imagePlayerPokemon);
        pokemonAgentImg = findViewById(R.id.imageAgentPokemon);

        this.pokemonPlayerImg.setImageResource(getResources().getIdentifier(playerPokemonsPased.get(0).getName().toLowerCase(), "drawable", getPackageName()));
        this.pokemonAgentImg.setImageResource(getResources().getIdentifier(agentPokemonsPased.get(0).getName().toLowerCase(), "drawable", getPackageName()));

        tackleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //playerAction = 0;
                nextTurn(playerPokemonsPased, agentPokemonsPased, 0);
            }
        });

        specialAttackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //playerAction = 1;
                nextTurn(playerPokemonsPased, agentPokemonsPased, 1);
            }
        });

        exitGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //playerAction = 4;
                nextTurn(playerPokemonsPased, agentPokemonsPased, 4);
            }
        });

        if (findViewById(R.id.fragment_container_IATEAM) != null || findViewById(R.id.fragment_container_Player) != null) {
            if (savedInstanceState != null) {
                return;
            }

            TEAM1VS1 fragmentoIATEAM = new TEAM1VS1();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container_IATEAM, fragmentoIATEAM).commit();
            TEAM1VS1 fragmentoYOURTEAM = new TEAM1VS1();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container_Player, fragmentoYOURTEAM).commit();
        }

        // TODO: llamar a fightNvsN
        //fightNvsN(playerPokemonsPased, agentPokemonsPased, 1);
    }

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

    public int nextTurn(ArrayList<pokemon> playerPokemons, ArrayList<pokemon> agentPokemons, int playerDecision) {

        // We choose the first two pokemons of the players
        pokemon p1 = playerPokemons.get(0);
        pokemon p2 = agentPokemons.get(0);

        switch (chooseFirstMove(p1, p2)) {

            // The Player Pokemon is Faster
            case 0:

                System.out.println(
                        "\n[START-FIGHT] - Player starts first - SPEED: " + p1.getSpeed() + " > " + p2.getSpeed());

                // Status of the Pokemon
                System.out.println("\n[HP] - PLAYER - " + p1.getName() + ": " + p1.getHealth() + " hp");
                System.out.println("[HP] - AGENT - " + p2.getName() + ": " + p2.getHealth() + " hp");


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

                    // Say what atack chooses the player
                    System.out.println(
                            "\n[ATACK] - Player Select '" + p1.getMovements().get(playerDecision).toString()
                                    + "'");

                    // Obtains the attackEfectivity of the Atack
                    double playerAtackEfective = atackEfective(p1.getType(), p2.getType(),
                            p1.getMovements().get(playerDecision).toString());

                    // Obtains the realDamage of the atack
                    int realDamagePlayer = updateHP(p2, (int) Math.round(p1.getAtack() * playerAtackEfective), 2);

                    // Say the damage done in the pokemon
                    System.out.println("[ATACK] - Player made " + realDamagePlayer + " points of damage to "
                            + p2.getName() + ".");
                }

                // If the p2 has more than 0 points of life it chooses its atack
                if (p2.getHealth() > 0) {

                    // *****************************************
                    // RANDOM AGENT CODE
                    // *****************************************

                    // Agent Select its action
                    Random azar = new Random();
                    int agentAction = 0;

                    int battleType = 1;
                    if (battleType == 1) {
                        // 0 - Tackle, 1 - Attack of the Pokemon
                        agentAction = azar.nextInt(2);

                    }

                    // *****************************************
                    // RANDOM AGENT CODE
                    // *****************************************

                    // Say what atack chooses the agent
                    System.out.println(
                            "\n[ATACK] - Agent Select '" + p2.getMovements().get(agentAction).toString()
                                    + "'");

                    // Obtains the attackEfectivity of the Atack
                    double agentAtackEfective = atackEfective(p2.getType(), p1.getType(),
                            p2.getMovements().get(agentAction).toString());

                    // Updates the current life of the pokemons
                    int realDamageAgent = updateHP(p1, (int) Math.round(p2.getAtack() * agentAtackEfective), 1);

                    // Says what it the damage made in the pokemons
                    System.out.println("[ATACK] - Agent made " + realDamageAgent + " points of damage to "
                            + p1.getName() + ".");

                }

                // One of the pokemons has 0 hp soo we delete if from the array

                // The agent pokemon has 0 hp or less
                if (p2.getHealth() <= 0) {

                    int pokePos = agentPokemons.indexOf(p2);
                    playerMarks++;

                    // We delete the tired pokemon from the array
                    System.out.println(
                            "\n[GO-OUT] - Agent Pokemon " + p2.getName()
                                    + " go out of the activity_Fight.");
                    agentPokemons.remove(pokePos);

                    // We obtain a new pokemon
                    if (!agentPokemons.isEmpty()) {
                        p2 = null;
                        p2 = agentPokemons.get(0);
                        System.out.println(
                                "\n[GO-INT] - Agent Pokemon chooses " + p2.getName()
                                        + " as the new pokemon.");
                    }

                }

                // The player pokemon has 0 hp
                if (p1.getHealth() <= 0) {

                    int pokePos = playerPokemons.indexOf(p1);
                    agentMarks++;

                    // We delete the tired pokemon from the array
                    System.out.println(
                            "\n[GO-OUT] - Player Pokemon " + p1.getName()
                                    + " go out of the activity_Fight.");
                    playerPokemons.remove(pokePos);

                    // We obtain a new pokemon
                    if (!playerPokemons.isEmpty()) {
                        p1 = null;
                        p1 = playerPokemons.get(0);
                        System.out.println(
                                "\n[GO-INT] - Player Pokemon chooses " + p1.getName()
                                        + " as the new pokemon.");
                    }

                }

                // Print the current pokemons in the game
                printPokemonsInGame(playerPokemons, agentPokemons);

                break;
            case 1:

                System.out.println(
                        "\n[START-FIGHT] - Player starts first - SPEED: " + p1.getSpeed() + " > " + p2.getSpeed());

                // Status of the Pokemon
                System.out.println("\n[HP] - PLAYER - " + p1.getName() + ": " + p1.getHealth() + " hp");
                System.out.println("[HP] - AGENT - " + p2.getName() + ": " + p2.getHealth() + " hp");

                // If the p2 has more than 0 points of life it chooses its atack
                if (p2.getHealth() > 0) {

                    // *****************************************
                    // RANDOM AGENT CODE
                    // *****************************************

                    // Agent Select its action
                    Random azar = new Random();
                    int agentAction = 0;

                    int battleType = 1;
                    if (battleType == 1) {
                        // 0 - Tackle, 1 - Attack of the Pokemon
                        agentAction = azar.nextInt(2);

                    }

                    // *****************************************
                    // RANDOM AGENT CODE
                    // *****************************************

                    // Say what atack chooses the agent
                    System.out.println(
                            "\n[ATACK] - Agent Select '" + p2.getMovements().get(agentAction).toString()
                                    + "'");

                    // Obtains the attackEfectivity of the Atack
                    double agentAtackEfective = atackEfective(p2.getType(), p1.getType(),
                            p2.getMovements().get(agentAction).toString());

                    // Updates the current life of the pokemons
                    int realDamageAgent = updateHP(p1, (int) Math.round(p2.getAtack() * agentAtackEfective), 1);

                    // Says what it the damage made in the pokemons
                    System.out.println("[ATACK] - Agent made " + realDamageAgent + " points of damage to "
                            + p1.getName() + ".");

                }

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

                    // Say what atack chooses the player
                    System.out.println(
                            "\n[ATACK] - Player Select '" + p1.getMovements().get(playerDecision).toString()
                                    + "'");

                    // Obtains the attackEfectivity of the Atack
                    double playerAtackEfective = atackEfective(p1.getType(), p2.getType(),
                            p1.getMovements().get(playerDecision).toString());

                    // Obtains the realDamage of the atack
                    int realDamagePlayer = updateHP(p2, (int) Math.round(p1.getAtack() * playerAtackEfective), 2);

                    // Say the damage done in the pokemon
                    System.out.println("[ATACK] - Player made " + realDamagePlayer + " points of damage to "
                            + p2.getName() + ".");
                }

                // One of the pokemons has 0 hp soo we delete if from the array

                // The agent pokemon has 0 hp or less
                if (p2.getHealth() <= 0) {

                    int pokePos = agentPokemons.indexOf(p2);
                    playerMarks++;

                    // We delete the tired pokemon from the array
                    System.out.println(
                            "\n[GO-OUT] - Agent Pokemon " + p2.getName()
                                    + " go out of the activity_Fight.");
                    agentPokemons.remove(pokePos);

                    // We obtain a new pokemon
                    if (!agentPokemons.isEmpty()) {
                        p2 = null;
                        p2 = agentPokemons.get(0);
                        System.out.println(
                                "\n[GO-INT] - Agent Pokemon chooses " + p2.getName()
                                        + " as the new pokemon.");
                    }

                }

                // The player pokemon has 0 hp
                if (p1.getHealth() <= 0) {

                    int pokePos = playerPokemons.indexOf(p1);
                    agentMarks++;

                    // We delete the tired pokemon from the array
                    System.out.println(
                            "\n[GO-OUT] - Player Pokemon " + p1.getName()
                                    + " go out of the activity_Fight.");
                    playerPokemons.remove(pokePos);

                    // We obtain a new pokemon
                    if (!playerPokemons.isEmpty()) {
                        p1 = null;
                        p1 = playerPokemons.get(0);
                        System.out.println(
                                "\n[GO-INT] - Player Pokemon chooses " + p1.getName()
                                        + " as the new pokemon.");
                    }

                }

                // Print the current pokemons in the game
                printPokemonsInGame(playerPokemons, agentPokemons);

                break;
            default:
                break;
        }

        // TODO - Esto solo vale si el juego es 1 vs 1, en otros casos habria que retocarlo

        if (p1.getHealth() <= 0 || p2.getHealth() <= 0) {

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

                return 1;

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

                return 2;
            }

        }
        return 0;

    }

    private static void printPokemonsInGame(ArrayList<pokemon> playerPokemons, ArrayList<pokemon> agentPokemons) {

        // See all pokemons currently in game
        System.out.print("\n[P1-POKEMONS] - Player Pokemons => [ ");
        for (pokemon pokemonInList : playerPokemons) {
            System.out.print(pokemonInList.getName() + " [HP]: " + (pokemonInList.getHealth()) + ", ");
        }
        System.out.print("]\n");

        // See all pokemons selected
        System.out.print("\n[P2-POKEMONS] - Agent Pokemons => [ ");
        for (pokemon pokemonInList : agentPokemons) {
            System.out.print(pokemonInList.getName() + "  [HP]: " + (pokemonInList.getHealth()) + ", ");
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
    @SuppressLint("ResourceAsColor")
    private int updateHP(pokemon p1, int atack, int player) {

        // 30% of defense reduction atack
        double defenseFactor = 0.03;
        int defense = (int) Math.round(p1.getDefense() * defenseFactor);
        int newLife = p1.getHealth() - atack + defense;
        p1.setHealth(newLife);

        int health = p1.getHealth();
        if (player == 1) {
            if (health > 0) {
                if (health < (fullHealthPlayerPokemon * 0.3)) {

                    // Menos del 30% cambiamos el color del texto a LightRed
                    this.playerHpTextView.setTextColor(ContextCompat.getColor(this, R.color.LightRed));

                } else if (health < (fullHealthPlayerPokemon * 0.5)) {

                    // Menos del 50% cambiamos el color del texto a LightYellow
                    this.playerHpTextView.setTextColor(ContextCompat.getColor(this, R.color.LightYellow));
                }

                // Si no es menos de la mitad se queda en verde
                this.playerHpTextView.setText("HP: " + health);

                return (atack - defense);

            } else {

                this.playerHpTextView.setText("HP: 0");
                this.playerHpTextView.setTextColor(ContextCompat.getColor(this, R.color.Red));
                return 0;

            }
        } else if (player == 2) {
            if (health > 0) {

                if (health < (fullHealthAgentPokemon * 0.3)) {

                    // Menos del 30% cambiamos el color del texto a LightRed
                    this.agentHpTextView.setTextColor(ContextCompat.getColor(this, R.color.LightRed));
                } else if (health < (fullHealthAgentPokemon * 0.5)) {

                    // Menos del 50% cambiamos el color del texto a LightYellow
                    this.agentHpTextView.setTextColor(ContextCompat.getColor(this, R.color.LightYellow));
                }
                // Si no es menos de la mitad se queda en verde
                this.agentHpTextView.setText("HP: " + health);

                return (atack - defense);

            } else {

                this.agentHpTextView.setText("HP: 0");
                this.agentHpTextView.setTextColor(ContextCompat.getColor(this, R.color.Red));
                return 0;

            }
        }

        return 0;
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


}