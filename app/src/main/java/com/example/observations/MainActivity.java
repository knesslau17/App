package com.example.observations;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onClickSwitchToRR(View view) {
        startActivity(new Intent(MainActivity.this, BlutdruckActivity.class));
        MainActivity.this.finish();
    }

    public void onClickSwitchToBMI(View view) {
        startActivity(new Intent(MainActivity.this, BmiActivity.class));
        MainActivity.this.finish();
    }

    public void onClickSwitchToBZ(View view) {
        startActivity(new Intent(MainActivity.this, BlutzuckerActivity.class));
        MainActivity.this.finish();
    }


}
