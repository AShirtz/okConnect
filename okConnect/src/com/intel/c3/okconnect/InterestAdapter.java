package com.intel.c3.okconnect;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class InterestAdapter extends ArrayAdapter<Interest> {
	
	private List<Interest> items;
	private Context context;
	
	public InterestAdapter (Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		this.context = context;
	}
	
	public InterestAdapter (Context context, int resource, List<Interest> items) {
		super(context, resource, items);
		this.context = context;
		this.items = items;
	}
	
	public void updateItems (List<Interest> list) {
		this.items = list;
	}
	
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
	
	@Override
	public View getView (int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		if (v == null) {
			LayoutInflater vi;
			vi = LayoutInflater.from(this.context);
			v = vi.inflate(R.layout.interest_row, null);
		}
		
		Interest p = items.get(position);
		
		if (p != null) {
			TextView interestField = (TextView) v.findViewById(R.id.Interest_Field);
			TextView ratingField = (TextView) v.findViewById(R.id.Rating_Field);
			
			if (interestField != null) {
				interestField.setText(p.getInterest());
			}
			if (ratingField != null) {
				String result = "";
				for (int i = p.getRating(); i > 0; i--) {
					result += "* ";
				}
				ratingField.setText(result);
			}
		}
		
		return v;
	}
	
	@Override
	public int getCount () {
		return items.size();
	}
}