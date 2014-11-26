package com.example.medmemory;

import java.sql.Date;
import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.medmemory.db.Database;
import com.example.medmemory.model.Medication;

public class AddMedication extends Activity {

	TextView medName;
	TextView pillCount;
	TextView notes;
	TimePicker timePicker;

	Button saveBtn;
	
	Date reminderDate;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addmedication);
        
        medName = (TextView) findViewById(R.id.med_name);
        pillCount = (TextView) findViewById(R.id.med_count);
        notes = (TextView) findViewById(R.id.med_notes);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        
        saveBtn = (Button) findViewById(R.id.save_med_btn);
        saveBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				if(medName.getText().length() == 0)
					medName.setText("Aspirin");
				if(pillCount.getText().length() == 0)
					pillCount.setText("30");
				if(notes.getText().length() == 0)
					notes.setText("Take with food, do not take with orange juice.");
				
				System.out.println("====SAVING MED====");
				System.out.println("Name: "+medName.getText());
				System.out.println("Pill Count: "+pillCount.getText());
				System.out.println("Notes: "+notes.getText());
				System.out.println("Reminder Time: "+timePicker.getCurrentHour()+":"+timePicker.getCurrentMinute());
				
				Medication med = new Medication();
				med.setName(medName.getText().toString());
				med.setMaximumPillCount(Integer.parseInt(pillCount.getText().toString()));
				med.setCurrentPillCount(Integer.parseInt(pillCount.getText().toString()));
				med.setNotes(notes.getText().toString());
				
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY,timePicker.getCurrentHour());
				cal.set(Calendar.MINUTE,timePicker.getCurrentMinute());
				cal.set(Calendar.SECOND,0);
				cal.set(Calendar.MILLISECOND,0);
				med.setReminderDate(cal.getTime());
				
				Database.context = AddMedication.this;
				boolean success = Database.addMedication(med);
				System.out.println("Added med successfully? "+success);
				
				finish();
			}
		});
	}
	
	
	
}