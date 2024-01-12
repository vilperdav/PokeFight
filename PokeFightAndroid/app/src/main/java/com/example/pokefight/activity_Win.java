package com.example.pokefight;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.os.Handler;
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

    private MediaPlayer mediaPlayer;
    private int musicDelay = 1000; //ms
    Handler handler = new Handler();

    /*
     * ********************************************************************************************
     * * onCreate                                                                             *
     * ********************************************************************************************
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win);

        // Give a new medal if we don't get it now
        if (!medalObtained) {
            giveNewMedal();
        }

        // Retard the music 1 second from the battle ends
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                playLevelUp();
            }
        }, musicDelay);

        // mainMenuButton
        Button mainMenuButton = findViewById(R.id.mainMenu);
        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to the next activity in correct way
                Intent intent = new Intent(activity_Win.this, activity_Main.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        // btnGoToCheck for see the medals
        Button btnGoToCheck = findViewById(R.id.goToCheckButton);
        btnGoToCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to the next activity in correct way
                Intent intent = new Intent(activity_Win.this, activity_Medals.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

    }


    /*
     * ********************************************************************************************
     * * playLevelUp                                                                             *
     * ********************************************************************************************
     * */
    private void playLevelUp() {
        // mediaPlayer with music on it
        mediaPlayer = MediaPlayer.create(this, R.raw.levelup);

        // Start the song
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    /*
     * ********************************************************************************************
     * * onDestroy                                                                             *
     * ********************************************************************************************
     * */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Liberate resources onDestroy
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /*
     * ********************************************************************************************
     * * onPause                                                                             *
     * ********************************************************************************************
     * */
    @Override
    protected void onPause() {
        super.onPause();
        // Pauses the mediaPlayer
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    /*
     * ********************************************************************************************
     * * giveNewMedal                                                                             *
     * ********************************************************************************************
     * */
    private void giveNewMedal() {
        try {

            // Check if the file exists in the internal storage
            String jsonFileName = "medals.json";
            File internalJsonFile = new File(getFilesDir(), jsonFileName);

            if (!internalJsonFile.exists()) {
                // If not exist copy it to the internal storage
                CopyRawToSDCard(R.raw.medals, getFilesDir() + "/" + jsonFileName);
                internalJsonFile = new File(getFilesDir(), jsonFileName);
                // System.out.println("File copied to internal storage - did not exist");

            }

            // Read the JSON from the internal storage
            InputStream inputStream = new FileInputStream(internalJsonFile);
            Reader reader = new BufferedReader(new InputStreamReader(inputStream));

            JSONParser parser = new JSONParser();
            Object obj = parser.parse(reader);

            JSONObject pJsonObj = (JSONObject) obj;
            JSONArray arrayAllGenerations = (JSONArray) pJsonObj.get("Medals");

            for (Object genObj : arrayAllGenerations) {
                if (genObj instanceof JSONObject) {
                    JSONObject gen = (JSONObject) genObj;

                    // generation attribute
                    // String genName = (String) gen.get("generation");

                    // System.out.println("**************************");
                    // System.out.println("****** Generation: " + genName + "******");
                    // System.out.println("**************************");

                    JSONArray medalsArray = (JSONArray) gen.get("medals");

                    // Verify if we have medalls for this generation
                    if (medalsArray != null && !medalsArray.isEmpty()) {
                        for (Object medalObj : medalsArray) {
                            if (medalObj instanceof JSONObject) {
                                JSONObject medalDetails = (JSONObject) medalObj;

                                // Access to the info of the medal
                                String medalName = (String) medalDetails.get("name");
                                String visibility = (String) medalDetails.get("visibility");

                                // If one is disabled -> enable it; Only 1st time you access
                                if (visibility.equals("False") && !medalObtained) {

                                    medalDetails.put("visibility", "True");
                                    // visibility = (String) medalDetails.get("visibility");
                                    // System.out.println("Medal " + medalName + " changed to visibility: " + visibility);
                                    medalObtained = true;
                                }
                            }
                        }
                    }
                }

            }

            // Write the changes to the internal storage
            FileWriter fileWriter = new FileWriter(internalJsonFile);
            fileWriter.write(pJsonObj.toJSONString());
            fileWriter.close();

            // Tell to the user that he get a new medal
            Toast.makeText(this, "Medal awarded successfully!", Toast.LENGTH_SHORT).show();

        } catch (IOException | ParseException e) {
            e.printStackTrace();

            // Tell to the user that he get an error
            Toast.makeText(this, "Error awarding medal! Please reset the game data from settings!", Toast.LENGTH_SHORT).show();
        }

    }

    /*
     * ********************************************************************************************
     * * CopyRawToSDCard                                                                             *
     * ********************************************************************************************
     * */
    public int CopyRawToSDCard(int id, String path) {
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