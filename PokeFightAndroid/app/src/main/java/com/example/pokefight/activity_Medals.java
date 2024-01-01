package com.example.pokefight;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

        // Check the visibility of the medals
        checkMedals();

    }

    // FUNCTION FOR CHARGE THE JSON OF THE MEDALS
    private void checkMedals() {

        try {

            // Intento leer el fichero
            File jsonFile = new File(Environment.getExternalStorageDirectory(), "/medals.json");

            // Si no lo encontramos lo copiamos y volvemos a cargarlo
            if (!jsonFile.exists()) {
                // Copiamos el fichero
                CopyRawToSDCard(R.raw.medals, Environment.getExternalStorageDirectory() + "/medals.json");
                jsonFile = new File(Environment.getExternalStorageDirectory(), "/medals.json");
                System.out.println("File copy to SD Card - No exists");
            }

            // InputStream inputStream = getResources().openRawResource(R.raw.medals);
            InputStream inputStream = new FileInputStream(jsonFile);
            Reader reader = new BufferedReader(new InputStreamReader(inputStream));

            JSONParser parser = new JSONParser();
            Object obj = parser.parse(reader);

            JSONObject jsonObj = (JSONObject) obj;
            JSONArray arrayOfMedals = (JSONArray) jsonObj.get("Medals");

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
                                        long id = (long) medalDetails.get("id");

                                        // Puedes hacer más cosas con la información del Pokémon según tus necesidades
                                        System.out.println("Medal: " + medalName);
                                        System.out.println("Visibilty: " + visibility);
                                        System.out.println("ID: " + id);

                                        // Ajustamos la visibilidad de las medallas ganadas
                                        String medalResourceName = "medalBlack" + id;
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
    private int CopyRawToSDCard(int id, String path) {
        InputStream in = getResources().openRawResource(id);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            byte[] buff = new byte[1024];
            int read = 0;
            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
            in.close();
            out.close();
            Log.i(TAG, "copyFile, success!");
            return 0;
        } catch (FileNotFoundException e) {
            Log.e(TAG, "copyFile FileNotFoundException " + e.getMessage());
            return -1;
        } catch (IOException e) {
            Log.e(TAG, "copyFile IOException " + e.getMessage());
            return -2;
        }
    }

}