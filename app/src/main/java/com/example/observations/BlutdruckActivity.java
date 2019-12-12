package com.example.observations;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BlutdruckActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blutdruck);
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
            File rrFile = new File(this.getApplicationInfo().dataDir + "/new_directory_name/");
            if (!rrFile.exists()) {
                rrFile.mkdir();
            }
            FileWriter file = new FileWriter(rrFile.getAbsolutePath() + "/filename");
            file.write("what you want to write in internal storage");
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onclickSaveRR(View view) {
    }

    public void onClickSwitchToMain(View view) {
        startActivity(new Intent(BlutdruckActivity.this, MainActivity.class));
        BlutdruckActivity.this.finish();
    }
}
