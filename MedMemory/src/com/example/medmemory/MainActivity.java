package com.example.medmemory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	public void gotoreminders(View view){
    	Intent myintent = new Intent(this, Reminders.class);
    	startActivity(myintent);
	}
	
	public void gotorefills(View view){
    	Intent myintent = new Intent(this, Refills.class);
    	startActivity(myintent);
	}
	
	public void gotomeds(View view){
    	Intent myintent = new Intent(this, Meds.class);
    	startActivity(myintent);
	}
	
	public void gotohistory(View view){
    	Intent myintent = new Intent(this, History.class);
    	startActivity(myintent);
	}
	
	public void gotodoctor(View view){
    	Intent myintent = new Intent(this, Doctor.class);
    	startActivity(myintent);
	}
	
	public void gotopharms(View view){
    	Intent myintent = new Intent(this, Pharm.class);
    	startActivity(myintent);
	}
	
	public void gotosettings(View view){
    	Intent myintent = new Intent(this, Settings.class);
    	startActivity(myintent);
	}
	
	public void gotoabout(View view){
    	Intent myintent = new Intent(this, About.class);
    	startActivity(myintent);
	}
	
	public void gotoaddmedication(View view){
    	Intent myintent = new Intent(this, AddMedication.class);
    	startActivity(myintent);
	}
}
