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
        cleanScreen.clean("W");

        // LET THIS WARNING LIKE THIS DONT TOUCH IT
        Scanner scanner = new Scanner(System.in);

        // Switch depending on what pokemon its Faster than other
        switch (chooseFirstMove(p1, p2)) {

            // The Player Pokemon is Faster
            case 0:

                System.out.println(
                        "\n[START-FIGHT] - Player starts first - SPEED: " + p1.getSpeed() + " > " + p2.getSpeed());

                // The Fight exits while the pokemons have hp points
                while ((p1.getHealth()) > 0 && (p2.getHealth() > 0)) {

                    // Status of the Pokemon
                    System.out.println("\n[HP] - PLAYER - " + p1.getName() + ": " + p1.getHealth() + " hp");
                    System.out.println("[HP] - AGENT - " + p2.getName() + ": " + p2.getHealth() + " hp");

                    // For now always starts the Player
                    boolean movementSelected = false;

                    while (!movementSelected) {

                        // Empieza atacando el jugador
                        System.out.print("\n[CHOOSE ATACK] = " + p1.getMovements() + " - [1-2]: ");

                        // Tackle for default
                        int atack = 0;

                        if (scanner.hasNextInt()) {
                            // Read the integer
                            atack = scanner.nextInt();
                            atack = atack - 1;

                            if ((atack >= 0) && (atack < p1.getMovements().size())) {

                                movementSelected = true;
                                System.out.println(
                                        "\n[ATACK] - You Select '" + p1.getMovements().get(atack).toString() + "'");
                                // Obtengo el multiplicador del ataque
                                double atackEfective = atackDamage(p1.getType(), p2.getType(),
                                        p1.getMovements().get(atack).toString());
                                // Actualizo la vida del contrincante
                                int totalDamage = (int) Math.round(p2.getAtack() * atackEfective);
                                updateHP(p2, totalDamage);
                                System.out.println("[ATACK] - Player made " + totalDamage + " points of damage.");

                            } else {
                                System.out.println(
                                        "\n[ERROR] - Use an ATACK between the Bounds! [1-" + p1.getMovements().size()
                                                + "]. \n");
                            }

                        } else {
                            System.out.println(
                                    "\n[ERROR] - That Pokemon ATACK don't exists in the database. Try Again \n");
                            scanner.nextLine();
                        }
                    }

                    // 0 - Tackle, 1 - Attack of the Pokemon
                    Random azar = new Random();
                    int agentAtack = azar.nextInt(2);
                    System.out
                            .println("\n[ATACK] - Agent Select '" + p2.getMovements().get(agentAtack).toString() + "'");

                    // Obtengo el multiplicador del ataque
                    double atackEfective = atackDamage(p2.getType(), p1.getType(),
                            p2.getMovements().get(agentAtack).toString());

                    // Actualizo la vida del contrincante
                    int totalDamage = (int) Math.round(p2.getAtack() * atackEfective);
                    updateHP(p1, totalDamage);
                    System.out.println("[ATACK] - Agent made " + totalDamage + " points of damage.");

                }

                break;

            // The Agent Pokemon is Faster
            case 1:

                System.out.println(
                        "\n[START-FIGHT] - Agent starts first - SPEED: " + p2.getSpeed() + " > " + p1.getSpeed());

                // The Fight exits while the pokemons have hp points
                while ((p1.getHealth()) > 0 && (p2.getHealth() > 0)) {

                    // Status of the Pokemon
                    System.out.println("\n[HP] - PLAYER - " + p1.getName() + ": " + p1.getHealth() + " hp");
                    System.out.println("[HP] - AGENT - " + p2.getName() + ": " + p2.getHealth() + " hp");

                    // 0 - Tackle, 1 - Attack of the Pokemon
                    Random azar = new Random();
                    int agentAtack = azar.nextInt(2);

                    // Obtengo el multiplicador del ataque
                    double atackEfective = atackDamage(p2.getType(), p1.getType(),
                            p2.getMovements().get(agentAtack).toString());

                    // Actualizo la vida del contrincante
                    int totalDamageAgent = (int) Math.round(p2.getAtack() * atackEfective);
                    updateHP(p1, totalDamageAgent);

                    // Tackle for default
                    int atack = 0;
                    int totalDamagePlayer = 0;
                    boolean movementSelected = false;

                    while (!movementSelected) {

                        // Empieza atacando el jugador
                        System.out.print("\n[CHOOSE ATACK] = " + p1.getMovements() + " - [1-2]: ");

                        if (scanner.hasNextInt()) {
                            // Read the integer
                            atack = scanner.nextInt();
                            atack = atack - 1;

                            if ((atack >= 0) && (atack < p1.getMovements().size())) {

                                movementSelected = true;

                                // Obtengo el multiplicador del ataque
                                atackEfective = atackDamage(p1.getType(), p2.getType(),
                                        p1.getMovements().get(atack).toString());
                                // Actualizo la vida del contrincante
                                totalDamagePlayer = (int) Math.round(p2.getAtack() * atackEfective);
                                updateHP(p2, totalDamagePlayer);

                            } else {
                                System.out.println(
                                        "\n[ERROR] - Use an ATACK between the Bounds! [1-" + p1.getMovements().size()
                                                + "]. \n");
                            }

                        } else {
                            System.out.println(
                                    "\n[ERROR] - That Pokemon ATACK don't exists in the database. Try Again \n");
                            scanner.nextLine();
                        }
                    }

                    System.out
                            .println("\n[ATACK] - Agent Select '" + p2.getMovements().get(agentAtack).toString() + "'");
                    System.out.println("[ATACK] - Agent made " + totalDamageAgent + " points of damage.");
                    System.out.println(
                            "\n[ATACK] - You Select '" + p1.getMovements().get(atack).toString() + "'");
                    System.out.println("[ATACK] - Player made " + totalDamagePlayer + " points of damage.");
                }

                break;

            default:
                break;
        }

        // Choose Winner based on the life of the pokemon
        if (p1.getHealth() > 0) {
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

    /*static int minimax(int depth, int nodeIndex, 
                   Boolean maximizingPlayer,
                   int values[], int alpha,
                   int beta)
{
    // Terminating condition. i.e 
    // leaf node is reached
    if (depth == 3)
        return values[nodeIndex];
 
    if (maximizingPlayer)
    {
        int best = MIN;
 
        // Recur for left and
        // right children
        for (int i = 0; i < 2; i++)
        {
            int val = minimax(depth + 1, nodeIndex * 2 + i,
                              false, values, alpha, beta);
            best = Math.max(best, val);
            alpha = Math.max(alpha, best);
 
            // Alpha Beta Pruning
            if (beta <= alpha)
                break;
        }
        return best;
    }
    else
    {
        int best = MAX;
 
        // Recur for left and
        // right children
        for (int i = 0; i < 2; i++)
        {
             
            int val = minimax(depth + 1, nodeIndex * 2 + i,
                              true, values, alpha, beta);
            best = Math.min(best, val);
            beta = Math.min(beta, best);
 
            // Alpha Beta Pruning
            if (beta <= alpha)
                break;
        }
        return best;
    }
}*/

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

        return p1.getHealth() - atack;
    }

    // Function for know if the atack its efective or not
    private static double atackDamage(String type1, String type2, String attack) {

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
