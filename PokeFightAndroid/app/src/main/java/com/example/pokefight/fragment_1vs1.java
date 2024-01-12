package com.example.pokefight;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.*;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/*
 * ********************************************************************************************
 * * fragment_1vs1                                                                             *
 * ********************************************************************************************
 * */
public class fragment_1vs1 extends Fragment {

    public static ArrayList<pokemon> listOfPokemons = new ArrayList<pokemon>();
    public static ArrayList<pokemon> agentPokemons = new ArrayList<pokemon>();
    public static ArrayList<pokemon> playerPokemons = new ArrayList<pokemon>();
    private static int numberOfPokemonCharged = 0;
    private static int pokeBattle = 1;
    OnDataPass dataPasser;

    /*
     * ********************************************************************************************
     * * OnDataPass                                                                               *
     * ********************************************************************************************
     * */
    public interface OnDataPass {
        void onDataPass(ArrayList<pokemon> playerPokemons, ArrayList<pokemon> agentPokemons);
    }

    /*
     * ********************************************************************************************
     * * passData                                                                                 *
     * ********************************************************************************************
     * */
    public void passData(ArrayList<pokemon> playerPokemons, ArrayList<pokemon> agentPokemons) {
        dataPasser.onDataPass(playerPokemons, agentPokemons);
    }

    /*
     * ********************************************************************************************
     * * onAttach                                                                                 *
     * ********************************************************************************************
     * */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (OnDataPass) context;
    }

    /*
     * ********************************************************************************************
     * * onCreate                                                                                 *
     * ********************************************************************************************
     * */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Cleaning all the pokemon array Objects
        listOfPokemons.clear();
        agentPokemons.clear();
        playerPokemons.clear();

        // Charge the pokemons in the Array
        numberOfPokemonCharged = pokemonCharger();

        Random azar = new Random();
        // Agent and Player chooses in first instance random pokemons
        for (int i = 0; i < pokeBattle; i++) {

            pokemon poke = listOfPokemons.get(azar.nextInt(numberOfPokemonCharged));
            try {
                agentPokemons.add(poke.clone());
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }

            poke = listOfPokemons.get(azar.nextInt(numberOfPokemonCharged));
            try {
                playerPokemons.add(poke.clone());
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        // Send the arrays to the main activity
        passData(playerPokemons, agentPokemons);

    }

    /*
     * ********************************************************************************************
     * * onCreateView                                                                           *
     * ********************************************************************************************
     * */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflater
        View view = inflater.inflate(R.layout.fragment_one_vs_one, container, false);

        // Shared variable to know what AI we are using
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        if (preferences.getBoolean("IASwitch_state", false)) {
            // We are using the random
            TextView iaTeamTextView = view.findViewById(R.id.IATeamText);
            iaTeamTextView.setText("IA TEAM - RANDOM");
        }

        // changeFragmentButton
        Button changeFragmentButton = view.findViewById(R.id.ChangeModeButton3VS3);
        changeFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment_3vs3 fragment = new fragment_3vs3();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
            }
        });

        ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();

                            // Get the data of the pokemon selected
                            pokemon pokemonSelected = (pokemon) data.getSerializableExtra("selectedPokemon");

                            // ImageButton based on the ID
                            ImageButton imageButton = (ImageButton) getView().findViewById(data.getIntExtra("buttonId", -1));

                            // Set the image of the pokemon selected based on the ID from the resources
                            imageButton.setImageResource(getResources().getIdentifier(pokemonSelected.getName().toLowerCase(), "drawable", getActivity().getPackageName()));
                            playerPokemons.set(0, pokemonSelected);
                        }
                    }

                });

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the intent
                Intent intent = new Intent(getActivity(), activity_SelectPokemon.class);
                intent.putExtra("buttonId", v.getId());
                intent.putExtra("pokemonList", listOfPokemons);
                mStartForResult.launch(intent);
            }
        };

        // Buttons for pick the pokemons
        ImageButton imageButton1 = (ImageButton) view.findViewById(R.id.YourTeamButton1);
        imageButton1.setOnClickListener(clickListener);

        return view;
    }

    /*
     * ********************************************************************************************
     * * pokemonCharger                                                                           *
     * ********************************************************************************************
     * */
    private int pokemonCharger() {

        // Know how much pokemons we charge
        int pokemonCharged = 0;

        try {
            InputStream inputStream = getResources().openRawResource(R.raw.pokemondb);
            Reader reader = new BufferedReader(new InputStreamReader(inputStream));

            JSONParser parser = new JSONParser();
            Object obj = parser.parse(reader);

            JSONObject pJsonObj = (JSONObject) obj;
            JSONArray arrayAllGenerations = (JSONArray) pJsonObj.get("Pokemon");

            for (Object genObj : arrayAllGenerations) {
                if (genObj instanceof JSONObject) {
                    JSONObject gen = (JSONObject) genObj;

                    // Access to the generation
                    // String genName = (String) gen.get("generation");

                    //System.out.println("**************************");
                    //System.out.println("****** Generation: " + genName + "******");
                    //System.out.println("**************************");

                    JSONArray pokemons = (JSONArray) gen.get("pokemons");

                    for (Object pokemonObj : pokemons) {
                        if (pokemonObj instanceof JSONObject) {
                            JSONObject pokemonDetails = (JSONObject) pokemonObj;

                            // Access to the information of the pokemon
                            String pokemonName = (String) pokemonDetails.get("name");
                            String type = (String) pokemonDetails.get("type");
                            // String img = "img/" + genName + "/" + pokemonName + ".png";
                            // String imgS = "img/" + genName + "/" + pokemonName + "_s" + ".png";
                            String img = pokemonName.toLowerCase();
                            String imgS = pokemonName.toLowerCase() + "_s";
                            int hp = ((Long) pokemonDetails.get("hp")).intValue();
                            int attack = ((Long) pokemonDetails.get("attack")).intValue();
                            int defense = ((Long) pokemonDetails.get("defense")).intValue();
                            int speed = ((Long) pokemonDetails.get("speed")).intValue();
                            JSONArray movements = (JSONArray) pokemonDetails.get("movements");

                            // Print pokemon data
                            System.out.println("------------------------");
                            System.out.println("Pokemon: " + pokemonName);
                            // System.out.println("IMG: " + img);
                            // System.out.println("IMG_S: " + imgS);
                            // System.out.println("Type: " + type);
                            // System.out.println("HP: " + hp);
                            // System.out.println("Attack: " + attack);
                            // System.out.println("Defense: " + defense);
                            // System.out.println("Speed: " + speed);
                            // System.out.println("Movements: " + movements + "");
                            // System.out.println("------------------------");
                            // System.out.println("Pokemon: " + pokemonName);

                            listOfPokemons.add(pokemonCharged, new pokemon(pokemonName, img, imgS, type, hp,
                                    attack, defense, speed, movements));

                            // Increase the pokemon number
                            pokemonCharged++;

                        } else {

                            // System.out.println("------------------------");
                            // System.out.println("-------- EMPTY ---------");
                            // System.out.println("------------------------");
                        }
                    }
                }
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        // Shared variable pokemonCharged
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        preferences.edit().putInt("pokemonCharged", pokemonCharged).apply();

        return pokemonCharged;
    }
}