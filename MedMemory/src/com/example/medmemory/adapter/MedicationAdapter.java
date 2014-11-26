package com.example.medmemory.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.medmemory.R;
import com.example.medmemory.model.Medication;

public class MedicationAdapter extends ArrayAdapter<Medication>
{

	ArrayList<Medication> medications; // yeah i know thats not the real plural
	
	public MedicationAdapter(Context context, int resource, ArrayList<Medication> meds)
	{
		super(context, resource);
		medications = meds;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent)
	{
		if(view == null)
		{
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.med_list_row, null);
		}
		
		Medication med = medications.get(position);
		
		((TextView)view.findViewById(R.id.med_row_name)).setText(med.getName());
		((TextView)view.findViewById(R.id.med_row_count)).setText(med.getCurrentPillCount() + "/" + med.getMaximumPillCount());
		
		return view;
	}
	
	@Override
	public int getCount()
	{
		return medications.size();
	}
	
}
