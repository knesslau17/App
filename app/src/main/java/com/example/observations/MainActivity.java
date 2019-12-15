package com.example.observations;

import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String object;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            object = getPatient.getNames("http://mirth.grieshofer.com:80/fhir/", 3000);
        } catch (NetworkOnMainThreadException e) {
            Toast.makeText(this, "Verbindung fehlgeschalgen", Toast.LENGTH_LONG);
        }

        spinner = findViewById(R.id.patientList);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Patientennamen, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String sSelected = parent.getItemAtPosition(position).toString();
        //Toast.makeText(this, sSelected, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
