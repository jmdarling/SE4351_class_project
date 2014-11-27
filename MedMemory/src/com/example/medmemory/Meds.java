package com.example.medmemory;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.medmemory.adapter.MedicationAdapter;
import com.example.medmemory.db.Database;
import com.example.medmemory.model.Medication;

public class Meds extends Activity {

	private static final int ADD_MED_REQUEST = 69; // the sex number
	
	ListView listView;
	ArrayList<Medication> medications; // yeah its not plural f the police
	MedicationAdapter adapter;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meds);
        setTitle("Medication");
        
        // Retrieve all meds
        Database.context = this;
        medications = Database.getAllMedications();
        
        // Set up ListView
        listView = (ListView) findViewById(R.id.med_list);
        adapter = new MedicationAdapter(this, R.layout.med_list_row, medications);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
        			long arg3)
        	{
        		Intent editIntent = new Intent(Meds.this, AddMedication.class);
        		editIntent.putExtra(AddMedication.MEDICATION_ID, medications.get(position).getId());
        		startActivityForResult(editIntent, ADD_MED_REQUEST);
        	}
		});
        
	}
	
	public void gotoaddmedication(View view){
    	Intent myintent = new Intent(this, AddMedication.class);
    	startActivityForResult(myintent, ADD_MED_REQUEST);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == ADD_MED_REQUEST && resultCode == RESULT_OK)
		{
	        // Re-retrieve all meds
	        Database.context = this;
	        medications = Database.getAllMedications();
	        
	        // Replace adapter it's not efficient but who cares
	        adapter = new MedicationAdapter(this, R.layout.med_list_row, medications);
	        listView.setAdapter(adapter);
		}
	}
	
}

