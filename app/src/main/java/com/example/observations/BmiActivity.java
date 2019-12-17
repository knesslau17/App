package com.example.observations;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.valueset.ObservationStatusEnum;
import ca.uhn.fhir.parser.IParser;

public class BmiActivity extends AppCompatActivity {

    private double weight;
    private double height;
    double roundedBmi = 0;

    FhirContext ourCtx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);
        //createFile();
        //create FHIR Context
        ourCtx = FhirContext.forDstu2();

    }

    public void onClickSwitchToMain(View view) {
        startActivity(new Intent(BmiActivity.this, MainActivity.class));
        BmiActivity.this.finish();
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

    public void onClickSaveBMI(View view) {

        JSONObject object = new JSONObject();
        try {
            object.put("BMI", roundedBmi);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void testHapiObserv(){
        Patient patient = new Patient();
        patient.addName().addFamily("Kness");

        IParser parser = ourCtx.newJsonParser();

        // now convert the resource to JSON
        String output = parser.encodeResourceToString(patient);


      //  String output = ourCtx.newJsonParser().encodeResourceToString(patient);
//String output = ourCtx.newJsonParser().setPrettyPrint(true).encodeResourceToString(patient);
        Log.i("patient",output);

    }

    public void createWeightObservation(Double weight){
        // Create an Observation instance
        Observation observation = new Observation();

        observation.setId("Body weight");

        // Give the observation a status
        observation.setStatus(ObservationStatusEnum.FINAL);

        // Give the observation a code (what kind of observation is this)
        CodingDt coding = observation.getCode().addCoding();
        coding.setCode("29463-7").setSystem("http://loinc.org").setDisplay("Body Weight");

        //TO-DO: add relation to Patient -> Subject

        //TO-DO: Zeit hinzufügen

        // Create a quantity datatype
        QuantityDt value = new QuantityDt();
        value.setValue(weight).setSystem("http://unitsofmeasure.org").setCode("kg");
        observation.setValue(value);

        IParser parser = ourCtx.newJsonParser();

        String output = parser.setPrettyPrint(true).encodeResourceToString(observation);
        Log.i("patient",output);
    }

    public void createHeightObservation (Double height){
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

        String output = parser.setPrettyPrint(true).encodeResourceToString(observation);
        Log.i("patient",output);
    }

    public void createBmiObservation (Double bmi){
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

        String output = parser.setPrettyPrint(true).encodeResourceToString(observation);
        Log.i("patient",output);
    }



    /*
    public void createFile() {
        try {
            File bmiFile = new File(this.getApplicationInfo().dataDir + "/new_directory_name/");
            if (!bmiFile.exists()) {
                bmiFile.mkdir();
            }
            FileWriter file = new FileWriter(bmiFile.getAbsolutePath() + "/bmi.json");
            file.write("{\n" +
                    "  \"resourceType\": \"Patient\",\n" +
                    "  \"id\": \"example\",\n" +
                    "  \"text\": {\n" +
                    "    \"status\": \"generated\",\n" +
                    "    \"div\": \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\">\\n\\t\\t\\t<table>\\n\\t\\t\\t\\t<tbody>\\n\\t\\t\\t\\t\\t<tr>\\n\\t\\t\\t\\t\\t\\t<td>Name</td>\\n\\t\\t\\t\\t\\t\\t<td>Peter James \\n              <b>Chalmers</b> (&quot;Jim&quot;)\\n            </td>\\n\\t\\t\\t\\t\\t</tr>\\n\\t\\t\\t\\t\\t<tr>\\n\\t\\t\\t\\t\\t\\t<td>Address</td>\\n\\t\\t\\t\\t\\t\\t<td>534 Erewhon, Pleasantville, Vic, 3999</td>\\n\\t\\t\\t\\t\\t</tr>\\n\\t\\t\\t\\t\\t<tr>\\n\\t\\t\\t\\t\\t\\t<td>Contacts</td>\\n\\t\\t\\t\\t\\t\\t<td>Home: unknown. Work: (03) 5555 6473</td>\\n\\t\\t\\t\\t\\t</tr>\\n\\t\\t\\t\\t\\t<tr>\\n\\t\\t\\t\\t\\t\\t<td>Id</td>\\n\\t\\t\\t\\t\\t\\t<td>MRN: 12345 (Acme Healthcare)</td>\\n\\t\\t\\t\\t\\t</tr>\\n\\t\\t\\t\\t</tbody>\\n\\t\\t\\t</table>\\n\\t\\t</div>\"\n" +
                    "  },\n" +
                    "  \"identifier\": [\n" +
                    "    {\n" +
                    "      \"use\": \"usual\",\n" +
                    "      \"type\": {\n" +
                    "        \"coding\": [\n" +
                    "          {\n" +
                    "            \"system\": \"http://terminology.hl7.org/CodeSystem/v2-0203\",\n" +
                    "            \"code\": \"MR\"\n" +
                    "          }\n" +
                    "        ]\n" +
                    "      },\n" +
                    "      \"system\": \"urn:oid:1.2.36.146.595.217.0.1\",\n" +
                    "      \"value\": \"12345\",\n" +
                    "      \"period\": {\n" +
                    "        \"start\": \"2001-05-06\"\n" +
                    "      },\n" +
                    "      \"assigner\": {\n" +
                    "        \"display\": \"Acme Healthcare\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  \"active\": true,\n" +
                    "  \"name\": [\n" +
                    "    {\n" +
                    "      \"use\": \"official\",\n" +
                    "      \"family\": \"Chalmers\",\n" +
                    "      \"given\": [\n" +
                    "        \"Peter\",\n" +
                    "        \"James\"\n" +
                    "      ]\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"use\": \"usual\",\n" +
                    "      \"given\": [\n" +
                    "        \"Jim\"\n" +
                    "      ]\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"use\": \"maiden\",\n" +
                    "      \"family\": \"Windsor\",\n" +
                    "      \"given\": [\n" +
                    "        \"Peter\",\n" +
                    "        \"James\"\n" +
                    "      ],\n" +
                    "      \"period\": {\n" +
                    "        \"end\": \"2002\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  \"telecom\": [\n" +
                    "    {\n" +
                    "      \"use\": \"home\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"system\": \"phone\",\n" +
                    "      \"value\": \"(03) 5555 6473\",\n" +
                    "      \"use\": \"work\",\n" +
                    "      \"rank\": 1\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"system\": \"phone\",\n" +
                    "      \"value\": \"(03) 3410 5613\",\n" +
                    "      \"use\": \"mobile\",\n" +
                    "      \"rank\": 2\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"system\": \"phone\",\n" +
                    "      \"value\": \"(03) 5555 8834\",\n" +
                    "      \"use\": \"old\",\n" +
                    "      \"period\": {\n" +
                    "        \"end\": \"2014\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  \"gender\": \"male\",\n" +
                    "  \"birthDate\": \"1974-12-25\",\n" +
                    "  \"_birthDate\": {\n" +
                    "    \"extension\": [\n" +
                    "      {\n" +
                    "        \"url\": \"http://hl7.org/fhir/StructureDefinition/patient-birthTime\",\n" +
                    "        \"valueDateTime\": \"1974-12-25T14:35:45-05:00\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  \"deceasedBoolean\": false,\n" +
                    "  \"address\": [\n" +
                    "    {\n" +
                    "      \"use\": \"home\",\n" +
                    "      \"type\": \"both\",\n" +
                    "      \"text\": \"534 Erewhon St PeasantVille, Rainbow, Vic  3999\",\n" +
                    "      \"line\": [\n" +
                    "        \"534 Erewhon St\"\n" +
                    "      ],\n" +
                    "      \"city\": \"PleasantVille\",\n" +
                    "      \"district\": \"Rainbow\",\n" +
                    "      \"state\": \"Vic\",\n" +
                    "      \"postalCode\": \"3999\",\n" +
                    "      \"period\": {\n" +
                    "        \"start\": \"1974-12-25\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  \"contact\": [\n" +
                    "    {\n" +
                    "      \"relationship\": [\n" +
                    "        {\n" +
                    "          \"coding\": [\n" +
                    "            {\n" +
                    "              \"system\": \"http://terminology.hl7.org/CodeSystem/v2-0131\",\n" +
                    "              \"code\": \"N\"\n" +
                    "            }\n" +
                    "          ]\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"name\": {\n" +
                    "        \"family\": \"du Marché\",\n" +
                    "        \"_family\": {\n" +
                    "          \"extension\": [\n" +
                    "            {\n" +
                    "              \"url\": \"http://hl7.org/fhir/StructureDefinition/humanname-own-prefix\",\n" +
                    "              \"valueString\": \"VV\"\n" +
                    "            }\n" +
                    "          ]\n" +
                    "        },\n" +
                    "        \"given\": [\n" +
                    "          \"Bénédicte\"\n" +
                    "        ]\n" +
                    "      },\n" +
                    "      \"telecom\": [\n" +
                    "        {\n" +
                    "          \"system\": \"phone\",\n" +
                    "          \"value\": \"+33 (237) 998327\"\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"address\": {\n" +
                    "        \"use\": \"home\",\n" +
                    "        \"type\": \"both\",\n" +
                    "        \"line\": [\n" +
                    "          \"534 Erewhon St\"\n" +
                    "        ],\n" +
                    "        \"city\": \"PleasantVille\",\n" +
                    "        \"district\": \"Rainbow\",\n" +
                    "        \"state\": \"Vic\",\n" +
                    "        \"postalCode\": \"3999\",\n" +
                    "        \"period\": {\n" +
                    "          \"start\": \"1974-12-25\"\n" +
                    "        }\n" +
                    "      },\n" +
                    "      \"gender\": \"female\",\n" +
                    "      \"period\": {\n" +
                    "        \"start\": \"2012\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  \"managingOrganization\": {\n" +
                    "    \"reference\": \"Organization/1\"\n" +
                    "  }\n" +
                    "}");
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */
}