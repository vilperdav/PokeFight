import java.util.*;

/*
 * Compilar:    cd C:\Users\vilperdav\Desktop\Clases\PSI\Practica C\GitHub\PokeFight\PokeFight
 *              javac -cp lib\json-simple-1.1.1.jar src\*.java
 *              java -cp lib\json-simple-1.1.1.jar;src pokeFight
 */

public class battle {

    public static int fight1vs1(pokemon p1, pokemon p2) {

        // Clean the Screen
        cleanScreen.clean("W");

        // Selection of the first movement
        Random azar = new Random();

        // True/1 -> Player; 0/False -> Agent
        boolean firstMove = azar.nextBoolean();

        // LET THIS WARNING LIKE THIS DONT TOUCH IT
        Scanner scanner = new Scanner(System.in);

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
                        System.out.println("\n[ATACK] - You Select '" + p1.getMovements().get(atack).toString() + "'");
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
            int agentAtack = azar.nextInt(2);
            System.out.println("\n[ATACK] - Agent Select '" + p2.getMovements().get(agentAtack).toString() + "'");
            // Obtengo el multiplicador del ataque
            double atackEfective = atackDamage(p2.getType(), p1.getType(),
                    p2.getMovements().get(agentAtack).toString());
            // Actualizo la vida del contrincante
            int totalDamage = (int) Math.round(p2.getAtack() * atackEfective);
            updateHP(p1, totalDamage);
            System.out.println("[ATACK] - Agent made " + totalDamage + " points of damage.");

        }

        if (p1.getHealth() > 0) {
            System.out.println("\n[GO-OUT] - AGENT Pokemon go out of the battle.");
            System.out.println("\n[REWARD] - Congrats you recibe a GYM medal!.");
            return 1;

        } else {
            System.out.println("\n[GO-OUT] - PLAYER Pokemon go out of the battle.");
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
