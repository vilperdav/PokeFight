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

    private ArrayList[] pokemon;

    // MAIN CLASS OF THE GAME
    public static void main(String[] args) {

        System.out.println("Juego Iniciado - Estoy en MAINs. \n");

        // CHARGE THE POKEMON JSON DB IN THE MAIN CODE
        String jsonPokemonDB = "db/pokemonDB.json";
        int numberOfPokemonCharged = pokemonCharger(jsonPokemonDB);

        System.out.println("Se han cargado " + numberOfPokemonCharged + " pokemons. \n");

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
                            System.out.println("**************************");
                            System.out.println("****** Generation: " + genName + "******");
                            System.out.println("**************************");
                            JSONObject genDetails = (JSONObject) gen.get(genName);

                            // Check if the Generation is Empty or not
                            if (!genDetails.isEmpty()) {
                                for (Object pokemonKey : genDetails.keySet()) {
                                    if (pokemonKey instanceof String) {
                                        String pokemonName = (String) pokemonKey;

                                        System.out.println("------------------------");
                                        JSONObject pokemonDetails = (JSONObject) genDetails.get(pokemonName);

                                        // Acess to the information of the pokemon
                                        String type = (String) pokemonDetails.get("type");
                                        int hp = ((Long) pokemonDetails.get("hp")).intValue();
                                        int attack = ((Long) pokemonDetails.get("attack")).intValue();
                                        int defense = ((Long) pokemonDetails.get("defense")).intValue();
                                        int speed = ((Long) pokemonDetails.get("speed")).intValue();
                                        JSONArray movements = (JSONArray) pokemonDetails.get("movements");

                                        // Puedes hacer más cosas con la información del Pokémon según tus necesidades
                                        System.out.println("Pokemon: " + pokemonName);
                                        System.out.println("Type: " + type);
                                        System.out.println("HP: " + hp);
                                        System.out.println("Attack: " + attack);
                                        System.out.println("Defense: " + defense);
                                        System.out.println("Speed: " + speed);
                                        System.out.println("Movements: " + movements + "");
                                        System.out.println("------------------------");

                                        // Incrementar la cantidad de Pokémon cargados
                                        pokemonCarged++;
                                    }
                                }

                            } else {

                                System.out.println("------------------------");
                                System.out.println("-------- EMPTY ---------");
                                System.out.println("------------------------");

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
