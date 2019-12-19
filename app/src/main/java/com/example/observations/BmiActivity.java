package com.example.observations;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.valueset.ObservationStatusEnum;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;

public class BmiActivity extends AppCompatActivity {

    private double weight;
    private double height;
    double roundedBmi = 0;

    String outputWeight;
    String outputHeight;
    String outputBMI;

    FhirContext ourCtx;
    IGenericClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);
        //create FHIR Context
        ourCtx = FhirContext.forDstu2();
    }


    public void onClickCalculateBMI(View view) {
        //BMI= Gewicht/(Größe)^2 (kg/m^2)


        if (getUserdata()) {

            double bmi = weight / (height * height);


            BigDecimal bd = BigDecimal.valueOf(bmi);
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            roundedBmi = bd.doubleValue();


            TextView textViewBmi = findViewById(R.id.BMIOutcome);
            String bmiString = Double.toString(roundedBmi);
            textViewBmi.setText(bmiString);

            //create Observations
            createWeightObservation(weight);
            createHeightObservation(height);
            createBmiObservation(roundedBmi);

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(BmiActivity.this);
            builder.setMessage(R.string.inputError);
            builder.setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private boolean getUserdata() {
        //Gewicht
        EditText weightEdit = findViewById(R.id.inputWeight);
        if (weightEdit.getText().toString().equals("")) {
            return false;
        } else {
            weight = Double.parseDouble(weightEdit.getText().toString());
        }

        //Körpergröße
        EditText heightEdit = findViewById(R.id.inputHeight);
        if (heightEdit.getText().toString().equals("")) {
            return false;
        } else {
            height = Double.parseDouble(heightEdit.getText().toString());
        }
        return true;
    }

    public String createWeightObservation(Double weight) {
        // Create an Observation instance
        Observation observation = new Observation();

        observation.setId("Body weight");

        // Give the observation a status
        observation.setStatus(ObservationStatusEnum.FINAL);

        // Give the observation a code (what kind of observation is this)
        CodingDt coding = observation.getCode().addCoding();
        coding.setCode("3141-9").setSystem("http://loinc.org").setDisplay("Body Weight");

        //TO-DO: add relation to Patient -> Subject

        //TO-DO: Zeit hinzufügen

        // Create a quantity datatype
        QuantityDt value = new QuantityDt();
        value.setValue(weight).setSystem("http://unitsofmeasure.org").setCode("kg");
        observation.setValue(value);

        IParser parser = ourCtx.newJsonParser();

        outputWeight = parser.setPrettyPrint(true).encodeResourceToString(observation);
        Log.i("patient", outputWeight);
        return outputWeight;
    }

    public String createHeightObservation(Double height) {
        // Create an Observation instance
        Observation observation = new Observation();

        observation.setId("Body height");
        // Give the observation a status
        observation.setStatus(ObservationStatusEnum.FINAL);

        // Give the observation a code (what kind of observation is this)
        CodingDt coding = observation.getCode().addCoding();
        coding.setCode("8302-2").setSystem("http://loinc.org").setDisplay("Body Height");

        //TO_DO: add relation to Patient -> Subject
        //TO-DO: Zeit hinzufügen

        // Create a quantity datatype
        QuantityDt value = new QuantityDt();
        value.setValue(height).setSystem("http://unitsofmeasure.org").setCode("m");
        observation.setValue(value);

        IParser parser = ourCtx.newJsonParser();

        outputHeight = parser.setPrettyPrint(true).encodeResourceToString(observation);
        Log.i("patient", outputHeight);
        return outputHeight;
    }

    public String createBmiObservation(Double bmi) {
        // Create an Observation instance
        Observation observation = new Observation();

        observation.setId("bmi");
        // Give the observation a status
        observation.setStatus(ObservationStatusEnum.FINAL);

        // Give the observation a code (what kind of observation is this)
        CodingDt coding = observation.getCode().addCoding();
        coding.setCode("39156-5").setSystem("http://loinc.org").setDisplay("Body mass index (BMI) [Ratio");

        //TO-DO: add relation to Patient -> Subject
        //TO-DO: Zeit hinzufügen

        // Create a quantity datatype
        QuantityDt value = new QuantityDt();
        value.setValue(bmi).setSystem("http://unitsofmeasure.org").setCode("kg/m2");
        observation.setValue(value);

        IParser parser = ourCtx.newJsonParser();

        outputBMI = parser.setPrettyPrint(true).encodeResourceToString(observation);
        Log.i("patient", outputBMI);
        return outputBMI;
    }

    //Boolean bei Height Weight und BMI um zu checken ob Daten passen
    public void onClickSaveBMI(View view) {

        String serverBaseUrl = "http://mirth.grieshofer.com:80/observation";
        client = ourCtx.newRestfulGenericClient(serverBaseUrl);
        client.registerInterceptor(new LoggingInterceptor(true));

        MethodOutcome outcomeWeight = client.create().resource(outputWeight).execute();
        Boolean postWeight = outcomeWeight.getCreated();
        if (postWeight) {
            Toast.makeText(BmiActivity.this, "Gewicht gespeichert!", Toast.LENGTH_SHORT);
        } else {
            Toast.makeText(BmiActivity.this, "Gewicht nicht gespeichert", Toast.LENGTH_SHORT);
        }


        MethodOutcome outcomeHeight = client.create().resource(outputHeight).execute();
        Boolean postHeight = outcomeHeight.getCreated();
        if (postHeight) {
            Toast.makeText(BmiActivity.this, "Größe gespeichert!", Toast.LENGTH_SHORT);
        } else {
            Toast.makeText(BmiActivity.this, "Größe nicht gespeichert", Toast.LENGTH_SHORT);
        }

        MethodOutcome outcomeBMI = client.create().resource(outputBMI).execute();
        Boolean postBMI = outcomeBMI.getCreated();
        if (postBMI) {
            Toast.makeText(BmiActivity.this, "BMI gespeichert!", Toast.LENGTH_SHORT);
        } else {
            Toast.makeText(BmiActivity.this, "BMI nicht gespeichert", Toast.LENGTH_SHORT);
        }

    }

    public void onClickSwitchToMain(View view) {
        startActivity(new Intent(BmiActivity.this, MainActivity.class));
        BmiActivity.this.finish();

    }


}