package com.example.pokefight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;

import android.widget.ImageView;
import android.view.View;

public class activity_Medals extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medals);

        ImageButton tuBoton = findViewById(R.id.ReturnButton);

        tuBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_Medals.this, activity_Main.class);

                // Establece la bandera FLAG_ACTIVITY_CLEAR_TOP para limpiar la pila
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Inicia la actividad
                startActivity(intent);

                // Cierra la actividad actual
                finish();
            }
        });

        checkMedals();


    }

    // FUNCTION FOR CHARGE THE JSON OF THE MEDALS
    private void checkMedals() {

        int medalCounter = 15;

        try {
            InputStream inputStream = getResources().openRawResource(R.raw.medals);
            Reader reader = new BufferedReader(new InputStreamReader(inputStream));

            JSONParser parser = new JSONParser();
            Object obj = parser.parse(reader);

            JSONObject pJsonObj = (JSONObject) obj;
            JSONArray arrayOfMedals = (JSONArray) pJsonObj.get("Medals");

            for (Object genMedal : arrayOfMedals) {
                if (genMedal instanceof JSONObject) {
                    JSONObject gen = (JSONObject) genMedal;

                    for (Object genKey : gen.keySet()) {
                        if (genKey instanceof String) {
                            String genName = (String) genKey;

                            // Generation Of The Pokemon
                            System.out.println("**************************");
                            System.out.println("****** Generation Medal: " + genName + "******");
                            System.out.println("**************************");

                            JSONObject genDetails = (JSONObject) gen.get(genName);

                            // Check if the Generation is Empty or not
                            if (!genDetails.isEmpty()) {
                                for (Object medalKey : genDetails.keySet()) {
                                    if (medalKey instanceof String) {
                                        String medalName = (String) medalKey;

                                        System.out.println("------------------------");
                                        JSONObject medalDetails = (JSONObject) genDetails.get(medalName);

                                        // Acess to the information of the pokemon
                                        String visibility = (String) medalDetails.get("visibility");

                                        // Puedes hacer más cosas con la información del Pokémon según tus necesidades
                                        System.out.println("Medal: " + medalName);
                                        System.out.println("Visibilty: " + visibility);

                                        // Ajustamos la visibilidad de las medallas ganadas
                                        String medalResourceName = "medalBlack" + medalCounter;
                                        int medalId = getResources().getIdentifier(medalResourceName, "id", getPackageName());
                                        ImageView medalBlack = findViewById(medalId);

                                        // Si alguna esta desactivada
                                        if(visibility.equals("True")) {
                                            medalBlack.setVisibility(View.GONE);
                                            System.out.println("Estado: Visible");
                                        } else {
                                            medalBlack.setVisibility(View.VISIBLE);
                                            System.out.println("Estado: No Visible");
                                        }

                                        medalCounter--;

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

    }
}