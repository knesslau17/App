package com.example.observations;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BlutzuckerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bz);
        createFile();
    }

    @Override
    public void onBackPressed() {
        //if(!werte gespeichert){
        //alertDialog fragt ob zurück ohne speichern
        //}
        //else{
        super.onBackPressed(); //wenn man die Zeile auskommentiert funktioniert der back-Button nicht in der Activity
        //}
    }

    public void createFile() {
        try {
            File bzFile = new File(this.getApplicationInfo().dataDir + "/new_directory_name/");
            if (!bzFile.exists()) {
                bzFile.mkdir();
            }
            FileWriter file = new FileWriter(bzFile.getAbsolutePath() + "/filename");
            file.write("what you want to write in internal storage");
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    public void onClickSaveBZ(View view) {
        JSONObject object = new JSONObject();
        object.put("Blutzucker",)
    }
     */


    public void onClickSwitchToMain(View view) {
        startActivity(new Intent(BlutzuckerActivity.this, MainActivity.class));
        BlutzuckerActivity.this.finish();
    }
}
