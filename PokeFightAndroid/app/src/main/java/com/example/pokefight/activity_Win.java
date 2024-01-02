package com.example.pokefight;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class activity_Win extends AppCompatActivity {

    private boolean medalObtained = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win);

        // Damos una medalla en orden
        if (!medalObtained) {
            giveNewMedal();
            medalObtained = true;
        }

        // Boton para volver a la pestana anterior, osea al menu
        Button returnButton = findViewById(R.id.mainMenu);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_Win.this, activity_Main.class);

                // Establece la bandera FLAG_ACTIVITY_CLEAR_TOP para limpiar la pila
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Inicia la actividad
                startActivity(intent);

                // Cierra la actividad actual
                finish();
            }
        });

        // Boton para ir a ver las medallas
        Button btnGoToCheck = findViewById(R.id.goToCheckButton);
        btnGoToCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_Win.this, activity_Medals.class);

                // Establece la bandera FLAG_ACTIVITY_CLEAR_TOP para limpiar la pila
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Inicia la actividad
                startActivity(intent);

                // Cierra la actividad actual
                finish();
            }

        });

    }

    private int giveNewMedal() {
        try {

            boolean medalObtained = false;

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

            JSONObject jsonObj = (JSONObject) obj;
            JSONArray arrayOfMedals = (JSONArray) jsonObj.get("Medals");

            for (Object genMedal : arrayOfMedals) {
                if (genMedal instanceof JSONObject) {
                    JSONObject gen = (JSONObject) genMedal;

                    for (Object genKey : gen.keySet()) {
                        if (genKey instanceof String) {
                            String genName = (String) genKey;

                            JSONObject genDetails = (JSONObject) gen.get(genName);

                            // Check if the Generation is Empty or not
                            if (!genDetails.isEmpty()) {
                                for (Object medalKey : genDetails.keySet()) {
                                    if (medalKey instanceof String) {
                                        String medalName = (String) medalKey;

                                        JSONObject medalDetails = (JSONObject) genDetails.get(medalName);

                                        // Acceso a la información de las medallas
                                        String visibility = (String) medalDetails.get("visibility");
                                        long id = (long) medalDetails.get("id");

                                        // Si está desactivada, la activamos
                                        if (visibility.equals("False") && !medalObtained) {
                                            medalDetails.put("visibility", "True");
                                            visibility = (String) medalDetails.get("visibility");
                                            System.out.println(
                                                    "Medal " + medalName + " changed to visibility: " + visibility);
                                            medalObtained = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Ahora escribimos los cambios de vuelta al archivo JSON en el almacenamiento
            // interno
            FileWriter fileWriter = new FileWriter(internalJsonFile);
            fileWriter.write(jsonObj.toJSONString());
            fileWriter.close();

            // Muestra un Toast indicando que la escritura fue exitosa
            Toast.makeText(this, "Medal awarded successfully!", Toast.LENGTH_SHORT).show();

            return 0;

        } catch (IOException | ParseException e) {
            e.printStackTrace();

            // Muestra un Toast indicando que hubo un error
            Toast.makeText(this, "Error awarding medal!", Toast.LENGTH_SHORT).show();
        }

        return -1;
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