package com.example.medmemory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Meds extends Activity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meds);
	}
	
	public void gotoaddmedication(View view){
    	Intent myintent = new Intent(this, AddMedication.class);
    	startActivity(myintent);
	}
	
}

