package com.example.medmemory;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.example.medmemory.adapter.MedicationAdapter;
import com.example.medmemory.db.Database;
import com.example.medmemory.model.Medication;

public class Meds extends Activity {

	ListView listView;
	ArrayList<Medication> medications; //yeah its not plural f the police
	MedicationAdapter adapter;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meds);
        
        Database.context = this;
        medications = Database.getAllMedications();
        
        listView = (ListView) findViewById(R.id.med_list);
        adapter = new MedicationAdapter(this, R.layout.med_list_row, medications);
        listView.setAdapter(adapter);
        
	}
	
	public void gotoaddmedication(View view){
    	Intent myintent = new Intent(this, AddMedication.class);
    	startActivity(myintent);
	}
	
}

