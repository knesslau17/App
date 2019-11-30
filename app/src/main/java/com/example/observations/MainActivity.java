package com.example.observations;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String sSelected=parent.getItemAtPosition(position).toString();
        Toast.makeText(this,sSelected,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        Spinner spinner = (Spinner) findViewById(R.id.patientList);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Patientennamen, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);
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
