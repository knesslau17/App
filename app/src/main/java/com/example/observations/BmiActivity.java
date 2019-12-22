package com.example.observations;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.RoundingMode;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.valueset.ObservationStatusEnum;
import ca.uhn.fhir.parser.IParser;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class BmiActivity extends AppCompatActivity {

    private double weight;
    private double height;
    double roundedBmi = 0;

    static String outputWeight;
    static String outputHeight;
    static String outputBMI;

    static FhirContext ourCtx;

    static String url = "http://mirth.grieshofer.com:80/observation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

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
            /*
            createWeightObservation(weight);
            createHeightObservation(height);
            createBmiObservation(roundedBmi);
            */
            new CreateObservations(BmiActivity.this).execute(weight, height, roundedBmi);
            Toast.makeText(this, "Warten bis die JSON-Files generiert sind...", Toast.LENGTH_LONG);
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

    public static String createWeightObservation(Double weight) {
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

        try {
            outputWeight = parser.setPrettyPrint(true).encodeResourceToString(observation);
        } catch (Exception x) {
            x.printStackTrace();
        }
        Log.i("patient", outputWeight);
        return outputWeight;
    }

    public static String createHeightObservation(Double height) {
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

    public static String createBmiObservation(Double bmi) {
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

        SyncHttpClient client = new SyncHttpClient();
        client.setMaxRetriesAndTimeout(2, 1000);
        client.setTimeout(200);
        //JSONObject json = ;
        try {
            StringEntity entity = new StringEntity(outputWeight + outputHeight + outputBMI);
            client.post(BmiActivity.this, String.valueOf(url), entity, "observation", new JsonHttpResponseHandler() {


                @Override
                public void onRetry(int retryNo) {
                    super.onRetry(retryNo);
                    Toast.makeText(BmiActivity.this, "Es besteht ein Problem mit der Internetverbindung! Verbingunsversuch Nr. " + retryNo + ".", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
                    Log.println(Log.ERROR, "Fehler", "Fehler");
                }

            });
        } catch (Exception e) {
            Log.println(Log.ERROR, "Fehler", "Fehler");
        }






        /*
        HttpURLConnection conn = null;
        InputStream is = null;
        OutputStream os = null;
        try {

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(outputWeight.getBytes().length + outputHeight.getBytes().length + outputBMI.getBytes().length);


            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setRequestProperty("X-Requested-With", "HttpRequest");

            conn.connect();

            os = new BufferedOutputStream(conn.getOutputStream());
            os.write(outputWeight.getBytes());
            os.write(outputHeight.getBytes());
            os.write(outputBMI.getBytes());

            os.flush();

            //is = conn.getInputStream();
        } finally {
            //clean up
            os.close();
            //is.close();
            conn.disconnect();
        }
        */


    //client = ourCtx.newRestfulGenericClient(serverBaseUrl);
    //client.registerInterceptor(new LoggingInterceptor(true));

        /*
        MethodOutcome outcomeWeight = client.create().resource(outputWeight).execute();
        Boolean postWeight = outcomeWeight.getCreated();
        if (postWeight) {
            Toast.makeText(BmiActivity.this, "Gewicht gespeichert!", Toast.LENGTH_SHORT);
        } else {
            Toast.makeText(BmiActivity.this, "Gewicht nicht gespeichert", Toast.LENGTH_SHORT);
        }


        */
        /*
        MethodOutcome outcomeHeight = client.create().resource(outputHeight).execute();

        Boolean postHeight = outcomeHeight.getCreated();
        if (postHeight) {
            Toast.makeText(BmiActivity.this, "Größe gespeichert!", Toast.LENGTH_SHORT);
        } else {
            Toast.makeText(BmiActivity.this, "Größe nicht gespeichert", Toast.LENGTH_SHORT);
        }

        */
        /*
        MethodOutcome outcomeBMI = client.create().resource(outputBMI).execute();

        Boolean postBMI = outcomeBMI.getCreated();
        if (postBMI) {
            Toast.makeText(BmiActivity.this, "BMI gespeichert!", Toast.LENGTH_SHORT);
        } else {
            Toast.makeText(BmiActivity.this, "BMI nicht gespeichert", Toast.LENGTH_SHORT);
        }

         */


}

    public void onClickSwitchToMain(View view) {
        startActivity(new Intent(BmiActivity.this, MainActivity.class));
        BmiActivity.this.finish();

    }

class CreateObservations extends AsyncTask<Double, Double, String[]> {

    private WeakReference<BmiActivity> activityWeakReference;

    public CreateObservations(BmiActivity activity) {
        activityWeakReference = new WeakReference<BmiActivity>(activity);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        BmiActivity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing()) {
            return;
        }
    }

    @Override
    protected String[] doInBackground(Double... doubles) {
            /*
            String gewicht = BmiActivity.createWeightObservation(weight);
            String groesse = BmiActivity.createHeightObservation(height);
            String bmi = BmiActivity.createBmiObservation(roundedBmi);
             */

        return new String[]{BmiActivity.createWeightObservation(weight), BmiActivity.createHeightObservation(height), BmiActivity.createBmiObservation(roundedBmi)};
    }

    @Override
    protected void onPostExecute(String[] strings) {
        super.onPostExecute(strings);
        findViewById(R.id.saveBMI).setEnabled(true);
        //this.cancel(true);
    }

}

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        //client2.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return url + relativeUrl;
    }
}

