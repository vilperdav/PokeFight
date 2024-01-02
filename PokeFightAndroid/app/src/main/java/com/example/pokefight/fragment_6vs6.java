package com.example.pokefight;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

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
 * Use the {@link fragment_6vs6#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_6vs6 extends Fragment {

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

    public fragment_6vs6() {
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
    public static fragment_6vs6 newInstance(String param1, String param2) {
        fragment_6vs6 fragment = new fragment_6vs6();
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
        int pokeBattle = 6;
        for (int i = 0; i < pokeBattle; i++) {

            // El agente escoge un pokemon de 0 a 9 aleatoriamente
            pokemon poke = listOfPokemons.get(azar.nextInt(9));
            try {
                agentPokemons.add(poke.clone());
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }

            // El jugador escoge un pokemon de 0 a 9 aleatoriamente
            poke = listOfPokemons.get(azar.nextInt(9));
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

        // VARIABLE FOR KNOW WHAT POKEMONS ARE CHARGED IN THE GAME
        int pokemonCarged = 0;

        try {
            InputStream inputStream = getResources().openRawResource(R.raw.pokemondb);
            Reader reader = new BufferedReader(new InputStreamReader(inputStream));

            JSONParser parser = new JSONParser();
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

                                        listOfPokemons.add(pokemonCarged, new pokemon(pokemonName, img, imgS, type, hp,
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

        // Invertimos el array para que salgan en el orden adecuado
        Collections.reverse(listOfPokemons);

        return pokemonCarged;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_six_vs_six, container, false);

        Button botonCambiarFragmento = view.findViewById(R.id.ChangeModeButton1VS1);
        botonCambiarFragmento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment_1vs1 fragment = new fragment_1vs1();
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
                            if (buttonId == R.id.yourTeamButton1) {
                                playerPokemons.set(0, pokemonSelected);
                            } else if (buttonId == R.id.yourTeamButton2) {
                                playerPokemons.set(1, pokemonSelected);
                            } else if (buttonId == R.id.yourTeamButton3) {
                                playerPokemons.set(2, pokemonSelected);
                            } else if (buttonId == R.id.yourTeamButton4) {
                                playerPokemons.set(3, pokemonSelected);
                            } else if (buttonId == R.id.yourTeamButton5) {
                                playerPokemons.set(4, pokemonSelected);
                            } else if (buttonId == R.id.yourTeamButton6) {
                                playerPokemons.set(5, pokemonSelected);
                            }
                        }

                    }

                });

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), activity_SelectPokemon.class);
                intent.putExtra("buttonId", v.getId());
                intent.putExtra("pokemonList",listOfPokemons);

                mStartForResult.launch(intent);
            }
        };

        // Establecemos 3 Listener para los botones del fragmento
        ImageButton imageButton1 = (ImageButton) view.findViewById(R.id.yourTeamButton1);
        imageButton1.setOnClickListener(clickListener);
        ImageButton imageButton2 = (ImageButton) view.findViewById(R.id.yourTeamButton2);
        imageButton2.setOnClickListener(clickListener);
        ImageButton imageButton3 = (ImageButton) view.findViewById(R.id.yourTeamButton3);
        imageButton3.setOnClickListener(clickListener);
        ImageButton imageButton4 = (ImageButton) view.findViewById(R.id.yourTeamButton4);
        imageButton4.setOnClickListener(clickListener);
        ImageButton imageButton5 = (ImageButton) view.findViewById(R.id.yourTeamButton5);
        imageButton5.setOnClickListener(clickListener);
        ImageButton imageButton6 = (ImageButton) view.findViewById(R.id.yourTeamButton6);
        imageButton6.setOnClickListener(clickListener);

        return view;
    }
}