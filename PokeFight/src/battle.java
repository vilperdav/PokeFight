import java.util.*;

/*
 * Compilar:    cd C:\Users\vilperdav\Desktop\Clases\PSI\Practica C\GitHub\PokeFight\PokeFight
 *              javac -cp lib\json-simple-1.1.1.jar src\*.java
 *              java -cp lib\json-simple-1.1.1.jar;src pokeFight
 */

public class battle {

  

    static int playerMarks = 0;
    static int agentMarks = 0;
    static PokeAgent a=new PokeAgent();
    private static PokeAgent.GameState ia=a.new GameState(null, null);
    private static PokeAgent.Action move=a.new Action();
    static int MAX = 500;
    static int MIN = -500;

    public static int fightNvsN(ArrayList<pokemon> playerPokemons, ArrayList<pokemon> agentPokemons, int battleType) {

        // We choose the first two pokemons of the players
        ia.setPlayerPokemons(agentPokemons);
        ia.setOpponentPokemons(playerPokemons);
        playerMarks = 0;
        agentMarks = 0;
        pokemon p1 = ia.getOpponentPokemons().get(0);
        pokemon p2 = ia.getPlayerPokemons().get(0);
       //gameState= new PokeAgent().GameState(ArrayList<pokemon> playerPokemons,ArrayList<pokemon> agentPokemons);
        // Switch depending on what pokemon its Faster than other
        switch (chooseFirstMove(p1, p2)) {

            // The Player Pokemon is Faster
            case 0:

                System.out.println(
                        "\n[START-FIGHT] - Player starts first - SPEED: " + p1.getSpeed() + " > " + p2.getSpeed());

                // This means, while we have pokemons with life in our bag
                while (playerPokemons.size() > 0 && agentPokemons.size() > 0) {

                    // The Fight exits while the pokemons have hp points
                    while (p1.getHealth() > 0 && p2.getHealth() > 0) {

                        // For inplementing the pokemon changes
                        pokemon nextPokemonPlayer = null;
                        pokemon nextPokemonAgent = null;

                        // Status of the Pokemon
                        System.out.println("\n[HP] - PLAYER - " + p1.getName() + ": " + p1.getHealth() + " hp");
                        System.out.println("[HP] - AGENT - " + p2.getName() + ": " + p2.getHealth() + " hp");

                        // If the p1 has more than 0 points of life it chooses its atack
                        if (p1.getHealth() > 0) {

                            //La ia tiene que evaluar la decisión antes de cualquier tip ode acción del jugador
                            move.setScore(ia.evaluate(ia));
                            
                            move=ia.minimax(3, ia, true);

                            // We ask the player for its action
                            int playerAction = askPlayerForAction(p1, playerPokemons, battleType);

                            // It chooses surrender
                            if (playerAction == 4) {

                                System.out.println("\n[SURRENDER] - PLAYER lose and go out of the battle.");
                                System.out.println("\n[REWARD] - Better luck next time :).");
                                return 2;

                            }

                            // It selected to change the pokemon
                            if (playerAction == 3) {
                                
                                ia.setPermitChange(true);
                                // Try to change the pokemon
                                nextPokemonPlayer = selectAPokemonToChange(ia.getOpponentPokemons());
                                
                                // Says that we want to change the pokemon
                                System.out.println("\n[CHANGE] - Player wants to change the pokemon -> "
                                        + p1.getName() + " -> " + nextPokemonPlayer.getName() + ".");

                                // Looses the atack turn but changes the pokemon
                            } else {

                                // Say what atack chooses the player
                                System.out.println(
                                        "\n[ATACK] - Player Select '" + p1.getMovements().get(playerAction).toString()
                                                + "'");

                                // Obtains the attackEfectivity of the Atack
                                double playerAtackEfective = atackEfective(p1.getType(),ia.getPlayerPokemons().get(ia.getCurrentPlayerPokemonIndex()).getType(),
                                        p1.getMovements().get(playerAction).toString());

                                // Obtains the realDamage of the atack
                                int realDamagePlayer = updateHP(p2,
                                        (int) Math.round(p1.getAtack() * playerAtackEfective));

                                // Say the damage done in the pokemon
                                System.out.println("[ATACK] - Player made " + realDamagePlayer + " points of damage to "
                                        + p2.getName() + ".");

                            }

                        }

                        // At the end of the turn we change the pokemon if its necesary
                        if (!(nextPokemonPlayer == null)) {
                            // We change the current pokemon of the player
                            p1 = nextPokemonPlayer;

                            for(int i=0;i<ia.getOpponentPokemons().size();i++){
                                if(p1.getName()==ia.getOpponentPokemons().get(i).getName()){
                                   
                                    pokemon pokecampo=ia.getOpponentPokemons().get(0);

                                    pokemon newpoke=ia.getOpponentPokemons().get(i);
                                   
                                    ia.getOpponentPokemons().set(0, newpoke);
                                    ia.getOpponentPokemons().set(i, pokecampo);
                                }
                            }
                            
                            nextPokemonPlayer = null;

                            // Status of the Pokemon
                            System.out.println("\n[HP] - PLAYER - " + p1.getName() + ": " + p1.getHealth() + " hp");
                            System.out.println("[HP] - AGENT - " + p2.getName() + ": " + p2.getHealth() + " hp");

                        }

                        // If the p2 has more than 0 points of life it chooses its atack
                        if (p2.getHealth() > 0) {

                            // *****************************************
                            // RANDOM AGENT CODE
                            // *****************************************
                            
                            // Agent Select its action
                            // Agent wants to change the pokemon
                            if (move.isSwitch()) {
                                ia.setPermitChange(false);
                                int nextPokemon = ia.getChangePlayerPokemonIndex();

                                // *****************************************
                                // RANDOM AGENT CODE
                                // *****************************************

                                // Agent wants to change its pokemon

                                 pokemon pokecampo=ia.getPlayerPokemons().get(0);

                                    nextPokemonAgent=ia.getPlayerPokemons().get(nextPokemon);
                                   
                                    ia.getPlayerPokemons().set(0, nextPokemonAgent);
                                    ia.getPlayerPokemons().set(nextPokemon, pokecampo);
                               
                                System.out.println("\n[CHANGE] - Agent wants to change the pokemon -> "
                                        + p2.getName() + " -> " + nextPokemonAgent.getName() + ".");

                            } else {
                                // Losses turn but he can change the pokemon

                                // Say what atack chooses the agent
                                System.out.println(
                                        "\n[ATACK] - Agent Select '" + p2.getMovements().get(move.getAtaque()).toString()
                                                + "'");

                                // Obtains the attackEfectivity of the Atack
                                double agentAtackEfective = atackEfective(p2.getType(), p1.getType(),
                                        p2.getMovements().get(move.getAtaque()).toString());

                                // Updates the current life of the pokemons
                                int realDamageAgent = updateHP(p1,
                                        (int) Math.round(p2.getAtack() * agentAtackEfective));

                                // Says what it the damage made in the pokemons
                                System.out.println("[ATACK] - Agent made " + realDamageAgent + " points of damage to "
                                        + p1.getName() + ".");

                            }

                        }

                        // At the end of the turn we change the pokemon if its necesary
                        if (!(nextPokemonAgent == null)) {
                            // We change the current pokemon of the agent
                            p2 = nextPokemonAgent;

                            nextPokemonAgent = null;

                        }

                    }

                    // One of the pokemons has 0 hp soo we delete if from the array
                    // updateMarksAndNextPokemons(p1, p2, playerPokemons, agentPokemons);

                    // The agent pokemon has 0 hp or less
                    if (p2.getHealth() < 0) {
                        ia.setPermitChange(true);
                        int pokePos = ia.getPlayerPokemons().indexOf(p2);
                        playerMarks++;

                        // We delete the tired pokemon from the array
                        System.out.println(
                                "\n[GO-OUT] - Agent Pokemon " + p2.getName()
                                        + " go out of the battle.");
                        ia.getPlayerPokemons().remove(pokePos);
                        

                        // We obtain a new pokemon
                        if (!agentPokemons.isEmpty()) {
                            p2 = null;
                            p2 = ia.getPlayerPokemons().get(0);
                            //TODO CAMBIAR POKEMON, NO SE SI ALEATORIO O QUE ELIJA
                            System.out.println(
                                    "\n[GO-INT] - Agent Pokemon chooses " + p2.getName()
                                            + " as the new pokemon.");
                        }

                    }

                    // The player pokemon has 0 hp
                    if (p1.getHealth() < 0) {

                        ia.setPermitChange(true);

                        int pokePos = ia.getOpponentPokemons().indexOf(p1);
                        agentMarks++;

                        // We delete the tired pokemon from the array
                        System.out.println(
                                "\n[GO-OUT] - Player Pokemon " + p1.getName()
                                        + " go out of the battle.");
                        ia.getOpponentPokemons().remove(pokePos);

                        // We obtain a new pokemon
                        if (!ia.getOpponentPokemons().isEmpty()) {
                            p1 = null;
                            p1 = ia.getOpponentPokemons().get(0);
                            System.out.println(
                                    "\n[GO-INT] - Player Pokemon chooses " + p1.getName()
                                            + " as the new pokemon.");
                        }

                    }

                    // Print the current pokemons in the game
                    printPokemonsInGame(ia.getOpponentPokemons(), ia.getPlayerPokemons());

                }

                break;

            // The Agent Pokemon is Faster
            case 1:

                System.out.println(
                        "\n[START-FIGHT] - Agent starts first - SPEED: " + p2.getSpeed() + " > " + p1.getSpeed());

                // This means, while we have pokemons with life in our bag
                while (ia.getOpponentPokemons().size() > 0 && ia.getPlayerPokemons().size() > 0) {

                    // The Fight exits while the pokemons have hp points
                    while (p1.getHealth() > 0 && p2.getHealth() > 0) {

                        // For inplementing the pokemon changes
                        pokemon nextPokemonPlayer = null;
                        pokemon nextPokemonAgent = null;

                        // Status of the Pokemon
                        System.out.println("\n[HP] - PLAYER - " + p1.getName() + ": " + p1.getHealth() + " hp");
                        System.out.println("[HP] - AGENT - " + p2.getName() + ": " + p2.getHealth() + " hp");

                        // If the p2 has more than 0 points of life it chooses its atack
                        if (p2.getHealth() > 0) {

                            // *****************************************
                            // RANDOM AGENT CODE
                            // *****************************************

                            // Agent Select its action

                             move.setScore(ia.evaluate(ia));
                          
                            move=ia.minimax(3, ia, true);
                          
                            // Agent wants to change the pokemon
                            if (move.isSwitch())  {

                                ia.setPermitChange(false);
                                int nextPokemon = 0;
                                nextPokemon=ia.getChangePlayerPokemonIndex();
                                
                                
                                // Depending on the pokemons alive the agent selects a random number

                                // *****************************************
                                // RANDOM AGENT CODE
                                // *****************************************

                                // Agent wants to change its pokemon
                                pokemon pokecampo=ia.getPlayerPokemons().get(0);

                                pokemon newpoke=ia.getPlayerPokemons().get(nextPokemon);

                                    ia.getPlayerPokemons().set(0, newpoke);
                                    ia.getPlayerPokemons().set(nextPokemon, pokecampo);
                                nextPokemonAgent = ia.getPlayerPokemons().get(0);
                                
                                System.out.println("\n[CHANGE] - Agent wants to change the pokemon -> "
                                        + p2.getName() + " -> " + nextPokemonAgent.getName() + ".");

                            } else {
                                // Losses turn but he can change the pokemon
                               
                                // Say what atack chooses the agent
                                System.out.println(
                                        "\n[ATACK] - Agent Select '" + p2.getMovements().get(move.getAtaque()).toString()
                                                + "'");

                                // Obtains the attackEfectivity of the Atack
                                double agentAtackEfective = atackEfective(p2.getType(), p1.getType(),
                                        p2.getMovements().get(move.getAtaque()).toString());

                                // Updates the current life of the pokemons
                                int realDamageAgent = updateHP(p1,
                                        (int) Math.round(p2.getAtack() * agentAtackEfective));

                                // Says what it the damage made in the pokemons
                                System.out.println("[ATACK] - Agent made " + realDamageAgent + " points of damage to "
                                        + p1.getName() + ".");

                            }

                        }

                        // At the end of the turn we change the pokemon if its necesary
                        if (!(nextPokemonAgent == null)) {
                            // We change the current pokemon of the agent
                            p2 = nextPokemonAgent;
                            nextPokemonAgent = null;

                            // Status of the Pokemon
                            System.out.println("\n[HP] - PLAYER - " + p1.getName() + ": " + p1.getHealth() + " hp");
                            System.out.println("[HP] - AGENT - " +p2.getName() + ": " + p2.getHealth() + " hp");

                        }

                        // If the p1 has more than 0 points of life it chooses its atack
                        if (p1.getHealth() > 0) {

                            // We ask the player for its action
                            int playerAction = askPlayerForAction(p1, ia.getOpponentPokemons(), battleType);

                            // It chooses surrender
                            if (playerAction == 4) {

                                System.out.println("\n[SURRENDER] - PLAYER lose and go out of the battle.");
                                System.out.println("\n[REWARD] - Better luck next time :).");
                                return 2;

                            }

                            // It selected to change the pokemon
                            if (playerAction == 3) {

                                ia.setPermitChange(true);
                                // Try to change the pokemon
                                nextPokemonPlayer = selectAPokemonToChange(ia.getOpponentPokemons());

                                // Says that we want to change the pokemon
                                System.out.println("\n[CHANGE] - Player wants to change the pokemon -> "
                                        + p1.getName() + " -> " + nextPokemonPlayer.getName() + ".");

                                // Looses the atack turn but changes the pokemon
                            } else {

                                // Say what atack chooses the player
                                System.out.println(
                                        "\n[ATACK] - Player Select '" + p1.getMovements().get(playerAction).toString()
                                                + "'");

                                // Obtains the attackEfectivity of the Atack
                                double playerAtackEfective = atackEfective(p1.getType(), p2.getType(),
                                        p1.getMovements().get(playerAction).toString());

                                // Obtains the realDamage of the atack
                                int realDamagePlayer = updateHP(p2,
                                        (int) Math.round(p1.getAtack() * playerAtackEfective));

                                // Say the damage done in the pokemon
                                System.out.println("[ATACK] - Player made " + realDamagePlayer + " points of damage to "
                                        + p2.getName() + ".");

                            }

                        }

                        // At the end of the turn we change the pokemon if its necesary
                        if (!(nextPokemonPlayer == null)) {
                            // We change the current pokemon of the player
                            p1 = nextPokemonPlayer;
                             for(int i=0;i<ia.getOpponentPokemons().size();i++){
                                if(p1.getName()==ia.getOpponentPokemons().get(i).getName()){
                                   
                                    pokemon pokecampo=ia.getOpponentPokemons().get(0);

                                    pokemon newpoke=ia.getOpponentPokemons().get(i);
                                   
                                    ia.getOpponentPokemons().set(0, newpoke);
                                    ia.getOpponentPokemons().set(i, pokecampo);
                                }
                            }
                            
                            nextPokemonPlayer = null;

                            // TODO - Caso en el que se cambia un pokemon al que le golpean y su vida baja a
                            // cero

                        }

                    }

                    // One of the pokemons has 0 hp soo we delete if from the array
                    // updateMarksAndNextPokemons(p1, p2, playerPokemons, agentPokemons);

                    // The agent pokemon has 0 hp or less
                    if (p2.getHealth() < 0) {

                        ia.setPermitChange(true);
                        int pokePos = ia.getPlayerPokemons().indexOf(p2);
                        playerMarks++;

                        // We delete the tired pokemon from the array
                        System.out.println(
                                "\n[GO-OUT] - Agent Pokemon " + p2.getName()
                                        + " go out of the battle.");
                        ia.getPlayerPokemons().remove(pokePos);

                        // We obtain a new pokemon
                        if (!ia.getPlayerPokemons().isEmpty()) {
                            p2 = null;
                            p2 = ia.getPlayerPokemons().get(0);
                            System.out.println(
                                    "\n[GO-INT] - Agent Pokemon chooses " + p2.getName()
                                            + " as the new pokemon.");
                        }

                    }

                    // The player pokemon has 0 hp
                    if (p1.getHealth() < 0) {

                        ia.setPermitChange(true);
                        int pokePos = ia.getOpponentPokemons().indexOf(p1);
                        agentMarks++;

                        // We delete the tired pokemon from the array
                        System.out.println(
                                "\n[GO-OUT] - Player Pokemon " + p1.getName()
                                        + " go out of the battle.");
                        ia.getOpponentPokemons().remove(pokePos);

                        // We obtain a new pokemon
                        if (!ia.getOpponentPokemons().isEmpty()) {
                            p1 = null;
                            p1 = ia.getOpponentPokemons().get(0);
                            System.out.println(
                                    "\n[GO-INT] - Player Pokemon chooses " + p1.getName()
                                            + " as the new pokemon.");
                        }

                    }

                    // Print the current pokemons in the game
                    printPokemonsInGame(ia.getOpponentPokemons(), ia.getPlayerPokemons());

                }
                break;

            default:
                break;

        }

        // Choose the winner based on the marks of the pokemon defeated
        if (playerMarks > agentMarks)

        {

            System.out.println("\n[GO-OUT] - AGENT Pokemon go out of the battle.");
            System.out.println("\n[REWARD] - Congrats you recibe a GYM medal!.");
            return 1;

        } else {

            System.out.println("\n[GO-OUT] - PLAYER Pokemon go out of the battle.");
            System.out.println("\n[REWARD] - Better luck next time :).");
            return 2;
        }

    }

    /*
     * static int minimax(int depth, int nodeIndex,
     * Boolean maximizingPlayer,
     * int values[], int alpha,
     * int beta)
     * {
     * // Terminating condition. i.e
     * // leaf node is reached
     * if (depth == 3)
     * return values[nodeIndex];
     * 
     * if (maximizingPlayer)
     * {
     * int best = MIN;
     * 
     * // Recur for left and
     * // right children
     * for (int i = 0; i < 2; i++)
     * {
     * int val = minimax(depth + 1, nodeIndex * 2 + i,
     * false, values, alpha, beta);
     * best = Math.max(best, val);
     * alpha = Math.max(alpha, best);
     * 
     * // Alpha Beta Pruning
     * if (beta <= alpha)
     * break;
     * }
     * return best;
     * }
     * else
     * {
     * int best = MAX;
     * 
     * // Recur for left and
     * // right children
     * for (int i = 0; i < 2; i++)
     * {
     * 
     * int val = minimax(depth + 1, nodeIndex * 2 + i,
     * true, values, alpha, beta);
     * best = Math.min(best, val);
     * beta = Math.min(beta, best);
     * 
     * // Alpha Beta Pruning
     * if (beta <= alpha)
     * break;
     * }
     * return best;
     * }
     * }
     */

    /*
     * private static void updateMarksAndNextPokemons(pokemon p1, pokemon p2,
     * ArrayList<pokemon> playerPokemons,
     * ArrayList<pokemon> agentPokemons) {
     * 
     * // The agent pokemon has 0 hp or less
     * if (p2.getHealth() < 0) {
     * 
     * int pokePos = agentPokemons.indexOf(p2);
     * playerMarks++;
     * 
     * // We delete the tired pokemon from the array
     * System.out.println(
     * "\n[GO-OUT] - Agent Pokemon " + p2.getName()
     * + " go out of the battle.");
     * agentPokemons.remove(pokePos);
     * 
     * // We obtain a new pokemon
     * if (!agentPokemons.isEmpty()) {
     * p2 = null;
     * p2 = agentPokemons.get(0);
     * System.out.println(
     * "\n[GO-INT] - Agent Pokemon chooses " + p2.getName()
     * + " as the new pokemon.");
     * }
     * 
     * }
     * 
     * // The player pokemon has 0 hp
     * if (p1.getHealth() < 0) {
     * 
     * int pokePos = playerPokemons.indexOf(p1);
     * agentMarks++;
     * 
     * // We delete the tired pokemon from the array
     * System.out.println(
     * "\n[GO-OUT] - Player Pokemon " + p1.getName()
     * + " go out of the battle.");
     * playerPokemons.remove(pokePos);
     * 
     * // We obtain a new pokemon
     * if (!playerPokemons.isEmpty()) {
     * p1 = null;
     * p1 = playerPokemons.get(0);
     * System.out.println(
     * "\n[GO-INT] - Player Pokemon chooses " + p1.getName()
     * + " as the new pokemon.");
     * }
     * 
     * }
     * 
     * // See all pokemons currently in game
     * System.out.print("\n[P1-POKEMONS] - Player Pokemons => [ ");
     * for (pokemon pokemonInList : playerPokemons) {
     * System.out.print(pokemonInList.getName() + " PV:" +
     * (pokemonInList.getHealth()) + ", ");
     * }
     * System.out.print("]\n");
     * 
     * // See all pokemons selected
     * System.out.print("\n[P2-POKEMONS] - Agent Pokemons => [ ");
     * for (pokemon pokemonInList : agentPokemons) {
     * System.out.print(pokemonInList.getName() + " PV:" +
     * (pokemonInList.getHealth()) + ", ");
     * }
     * System.out.print("]\n");
     * 
     * }
     */

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

    private static pokemon selectAPokemonToChange(ArrayList<pokemon> pokemonList) {

        Scanner scanner = new Scanner(System.in);

        // If we only have one pokemon we dont change

        boolean pokemonChanged = false;
        int pokemonToChange = 0;

        // While the pokemon is not changed, we stay in this loop
        while (!pokemonChanged) {

            // See all pokemons the currently alive in the game
            System.out.print("\n[POKEMONS-2-CHANGE] - Pokemons => [ ");
            int i = 0;
            for (pokemon pokemonInList : pokemonList) {
                i++;
                System.out.print(i + ".- " + pokemonInList.getName() + " [HP]: " + pokemonInList.getHealth() + ", ");
            }
            System.out.print("]: ");

            // Scan the pokemon to change
            if (scanner.hasNextInt()) {

                // Read the integer
                pokemonToChange = scanner.nextInt();
                pokemonToChange = pokemonToChange - 1;

                // Check if the player chooses a pokemon or not
                if (pokemonToChange >= 0 && pokemonToChange < pokemonList.size()) {

                    pokemonChanged = true;

                } else {
                    System.out.println(
                            "\n[ERROR] - Select a pokemon between the bounds [1-"
                                    + pokemonList.size()
                                    + "] \n");
                    scanner.nextLine();
                }

            } else {
                System.out.println(
                        "\n[ERROR] - That Pokemon don't exists in the database. Try Again \n");
                scanner.nextLine();
            }

        }

        // Now we have the id of the pokemon to change it
        return pokemonList.get(pokemonToChange);
    }

    private static int askPlayerForAction(pokemon p1, ArrayList<pokemon> playerPokemons, int battleType) {

        Scanner scanner = new Scanner(System.in);

        // For now always starts the Player
        boolean ActionSelected = false;

        while (!ActionSelected) {

            // Empieza atacando el jugador
            System.out.print("\n[CHOOSE ATACK or ACTION] = [ 1.-" + p1.getMovements().get(0) + " ] - [ 2.- "
                    + p1.getMovements().get(1) + "] - [ 3.- Change Pokemon ] - [ 4.- Surrender ]: ");

            // Tackle for default
            int playerAction = 0;

            if (scanner.hasNextInt()) {

                // Read the integer
                playerAction = scanner.nextInt();

                // Means Change the Pokemon or Surrender
                if (playerAction == 3) {

                    // 1 VS 1
                    if (battleType == 1) {

                        System.out.println("\n[ERROR] - You can't change the pokemon in 1 vs 1 fight.");
                        scanner.nextLine();

                        // 3 VS 3 or 6 VS 6
                    } else {

                        if (playerPokemons.size() == 1) {

                            System.out.println(
                                    "\n[ERROR] - You only have one pokemon with HP. Choose other action.");
                            scanner.nextLine();

                            // We have more pokemons so we can change it
                        } else {

                            return playerAction;

                        }

                    }

                } else if (playerAction == 4) {

                    return playerAction;

                    // Check if its a movement
                } else {

                    playerAction = playerAction - 1;

                    if ((playerAction >= 0) && (playerAction < p1.getMovements().size())) {

                        // Its a movement
                        ActionSelected = true;
                        System.out.println(
                                "\n[ATACK] - You Select '" + p1.getMovements().get(playerAction).toString() + "'");

                        return playerAction;

                    } else {

                        // Other thing that is not an atack
                        System.out.println("\n[ERROR] - Use an ATACK or ACTION between the Bounds! [1-4]. \n");
                    }
                }

            } else {
                System.out.println("\n[ERROR] - That Pokemon ATACK don't exists in the database. Try Again \n");
                scanner.nextLine();
            }

        }

        // Return 0 for default
        return 0;

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
    private static int updateHP(pokemon p1, int atack) {

        // 30% of defense reduction atack
        double defenseFactor = 0.03;
        int defense = (int) Math.round(p1.getDefense() * defenseFactor);
        p1.setHealth(p1.getHealth() - atack + defense);

        if ((atack - defense) == 0) {
            return 0;

        } else {

            return atack - defense;
        }

    }

    // Function for know if the atack its efective or not
    public static double atackEfective(String type1, String type2, String attack) {

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
