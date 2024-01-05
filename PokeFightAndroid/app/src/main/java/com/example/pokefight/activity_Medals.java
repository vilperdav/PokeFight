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
            int medalsUnlock = 0;

            // El nombre de tu archivo JSON
            String jsonFileName = "medals.json";

            // Comprueba si el archivo existe en el almacenamiento interno
            File internalJsonFile = new File(getFilesDir(), jsonFileName);
            if (!internalJsonFile.exists()) {
                // Si no existe, copia el archivo del almacenamiento externo al interno
                CopyRawToSDCard(R.raw.medals, getFilesDir() + "/" + jsonFileName);
                internalJsonFile = new File(getFilesDir(), jsonFileName);
                System.out.println("File copied to internal storage - did not exist");
            }

            // Lee el archivo JSON del almacenamiento interno
            InputStream inputStream = new FileInputStream(internalJsonFile);
            Reader reader = new BufferedReader(new InputStreamReader(inputStream));

            JSONParser parser = new JSONParser();
            Object obj = parser.parse(reader);

            JSONObject pJsonObj = (JSONObject) obj;
            JSONArray arrayAllGenerations = (JSONArray) pJsonObj.get("Medals");

            for (Object genObj : arrayAllGenerations) {
                if (genObj instanceof JSONObject) {
                    JSONObject gen = (JSONObject) genObj;

                    // Accede al nombre de la generación
                    String genName = (String) gen.get("generation");

                    System.out.println("**************************");
                    System.out.println("****** Generation: " + genName + "******");
                    System.out.println("**************************");

                    JSONArray medalsArray = (JSONArray) gen.get("medals");

                    // Verificar si hay medallas para esta generación
                    if (medalsArray != null && !medalsArray.isEmpty()) {
                        for (Object medalObj : medalsArray) {
                            if (medalObj instanceof JSONObject) {
                                JSONObject medalDetails = (JSONObject) medalObj;

                                // Acess to the information of the medal
                                System.out.println("------------------------");
                                // Acceso a la información de la medalla
                                String medalName = (String) medalDetails.get("name");
                                String visibility = (String) medalDetails.get("visibility");
                                long id = (long) medalDetails.get("id");

                                // Puedes hacer más cosas con la información de la medalla según tus necesidades
                                System.out.println("Medal: " + medalName);
                                System.out.println("Visibility: " + visibility);
                                System.out.println("ID: " + id);

                                // Ajustamos la visibilidad de las medallas ganadas
                                String medalResourceName = "medalBlack" + id;
                                int medalId = getResources().getIdentifier(medalResourceName, "id", getPackageName());
                                ImageView medalBlack = findViewById(medalId);

                                // Si alguna está desactivada
                                if (visibility.equals("True")) {
                                    medalBlack.setVisibility(View.GONE);
                                    System.out.println("Estado: Visible");
                                    medalsUnlock++;
                                } else {
                                    medalBlack.setVisibility(View.VISIBLE);
                                    System.out.println("Estado: No Visible");
                                }

                                // Recompensa si ganas 16 veces al IA
                                if (medalsUnlock == 16) {
                                    ImageView pikachu = findViewById(R.id.pikachuExtraBlack);
                                    pikachu.setVisibility(View.GONE);
                                }
                            }
                        }
                    } else {
                        System.out.println("No medals for generation: " + genName);
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