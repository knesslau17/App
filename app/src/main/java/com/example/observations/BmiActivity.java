package com.example.observations;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BmiActivity extends AppCompatActivity {

    private double weight;
    private double height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);
        createFile();
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
            double roundedBmi = bd.doubleValue();


            TextView textViewBmi = findViewById(R.id.BMIOutcome);
            String bmiString = Double.toString(roundedBmi);
            textViewBmi.setText(bmiString);

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(BmiActivity.this);
            builder.setMessage(R.string.inputError);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
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

    public void createFile() {
        try {
            File bmiFile = new File(this.getApplicationInfo().dataDir + "/new_directory_name/");
            if (!bmiFile.exists()) {
                bmiFile.mkdir();
            }
            FileWriter file = new FileWriter(bmiFile.getAbsolutePath() + "/bmi.json");
            file.write("what you want to write in internal storage");
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}