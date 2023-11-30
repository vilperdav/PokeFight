import java.io.*;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/*
 * Compilar:    cd C:\Users\vilperdav\Desktop\Clases\PSI\Practica C\GitHub\PokeFight\PokeFight
 *              javac -cp lib\json-simple-1.1.1.jar src\*.java
 *              java -cp lib\json-simple-1.1.1.jar;src battle
 */

public class battle {

    public static ArrayList<pokemon> listOfPokemons = new ArrayList<pokemon>();

    // MAIN CLASS OF THE GAME
    public static void main(String[] args) {

        // CHARGE THE POKEMON JSON DB IN THE MAIN CODE
        String jsonPokemonDB = "db/pokemonDB.json";
        int numberOfPokemonCharged = pokemonCharger(jsonPokemonDB);

        Scanner scanner = new Scanner(System.in);
        boolean newFigth = true;

        while (newFigth) {

            try {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("[CHARGING] - We charge in the system " + numberOfPokemonCharged + " pokemons. \n");

            boolean pokeID = false;
            int pokemonForPlayer = 0;

            while (!pokeID) {

                int i = 0;
                // See all the pokemons charged
                for (pokemon pokemonInList : listOfPokemons) {
                    i++;
                    System.out.println("[" + i + "] - " + pokemonInList.getName());
                }

                // System.out.println(listOfPokemons.toString());
                System.out.println("\n [CHOOSE] - CHOOSE YOUR POKEMON TO FIGHT!:");

                // Limpiar el Scanner antes de usarlo nuevamente

                if (scanner.hasNextInt()) {
                    // Read the integer
                    pokemonForPlayer = scanner.nextInt();
                    pokemonForPlayer = pokemonForPlayer - 1;

                    if ((pokemonForPlayer >= 0) && (pokemonForPlayer < listOfPokemons.size())) {
                        pokeID = true;
                    } else {
                        System.out.println(
                                "[ERROR] - Use an ID between the Bounds! [1-" + listOfPokemons.size() + "]. \n");
                    }

                } else {
                    System.out.println(
                            "[ERROR] - That Pokemon ID don't exists in the database. Try Again \n");
                    scanner.nextLine();
                    // System.exit(0);
                }

            }

            Random azar = new Random();
            int pokemonForAgent = azar.nextInt(9);

            System.out.println("[P1] - Player choose: " + listOfPokemons.get(pokemonForPlayer).getName() + "\n");
            System.out.println("[P2] - RandomAgent choose: " + listOfPokemons.get(pokemonForAgent).getName() + "\n");

            int result = fight(listOfPokemons.get(pokemonForPlayer), listOfPokemons.get(pokemonForAgent));

            System.out.println("\n#################################################################");
            // Final results of the game
            switch (result) {
                // Player Wins
                case 1:
                    System.out.println("\n        [Winner] - Player Wins The Fight! \n");
                    break;

                // Agent Wins
                case 2:
                    System.out.println("\n        [Winner] - Agent Wins The Fight!\n");
                    break;

                // Error Wins
                default:
                    System.out.println("\n        [Winner] - Nobody Wins The Fight!\n");
                    break;
            }
            System.out.println("################################################################# \n");

            // Display the menu
            System.out.println("[EXIT] - Do you want to fight again? (yes/no)");

            boolean responded = false;

            // Limpiar el Scanner antes de usarlo nuevamente
            scanner.nextLine();

            while (!responded) {

                // Read user input
                if (scanner.hasNextLine()) {
                    String userInput = scanner.nextLine().toLowerCase(); // Convert to lowercase for case-insensitivity

                    // Process user input
                    if (userInput.equals("yes") || userInput.equals("y")) {
                        System.out.println("You chose to continue. \n");
                        // Responded question
                        responded = true;

                        // Cleaning all the pokemon Objects
                        listOfPokemons.clear();

                        // Chargin new pokemons stats in java Objects
                        numberOfPokemonCharged = pokemonCharger(jsonPokemonDB);

                    } else if (userInput.equals("no") || userInput.equals("n")) {
                        System.out.println("You chose to exit. \n");
                        // Responded question
                        responded = true;
                        // Exit the game
                        newFigth = false;

                    } else {
                        System.out.println("Invalid input. Please enter 'yes/n' or 'no/n'. \n");
                        // Handle invalid input
                    }

                } else {
                    System.out.println("No input found. \n");
                }

            }

        }
        scanner.close();
        System.out.println("See you soon! \n");

    }

    // FUNCTION FOR CHARGE THE JSON INTO JAVA OBJECTS TO PLAY WITH THEM
    public static int pokemonCharger(String jsonPokemonDB) {

        // VARIABLE FOR KNOW WHAT POKEMONS ARE CHARGED IN THE GAME
        int pokemonCarged = 0;

        try {
            JSONParser parser = new JSONParser();
            FileReader reader = new FileReader(jsonPokemonDB);
            Object obj = parser.parse(reader);

            JSONObject pJsonObj = (JSONObject) obj;
            JSONArray arrayAllPokemons = (JSONArray) pJsonObj.get("Pokemon");

            for (Object genObj : arrayAllPokemons) {
                if (genObj instanceof JSONObject) {
                    JSONObject gen = (JSONObject) genObj;

                    for (Object genKey : gen.keySet()) {
                        if (genKey instanceof String) {
                            String genName = (String) genKey;
                            // Generation Of The Pokemon
                            /*
                             * System.out.println("**************************");
                             * System.out.println("****** Generation: " + genName + "******");
                             * System.out.println("**************************");
                             */
                            JSONObject genDetails = (JSONObject) gen.get(genName);

                            // Check if the Generation is Empty or not
                            if (!genDetails.isEmpty()) {
                                for (Object pokemonKey : genDetails.keySet()) {
                                    if (pokemonKey instanceof String) {
                                        String pokemonName = (String) pokemonKey;

                                        // System.out.println("------------------------");
                                        JSONObject pokemonDetails = (JSONObject) genDetails.get(pokemonName);

                                        // Acess to the information of the pokemon
                                        String type = (String) pokemonDetails.get("type");
                                        String img = "img/" + genName + "/" + pokemonName + ".png";
                                        int hp = ((Long) pokemonDetails.get("hp")).intValue();
                                        int attack = ((Long) pokemonDetails.get("attack")).intValue();
                                        int defense = ((Long) pokemonDetails.get("defense")).intValue();
                                        int speed = ((Long) pokemonDetails.get("speed")).intValue();
                                        JSONArray movements = (JSONArray) pokemonDetails.get("movements");

                                        // Puedes hacer más cosas con la información del Pokémon según tus necesidades
                                        /*
                                         * System.out.println("Pokemon: " + pokemonName);
                                         * System.out.println("IMG: " + img);
                                         * System.out.println("Type: " + type);
                                         * System.out.println("HP: " + hp);
                                         * System.out.println("Attack: " + attack);
                                         * System.out.println("Defense: " + defense);
                                         * System.out.println("Speed: " + speed);
                                         * System.out.println("Movements: " + movements + "");
                                         * System.out.println("------------------------");
                                         */

                                        listOfPokemons.add(pokemonCarged, new pokemon(pokemonName, img, type, hp,
                                                attack, defense, speed, movements));

                                        // Incrementar la cantidad de Pokémon cargados
                                        pokemonCarged++;
                                    }
                                }

                            } else {

                                /*
                                 * System.out.println("------------------------");
                                 * System.out.println("-------- EMPTY ---------");
                                 * System.out.println("------------------------");
                                 */
                            }

                        }
                    }
                }
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return pokemonCarged;
    }

    private static int fight(pokemon p1, pokemon p2) {

        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        // Selection of the first movement
        Random azar = new Random();

        // True/1 -> Player; 0/False -> Agent
        boolean firstMove = azar.nextBoolean();

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
                System.out.println("\n [CHOOSE ATACK] = " + p1.getMovements() + " - [1-2]");

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
                                "[ERROR] - Use an ATACK between the Bounds! [1-" + p1.getMovements().size() + "]. \n");
                    }

                } else {
                    System.out.println(
                            "[ERROR] - That Pokemon ATACK don't exists in the database. Try Again \n");
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
            System.out.println("[GO-OUT] - Agent Pokemon go out of the battle.");
            return 1;

        } else {
            System.out.println("[GO-OUT] - Player Pokemon go out of the battle.");
            return 2;
        }

    }

    // Function for reduce the HP points
    private static int updateHP(pokemon p1, int attack) {

        p1.setHealth(p1.getHealth() - attack);

        return p1.getHealth() - attack;
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
