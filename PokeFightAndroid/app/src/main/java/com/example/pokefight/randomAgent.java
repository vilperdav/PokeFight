package com.example.pokefight;

import java.util.ArrayList;
import java.util.Random;

public class randomAgent {
    // Function for choose the action of the agen
    public static int chooseAction(int fightSize, ArrayList<pokemon> agentPokemons) {

        // Agent Select its action, takle for defect
        Random azar = new Random();
        int agentAction = 0;

        if (fightSize == 1 || agentPokemons.size() == 1) {
            // 0 - Tackle, 1 - Attack of the Pokemon
            agentAction = azar.nextInt(2);

        } else {
            // 0 - Tackle, 1 - Attack of the Pokemon, 2 changes pokemon
            agentAction = azar.nextInt(3);

        }

        return agentAction;

    }

    // Function for choose the pokemon for changing
    public static int choosePokemonToChange(ArrayList<pokemon> agentPokemons) {

        // Escoge cambiar de pokemon
        int nextPokemon = -1;
        Random azar = new Random();

        // Depending on the pokemons alive the agent selects a random number
        switch (agentPokemons.size()) {

            case 1:
                // Pokemons 0
                nextPokemon = 0;
                break;

            case 2:
                // Pokemons 0,1
                nextPokemon = azar.nextInt(2);
                break;

            case 3:

                // Pokemons 0,1,2
                nextPokemon = azar.nextInt(3);
                break;

            case 4:

                // Pokemons 0,1,2,3
                nextPokemon = azar.nextInt(4);
                break;

            case 5:

                // Pokemons 0,1,2,3,4
                nextPokemon = azar.nextInt(5);
                break;

            case 6:

                // Pokemons 0,1,2,3,4,5
                nextPokemon = azar.nextInt(6);
                break;

            default:

                nextPokemon = -1;
                break;

        }

        return nextPokemon;
    }

}

