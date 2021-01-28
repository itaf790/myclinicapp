package com.example.myclinicapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.myclinicapp.model.Fiche;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Activitysheet extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText Disease;
    private EditText description;
    private EditText traitement;
    private Spinner ficheType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activitysheet);

        Disease = findViewById(R.id.disease_sheet);
        description = findViewById(R.id.fiche_description);
        traitement = findViewById(R.id.fiche_traitement);
        ficheType = findViewById(R.id.fiche_type_spinner);

        //Spinner to choose medical type
        Spinner spinner = findViewById(R.id.fiche_type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Tretment_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        Button addFicheButton = findViewById(R.id.button_add);
        addFicheButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFiche();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String SeltectedFicheType = adapterView.getItemAtPosition(i).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void addFiche(){
        String maladieFiche = Disease.getText().toString();
        String descriptionFiche =  description.getText().toString();
        String traitemenfiche = traitement.getText().toString();
        String typeFiche = ficheType.getSelectedItem().toString();

        String patient_name = getIntent().getStringExtra("patient_name");
        String patient_email = getIntent().getStringExtra("patient_email");


        CollectionReference ficheRef = FirebaseFirestore.getInstance().collection("Patient").document(""+patient_email+"")
                .collection("MyMedicalFolder");
        ficheRef.document().set(new Fiche(maladieFiche, descriptionFiche, traitemenfiche, typeFiche, FirebaseAuth.getInstance().getCurrentUser().getEmail().toString()));

        Toast.makeText(this, "\n" + "Sheet added."+patient_name, Toast.LENGTH_LONG).show();
        finish();
    }

}
