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

        System.out.println("Juego Iniciado - Estoy en MAINs. \n");

        // CHARGE THE POKEMON JSON DB IN THE MAIN CODE
        String jsonPokemonDB = "db/pokemonDB.json";
        int numberOfPokemonCharged = pokemonCharger(jsonPokemonDB);

        System.out.println("Se han cargado " + numberOfPokemonCharged + " pokemons.");

        int i = 0;
        // See all the pokemons charged
        for (pokemon pokemonInList : listOfPokemons) {

            System.out.println("[" + i + "] - " + pokemonInList.getName());
            i++;

        }

        System.out.println("CHOOSE YOUR POKEMON!:");
        Scanner reader = new Scanner(System.in);
        int pokemonForPlayer = 0;
        pokemonForPlayer = reader.nextInt();
        reader.close();

        Random azar = new Random();
        int pokemonForAgent = azar.nextInt(9);

        System.out.println("Player escogio a: " + listOfPokemons.get(pokemonForPlayer).getName());
        System.out.println("RandomAgent escogio a: " + listOfPokemons.get(pokemonForAgent).getName());

        // System.out.println(listOfPokemons.toString());

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
                                        String img = "img/" + genName + "/" + pokemonName + ".png";
                                        int hp = ((Long) pokemonDetails.get("hp")).intValue();
                                        int attack = ((Long) pokemonDetails.get("attack")).intValue();
                                        int defense = ((Long) pokemonDetails.get("defense")).intValue();
                                        int speed = ((Long) pokemonDetails.get("speed")).intValue();
                                        JSONArray movements = (JSONArray) pokemonDetails.get("movements");

                                        // Puedes hacer más cosas con la información del Pokémon según tus necesidades
                                        System.out.println("Pokemon: " + pokemonName);
                                        System.out.println("IMG: " + img);
                                        System.out.println("Type: " + type);
                                        System.out.println("HP: " + hp);
                                        System.out.println("Attack: " + attack);
                                        System.out.println("Defense: " + defense);
                                        System.out.println("Speed: " + speed);
                                        System.out.println("Movements: " + movements + "");
                                        System.out.println("------------------------");

                                        listOfPokemons.add(pokemonCarged, new pokemon(pokemonName, img, type, hp,
                                                attack, defense, speed, movements));

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
