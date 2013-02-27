package com.intel.c3.okconnect;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ContactAdapter extends ArrayAdapter<Contact> {
	
	private List<Contact> items;
	private Context context;
	
	public ContactAdapter (Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		this.context = context;
	}
	
	public ContactAdapter (Context context, int resource, List<Contact> items) {
		super(context, resource, items);
		this.context = context;
		this.items = items;
	}
	
	public void updateItems (List<Contact> list) {
		Log.i("updating_list", "updating the list in the contact adapter");
		this.items = list;
	}
	
	@Override
	public void notifyDataSetChanged() {
		Log.i("changing_display", "changing data set");
		super.notifyDataSetChanged();
	}
	
	@Override
	public View getView (int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		if (v == null) {
			LayoutInflater vi;
			vi = LayoutInflater.from(this.context);
			v = vi.inflate(R.layout.contact_row, null);
		}
		
		Contact p = items.get(position);
		
		if (p != null) {
			TextView nameField = (TextView) v.findViewById(R.id.user_name);
			TextView emailField = (TextView) v.findViewById(R.id.user_match_percentage);
			
			if (nameField != null) {
				nameField.setText(p.getName());
			}
			if (emailField != null) {
				emailField.setText(Integer.toString((int)p.getMatchPercentage()) + " %");
			}
		}
		
		return v;
	}
	
	@Override
	public int getCount () {
		return items.size();		
	}
}