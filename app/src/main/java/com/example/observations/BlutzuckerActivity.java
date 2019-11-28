package com.example.observations;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class BlutzuckerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bz);
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


    public void onClickSaveBZ(View view) {
    }

    public void onClickSwitchToMain(View view) {
        startActivity(new Intent(BlutzuckerActivity.this, MainActivity.class));
        BlutzuckerActivity.this.finish();
    }
}
