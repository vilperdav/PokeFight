package com.example.pokefight;

import static android.widget.Toast.LENGTH_SHORT;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class activity_Info extends AppCompatActivity {

    /*
     * ********************************************************************************************
     * * onCreate                                                                               *
     * ********************************************************************************************
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        // Slide buttons
        Switch shinyModeSwitch = findViewById(R.id.shinyModeSwitch);
        Switch IASwitch = findViewById(R.id.IASwitch);

        // Get the state of the Shiny Button and AI, If not -> Disabled
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("shinySwitch_state", false)) {
            shinyModeSwitch.setChecked(true);
        }
        if (preferences.getBoolean("IASwitch_state", false)) {
            IASwitch.setChecked(true);
        }

        // returnButton
        ImageButton returnButton = findViewById(R.id.ReturnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change the activity in the correct way
                Intent intent = new Intent(activity_Info.this, activity_Main.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        // btnHowToPlay
        Button btnHowToPlay = findViewById(R.id.howToPlayButton);
        btnHowToPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Sends to the README.md of the repository
                String url = "https://github.com/vilperdav/PokeFight/blob/main/README.md";
                openBrowser(url);
            }
        });

        // btnGitHubLink
        Button btnGitHubLink = findViewById(R.id.gitHubLink);
        btnGitHubLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Send to the releases page of the repository
                String url = "https://github.com/vilperdav/PokeFight/releases";
                openBrowser(url);
            }
        });

        // btnResetGameData
        Button btnResetGameData = findViewById(R.id.resetGameData);
        btnResetGameData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check if the file exists in the internal storage
                String jsonFileName = "medals.json";
                File internalJsonFile = new File(getFilesDir(), jsonFileName);
                if (internalJsonFile.exists()) {
                    // If exist -> We delete it
                    internalJsonFile.delete();
                    System.out.println("File deleted from internal storage");
                }

                // Copy the new JSON to the internal storage
                int error = CopyRawToSDCard(R.raw.medals, getFilesDir() + "/" + jsonFileName);
                Snackbar mySnackbar;
                if (error == 0) {
                    mySnackbar = Snackbar.make(view, "Game Medals Reset!", LENGTH_SHORT);
                } else {
                    mySnackbar = Snackbar.make(view, "!Error! Enable Storage Permissions", LENGTH_SHORT);
                }
                mySnackbar.show();
            }
        });

        // shinyModeSwitch
        shinyModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Checks the status of the button
                if (isChecked) {
                    // Log.d("Switch", "Shiny True");
                    preferences.edit().putBoolean("shinySwitch_state", true).apply();
                } else {
                    // Log.d("Switch", "Shiny False");
                    preferences.edit().putBoolean("shinySwitch_state", false).apply();
                }
            }
        });

        // IASwitch
        IASwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Checks the status of the button
                if (isChecked) {
                    // Log.d("Switch", "IA Switch True");
                    preferences.edit().putBoolean("IASwitch_state", true).apply();
                } else {
                    // Log.d("Switch", "IA Switch False");
                    preferences.edit().putBoolean("IASwitch_state", false).apply();
                }
            }
        });
    }

    /*
     * ********************************************************************************************
     * * openBrowser                                                                               *
     * ********************************************************************************************
     * */
    public void openBrowser(String url) {
        // Open the browser in a new activity
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    /*
     * ********************************************************************************************
     * * CopyRawToSDCard                                                                               *
     * ********************************************************************************************
     * */
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