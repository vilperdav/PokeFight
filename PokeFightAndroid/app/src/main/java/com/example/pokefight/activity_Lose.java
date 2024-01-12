package com.example.pokefight;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class activity_Lose extends AppCompatActivity {

    /*
     * ********************************************************************************************
     * * vibrate                                                                                  *
     * ********************************************************************************************
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lose);

        // Vibrate when you loose
        vibrate(500);

        // mainMenuButton
        Button mainMenuButton = findViewById(R.id.mainMenu);
        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to next activity in the correct way
                Intent intent = new Intent(activity_Lose.this, activity_Main.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

    }

    /*
     * ********************************************************************************************
     * * vibrate                                                                                  *
     * ********************************************************************************************
     * */
    private void vibrate(int vibrateTime) {

        // Access to the vibrator engine of the phone
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Check if the device is compatible or has vibrator
        if (vibrator == null || !vibrator.hasVibrator()) {
            return;
        }

        // Check if the device is compatible for API 26 and later
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(vibrateTime, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            // Previous API 26 versions
            vibrator.vibrate(vibrateTime);
        }
    }
}