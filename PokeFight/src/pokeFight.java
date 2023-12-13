import java.io.*;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/*
 * Compilar:    cd C:\Users\vilperdav\Desktop\Clases\PSI\Practica C\GitHub\PokeFight\PokeFight
 *              javac -cp lib\json-simple-1.1.1.jar src\*.java
 *              java -cp lib\json-simple-1.1.1.jar;src pokeFight
 */

public class pokeFight {

    public static ArrayList<pokemon> listOfPokemons = new ArrayList<pokemon>();
    public static ArrayList<pokemon> agentPokemons = new ArrayList<pokemon>();
    public static ArrayList<pokemon> playerPokemons = new ArrayList<pokemon>();

    private static String jsonPokemonDB = "db/pokemonDB.json";
    private static int numberOfPokemonCharged = 0;

    // MAIN CLASS OF THE GAME
    public static void main(String[] args) {

        // CHARGE THE POKEMON JSON DB IN THE MAIN CODE
        numberOfPokemonCharged = pokemonCharger(jsonPokemonDB);

        Scanner scanner = new Scanner(System.in);
        boolean newFigth = true;

        while (newFigth) {

            boolean selectedAction = false;
            int pokemonForPlayer = 0;
            int currentMode = 1;

            while (!selectedAction) {

                // Clean the Screen
                utils.clean();

                System.out.println("[CHARGING] - We charge in the system " + numberOfPokemonCharged + " pokemons. \n");

                int i = 0;
                System.out.println(
                        "[" + i + "] - Change Mode - Current Mode: '" + currentMode + " VS " + currentMode + "'\n");
                // See all the pokemons charged
                for (pokemon pokemonInList : listOfPokemons) {
                    i++;
                    System.out.println(
                            "[" + i + "] - " + pokemonInList.getName() + " => [HP]: " + pokemonInList.getHealth()
                                    + " [PA]: " + pokemonInList.getAtack() + " [PD]: "
                                    + pokemonInList.getDefense() + " [SP]: " + pokemonInList.getSpeed());
                }

                System.out.print("\n[SELECTED] => [ ");
                // See the 3 pokemons selected
                for (pokemon pokemonInList : playerPokemons) {
                    System.out.print(pokemonInList.getName() + ", ");
                }
                System.out.println("]");

                // System.out.println(listOfPokemons.toString());
                System.out.print("\n[CHOOSE] - CHOOSE YOUR POKEMONS (or mode) TO FIGHT!: ");

                // Only continue if we have an int to read
                if (scanner.hasNextInt()) {
                    // Read the integer
                    pokemonForPlayer = scanner.nextInt();

                    // Change the current mode
                    if (pokemonForPlayer == 0) {

                        // Restart all the Arrays of the game between the modes
                        restartGame();

                        // Selector for current mode
                        switch (currentMode) {

                            // Mode 1 VS 1 => 3 VS 3
                            case 1:
                                currentMode = 3;
                                break;

                            // Mode 3 VS 3 => 6 VS 6
                            case 3:
                                currentMode = 6;
                                break;

                            // Mode 6 VS 6 => 1 VS 1
                            case 6:
                                currentMode = 1;
                                break;

                            // Other result to mode 1 vs 1
                            default:
                                currentMode = 1;
                                break;
                        }

                        // Clean the Screen
                        utils.clean();

                        // Select a Pokemon
                    } else if ((pokemonForPlayer > 0) && (pokemonForPlayer <= listOfPokemons.size())) {

                        // Select the pokemon to clone
                        pokemon poke = listOfPokemons.get(pokemonForPlayer - 1);

                        // Selector for current mode
                        switch (currentMode) {

                            // Mode 1 VS 1
                            case 1:

                                // Add the pokemon to the list of chosed
                                try {
                                    playerPokemons.add(poke.clone());
                                } catch (CloneNotSupportedException e) {
                                    e.printStackTrace();
                                }

                                // Only if we choose 1 pokemons exit the while
                                if (playerPokemons.size() == 1) {
                                    selectedAction = true;
                                } else {
                                    selectedAction = false;
                                }
                                break;

                            // Mode 3 VS 3
                            case 3:

                                // Add the pokemon to the list of chosed
                                try {
                                    playerPokemons.add(poke.clone());
                                } catch (CloneNotSupportedException e) {
                                    e.printStackTrace();
                                }

                                // Only if we choose 3 pokemons exit the while
                                if (playerPokemons.size() == 3) {
                                    selectedAction = true;
                                } else {
                                    selectedAction = false;
                                }

                                break;

                            // Mode 6 VS 6
                            case 6:

                                // Add the pokemon to the list of chosed
                                try {
                                    playerPokemons.add(poke.clone());
                                } catch (CloneNotSupportedException e) {
                                    e.printStackTrace();
                                }
                                // Only if we choose 6 pokemons exit the while
                                if (playerPokemons.size() == 6) {
                                    selectedAction = true;
                                } else {
                                    selectedAction = false;
                                }
                                break;

                            // Other result
                            default:
                                selectedAction = false;
                                break;
                        }

                    } else {
                        System.out.println(
                                "\n[ERROR] - Use an ID between the Bounds! [1-" + listOfPokemons.size() + "]. \n");
                    }

                } else {
                    System.out.println(
                            "\n[ERROR] - That Pokemon ID don't exists in the database. Try Again \n");
                    // Clean Scanner
                    scanner.nextLine();
                    // System.exit(0);
                }

            }

            Random azar = new Random();
            for (int i = 0; i < currentMode; i++) {
                // Random Pokemon Betwen 0-8
                pokemon poke = listOfPokemons.get(azar.nextInt(9));
                try {
                    agentPokemons.add(poke.clone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }

            // Clean the Screen
            utils.clean();

            // See all pokemons selected
            System.out.print("\n[P1] - Player Pokemons => [ ");
            for (pokemon pokemonInList : playerPokemons) {
                System.out.print(pokemonInList.getName() + ", ");
            }
            System.out.print("]\n");

            // See all pokemons selected
            System.out.print("\n[P2] - Agent Pokemons => [ ");
            for (pokemon pokemonInList : agentPokemons) {
                System.out.print(pokemonInList.getName() + ", ");
            }
            System.out.print("]\n");

            // Wait for the battle
            wait(3000);

            int result = 0;

            // Mode 1vs1
            if (currentMode == 1) {

                // fight1vs1
                result = battle.fight1vs1(playerPokemons.get(0), agentPokemons.get(0));

                // Mode 3vs3 or 6vs6
            } else {

                // fight 3vs3 or 6vs6
                result = battle.fightNvsN(playerPokemons, agentPokemons);

            }

            // Printing the Winner in Comand Line
            printWinner(result);

            // Ask for new figth or not
            newFigth = askForExit();

        }

        scanner.close();
        System.out.println("See you soon! \n");

    }

    // Function for make simple waits on code
    private static int wait(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 1;
    }

    // Function for printing the winner
    private static void printWinner(int result) {

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

    }

    // Function for ASK for exit the game
    public static boolean askForExit() {

        Scanner scanner = new Scanner(System.in);
        boolean newFigth = true;

        // Display the menu
        System.out.println("[EXIT] - Do you want to fight again? (yes/no)");

        // Limpiar el Scanner antes de usarlo nuevamente
        // scanner.nextLine();
        boolean responded = false;

        while (!responded) {

            // Read user input
            if (scanner.hasNextLine()) {
                String userInput = scanner.nextLine().toLowerCase(); // Convert to lowercase for case-insensitivity

                // Process user input
                if (userInput.equals("yes") || userInput.equals("y")) {
                    System.out.println("You chose to continue. \n");
                    // Responded question
                    responded = true;

                    // Restart all the Arrays of the game
                    restartGame();

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
                newFigth = false;
            }

        }

        return newFigth;
    }

    // Function for restart the game
    public static void restartGame() {

        // Cleaning all the pokemon array Objects
        listOfPokemons.clear();
        agentPokemons.clear();
        playerPokemons.clear();

        // Chargin new pokemons stats in java Objects
        numberOfPokemonCharged = pokemonCharger(jsonPokemonDB);
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
                                        String imgS = "img/" + genName + "/" + pokemonName + "_s" + ".png";
                                        int hp = ((Long) pokemonDetails.get("hp")).intValue();
                                        int attack = ((Long) pokemonDetails.get("attack")).intValue();
                                        int defense = ((Long) pokemonDetails.get("defense")).intValue();
                                        int speed = ((Long) pokemonDetails.get("speed")).intValue();
                                        JSONArray movements = (JSONArray) pokemonDetails.get("movements");

                                        // Puedes hacer más cosas con la información del Pokémon según tus necesidades
                                        /*
                                         * System.out.println("Pokemon: " + pokemonName);
                                         * System.out.println("IMG: " + img);
                                         * System.out.println("IMG_S: " + imgS);
                                         * System.out.println("Type: " + type);
                                         * System.out.println("HP: " + hp);
                                         * System.out.println("Attack: " + attack);
                                         * System.out.println("Defense: " + defense);
                                         * System.out.println("Speed: " + speed);
                                         * System.out.println("Movements: " + movements + "");
                                         * System.out.println("------------------------");
                                         */

                                        listOfPokemons.add(pokemonCarged, new pokemon(pokemonName, img, imgS, type, hp,
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

}
