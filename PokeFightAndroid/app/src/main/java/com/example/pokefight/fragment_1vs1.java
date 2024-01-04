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

import java.io.*;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_1vs1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_1vs1 extends Fragment {

    public static ArrayList<pokemon> listOfPokemons = new ArrayList<pokemon>();
    public static ArrayList<pokemon> agentPokemons = new ArrayList<pokemon>();
    public static ArrayList<pokemon> playerPokemons = new ArrayList<pokemon>();
    private static int numberOfPokemonCharged = 0;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_1vs1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_1vs1.
     */


    // TODO: Rename and change types and number of parameters
    public static fragment_1vs1 newInstance(String param1, String param2) {
        fragment_1vs1 fragment = new fragment_1vs1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnDataPass {
        public void onDataPass(ArrayList<pokemon> playerPokemons, ArrayList<pokemon> agentPokemons);
    }

    public void passData(ArrayList<pokemon> playerPokemons, ArrayList<pokemon> agentPokemons) {
        dataPasser.onDataPass(playerPokemons, agentPokemons);
    }

    OnDataPass dataPasser;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (OnDataPass) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // CHARGE THE POKEMON JSON DB IN THE MAIN CODE

        // Cleaning all the pokemon array Objects
        listOfPokemons.clear();
        agentPokemons.clear();
        playerPokemons.clear();

        // Charge the pokemons in the Array
        numberOfPokemonCharged = pokemonCharger();

        Random azar = new Random();
        int pokeBattle = 1;
        for (int i = 0; i < pokeBattle; i++) {

            // El agente escoge un pokemon de 0 a 9 aleatoriamente
            pokemon poke = listOfPokemons.get(azar.nextInt(numberOfPokemonCharged));
            try {
                agentPokemons.add(poke.clone());
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }

            // El jugador escoge un pokemon de 0 a 9 aleatoriamente
            poke = listOfPokemons.get(azar.nextInt(numberOfPokemonCharged));
            try {
                playerPokemons.add(poke.clone());
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        // Envio los arrays al mainActivity
        passData(playerPokemons, agentPokemons);

    }

    // FUNCTION FOR CHARGE THE JSON INTO JAVA OBJECTS TO PLAY WITH THEM
    private int pokemonCharger() {

        // Variable compartida para los switches
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

        // VARIABLE FOR KNOW WHAT POKEMONS ARE CHARGED IN THE GAME
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

                    // Accede al nombre de la generación
                    String genName = (String) gen.get("generation");

                    System.out.println("**************************");
                    System.out.println("****** Generation: " + genName + "******");
                    System.out.println("**************************");

                    JSONArray pokemons = (JSONArray) gen.get("pokemons");

                    for (Object pokemonObj : pokemons) {
                        if (pokemonObj instanceof JSONObject) {
                            JSONObject pokemonDetails = (JSONObject) pokemonObj;

                            // Acess to the information of the pokemon
                            System.out.println("------------------------");
                            String pokemonName = (String) pokemonDetails.get("name");
                            String type = (String) pokemonDetails.get("type");
                            String img = "img/" + genName + "/" + pokemonName + ".png";
                            String imgS = "img/" + genName + "/" + pokemonName + "_s" + ".png";
                            int hp = ((Long) pokemonDetails.get("hp")).intValue();
                            int attack = ((Long) pokemonDetails.get("attack")).intValue();
                            int defense = ((Long) pokemonDetails.get("defense")).intValue();
                            int speed = ((Long) pokemonDetails.get("speed")).intValue();
                            JSONArray movements = (JSONArray) pokemonDetails.get("movements");

                            // Puedes hacer más cosas con la información del Pokémon según tus necesidades
                            System.out.println("Pokemon: " + pokemonName);

                                        /*
                                        System.out.println("IMG: " + img);
                                        System.out.println("IMG_S: " + imgS);
                                        System.out.println("Type: " + type);
                                        System.out.println("HP: " + hp);
                                        System.out.println("Attack: " + attack);
                                        System.out.println("Defense: " + defense);
                                        System.out.println("Speed: " + speed);
                                        System.out.println("Movements: " + movements + "");
                                        System.out.println("------------------------");
                                        System.out.println("Pokemon: " + pokemonName);
                                        */

                            listOfPokemons.add(pokemonCharged, new pokemon(pokemonName, img, imgS, type, hp,
                                    attack, defense, speed, movements));

                            // Incrementar la cantidad de Pokémon cargados
                            pokemonCharged++;
                        } else {
                            System.out.println("------------------------");
                            System.out.println("-------- EMPTY ---------");
                            System.out.println("------------------------");

                        }
                    }
                }
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        // Invertimos el array para que salgan en el orden adecuado
        //Collections.reverse(listOfPokemons);

        preferences.edit().putInt("pokemonCharged", pokemonCharged).apply();

        return pokemonCharged;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_one_vs_one, container, false);

        Button botonCambiarFragmento = view.findViewById(R.id.ChangeModeButton3VS3);
        botonCambiarFragmento.setOnClickListener(new View.OnClickListener() {
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
                            // Maneja tus datos aquí
                            pokemon pokemonSelected = (pokemon) data.getSerializableExtra("selectedPokemon");
                            int buttonId = data.getIntExtra("buttonId", -1);
                            ImageButton imageButton = (ImageButton) getView().findViewById(buttonId);

                            // Obtiene el identificador de recurso de la imagen
                            int resID = getResources().getIdentifier(pokemonSelected.getName().toLowerCase(), "drawable", getActivity().getPackageName());

                            // Establece la imagen del ImageButton
                            imageButton.setImageResource(resID);
                            playerPokemons.set(0, pokemonSelected);
                        }
                    }

                });

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), activity_SelectPokemon.class);
                intent.putExtra("buttonId", v.getId());
                intent.putExtra("pokemonList", listOfPokemons);

                mStartForResult.launch(intent);
            }
        };

        ImageButton imageButton1 = (ImageButton) view.findViewById(R.id.YourTeamButton);
        imageButton1.setOnClickListener(clickListener);

        return view;


    }
}