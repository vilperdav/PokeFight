import java.util.*;

/*
 * Compilar:    cd C:\Users\vilperdav\Desktop\Clases\PSI\Practica C\GitHub\PokeFight\PokeFight
 *              javac -cp lib\json-simple-1.1.1.jar src\*.java
 *              java -cp lib\json-simple-1.1.1.jar;src pokeFight
 */

public class battle {

    static int MAX = 1000;
    static int MIN = -1000;

    public static int fight1vs1(pokemon p1, pokemon p2) {

        // p1 -> Player Pokemon
        // p2 -> Agen Pokemon

        // Clean the Screen
        utils.clean();

        // LET THIS WARNING LIKE THIS DONT TOUCH IT
        Scanner scanner = new Scanner(System.in);

        // Switch depending on what pokemon its Faster than other
        switch (chooseFirstMove(p1, p2)) {

            // The Player Pokemon is Faster
            case 0:

                System.out.println(
                        "\n[START-FIGHT] - Player starts first - SPEED: " + p1.getSpeed() + " > " + p2.getSpeed());

                // The Fight exits while the pokemons have hp points
                while (p1.getHealth() > 0 && p2.getHealth() > 0) {

                    // Status of the Pokemon
                    System.out.println("\n[HP] - PLAYER - " + p1.getName() + ": " + p1.getHealth() + " hp");
                    System.out.println("[HP] - AGENT - " + p2.getName() + ": " + p2.getHealth() + " hp");

                    // If the p1 has more than 0 points of life it chooses its atack
                    if (p1.getHealth() > 0) {

                        // We ask the player for its action
                        int playerAction = askPlayerForAction(p1, 1);

                        // It chooses surrender
                        if (playerAction == 4) {

                            // We change the life of the pokemon to 0 hp
                            p1.setHealth(0);
                            break;

                        }

                        // Say what atack chooses the player
                        System.out.println(
                                "\n[ATACK] - Player Select '" + p1.getMovements().get(playerAction).toString() + "'");

                        // Obtains the attackEfectivity of the Atack
                        double playerAtackEfective = atackEfective(p1.getType(), p2.getType(),
                                p1.getMovements().get(playerAction).toString());

                        // Obtains the realDamage of the atack
                        int realDamagePlayer = updateHP(p2, (int) Math.round(p1.getAtack() * playerAtackEfective));

                        // Say the damage done in the pokemon
                        System.out.println("[ATACK] - Player made " + realDamagePlayer + " points of damage.");

                    }

                    // If the p2 has more than 0 points of life it chooses its atack
                    if (p2.getHealth() > 0) {

                        // Agent Select its action
                        // 0 - Tackle, 1 - Attack of the Pokemon
                        Random azar = new Random();
                        int agentAction = azar.nextInt(2);

                        // Say what atack chooses the agent
                        System.out.println(
                                "\n[ATACK] - Agent Select '" + p2.getMovements().get(agentAction).toString() + "'");

                        // Obtains the attackEfectivity of the Atack
                        double agentAtackEfective = atackEfective(p2.getType(), p1.getType(),
                                p2.getMovements().get(agentAction).toString());

                        // Updates the current life of the pokemons
                        int realDamageAgent = updateHP(p1, (int) Math.round(p2.getAtack() * agentAtackEfective));

                        // Says what it the damage made in the pokemons
                        System.out.println("[ATACK] - Agent made " + realDamageAgent + " points of damage.");

                    }

                }

                break;

            // The Agent Pokemon is Faster
            case 1:

                System.out.println(
                        "\n[START-FIGHT] - Agent starts first - SPEED: " + p2.getSpeed() + " > " + p1.getSpeed());

                // The Fight exits while the pokemons have hp points
                while (p1.getHealth() > 0 && p2.getHealth() > 0) {

                    // Status of the Pokemon
                    System.out.println("\n[HP] - PLAYER - " + p1.getName() + ": " + p1.getHealth() + " hp");
                    System.out.println("[HP] - AGENT - " + p2.getName() + ": " + p2.getHealth() + " hp");

                    // If the p2 has more than 0 points of life it chooses its atack
                    if (p2.getHealth() > 0) {

                        // Agent Select its action
                        // 0 - Tackle, 1 - Attack of the Pokemon
                        Random azar = new Random();
                        int agentAction = azar.nextInt(2);

                        // Say what atack chooses the agent
                        System.out.println(
                                "\n[ATACK] - Agent Select '" + p2.getMovements().get(agentAction).toString() + "'");

                        // Obtains the attackEfectivity of the Atack
                        double agentAtackEfective = atackEfective(p2.getType(), p1.getType(),
                                p2.getMovements().get(agentAction).toString());

                        // Updates the current life of the pokemons
                        int realDamageAgent = updateHP(p1, (int) Math.round(p2.getAtack() * agentAtackEfective));

                        // Says what it the damage made in the pokemons
                        System.out.println("[ATACK] - Agent made " + realDamageAgent + " points of damage.");

                    }

                    // If the p1 has more than 0 points of life it chooses its atack
                    if (p1.getHealth() > 0) {

                        // We ask the player for its action
                        int playerAction = askPlayerForAction(p1, 1);

                        // It chooses surrender
                        if (playerAction == 4) {

                            // We change the life of the pokemon to 0 hp
                            p1.setHealth(0);
                            break;

                        }

                        // Say what atack chooses the player
                        System.out.println(
                                "\n[ATACK] - Player Select '" + p1.getMovements().get(playerAction).toString() + "'");

                        // Obtains the attackEfectivity of the Atack
                        double playerAtackEfective = atackEfective(p1.getType(), p2.getType(),
                                p1.getMovements().get(playerAction).toString());

                        // Obtains the realDamage of the atack
                        int realDamagePlayer = updateHP(p2, (int) Math.round(p1.getAtack() * playerAtackEfective));

                        // Say the damage done in the pokemon
                        System.out.println("[ATACK] - Player made " + realDamagePlayer + " points of damage.");

                    }

                }

                break;

            default:
                break;
        }

        // Choose Winner based on the life of the pokemon
        if (p1.getHealth() > 0)

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

    public static int fight3vs3(ArrayList<pokemon> playerPokemons, ArrayList<pokemon> agentPokemons) {

        // TODO
        System.out.println("\n TODOOOOOOO");

        return 0;
    }

    public static int fight6vs6(ArrayList<pokemon> playerPokemons2, ArrayList<pokemon> agentPokemons2) {

        // TODO
        System.out.println("\n TODOOOOOOO");

        return 0;
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

    private static int askPlayerForAction(pokemon p1, int battleType) {

        Scanner scanner = new Scanner(System.in);

        // For now always starts the Player
        boolean ActionSelected = false;

        while (!ActionSelected) {

            // Empieza atacando el jugador
            System.out.println("\n[CHOOSE ATACK] = [ 1.-" + p1.getMovements().get(0) + " ] - [ 2.- "
                    + p1.getMovements().get(1) + "]");
            System.out.print("\n[CHOOSE AN ACTION] = [ 3.- Change Pokemon ] - [ 4.- Surrender ] ");

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
                    } else if (battleType == 3) {

                        // TODO - CHANGE POKEMON 3 vs 3

                    } else if (battleType == 6) {

                        // TODO - CHANGE POKEMON 6 vs 6

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
