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
    	setContentView(R.layout.reminders);
    	Intent myintent = new Intent(this, Reminders.class);
	}
	
	public void gotorefills(View view){
    	setContentView(R.layout.refills);
    	Intent myintent = new Intent(this, Refills.class);
	}
	
	public void gotomeds(View view){
    	setContentView(R.layout.meds);
    	Intent myintent = new Intent(this, Meds.class);
	}
	
	public void gotohistory(View view){
    	setContentView(R.layout.history);
    	Intent myintent = new Intent(this, History.class);
	}
	
	public void gotodoctor(View view){
    	setContentView(R.layout.doctor);
    	Intent myintent = new Intent(this, Doctor.class);
	}
	
	public void gotopharms(View view){
    	setContentView(R.layout.pharm);
    	Intent myintent = new Intent(this, Pharm.class);
	}
	
	public void gotosettings(View view){
    	setContentView(R.layout.settings);
    	Intent myintent = new Intent(this, Settings.class);
	}
	
	public void gotoabout(View view){
    	setContentView(R.layout.about);
    	Intent myintent = new Intent(this, About.class);
	}
}
