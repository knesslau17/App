package com.example.observations;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.valueset.ObservationStatusEnum;
import ca.uhn.fhir.parser.IParser;

public class BlutdruckActivity extends AppCompatActivity {

    FhirContext ourCtx;
    Double diastolic;
    Double systolic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blutdruck);
        createFile();
        ourCtx = FhirContext.forDstu2();
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
        if (getUserData()) {
            createBloodpressureObservation(diastolic, systolic);
            Toast.makeText(this, "Observation created.", Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(BlutdruckActivity.this);
            builder.setMessage(R.string.inputError);
            builder.setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private void createBloodpressureObservation(Double dia, Double sys) {
        // Create an Observation instance
        Observation observation = new Observation();

        observation.setId("Blood-pressure");

        // Give the observation a status
        observation.setStatus(ObservationStatusEnum.FINAL);

        // Give the observation a code (what kind of observation is this)
        CodingDt coding = observation.getCode().addCoding();
        coding.setCode("85354-9").setSystem("http://loinc.org").setDisplay("Blood pressure panel with all children optional");


        //systolic value
        Observation.Component compSys = observation.addComponent();
        CodingDt codingSys = compSys.getCode().addCoding();
        codingSys.setCode("8480-6").setSystem("http://loinc.org").setDisplay("Systolic blood pressure");
        QuantityDt valueSys = new QuantityDt();
        valueSys.setValue(sys).setSystem("http://unitsofmeasure.org").setCode("mm[Hg]");
        compSys.setValue(valueSys);

        //diastolic value
        Observation.Component compDia = observation.addComponent();
        CodingDt codingDia = compDia.getCode().addCoding();
        codingDia.setCode("8480-6").setSystem("http://loinc.org").setDisplay("Diastolic blood pressure");
        QuantityDt valueDia = new QuantityDt();
        valueDia.setValue(dia).setSystem("http://unitsofmeasure.org").setCode("mm[Hg]");
        compDia.setValue(valueDia);

        //TO-DO: add relation to Patient -> Subject

        //TO-DO: Zeit hinzufügen
        // observation.setEffective();


        IParser parser = ourCtx.newJsonParser();

        String output = parser.setPrettyPrint(true).encodeResourceToString(observation);
        Log.i("patient", output);
    }

    public boolean getUserData() {
        //Diastolischer Wert
        EditText diastolicEdit = findViewById(R.id.inputDiastolic);
        if (diastolicEdit.getText().toString().equals("")) {
            return false;
        } else {
            diastolic = Double.parseDouble(diastolicEdit.getText().toString());
        }

        //Systolischer Wert
        EditText systolicEdit = findViewById(R.id.inputSystolic);
        if (systolicEdit.getText().toString().equals("")) {
            return false;
        } else {
            systolic = Double.parseDouble(systolicEdit.getText().toString());
        }
        return true;
    }

    public void onClickSwitchToMain(View view) {
        startActivity(new Intent(BlutdruckActivity.this, MainActivity.class));
        BlutdruckActivity.this.finish();
    }
}
