package com.intel.c3.okconnect;

import java.io.IOException;
import java.io.StreamCorruptedException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class OkConnectContactActivity extends Activity {
	
	private Affiliations myAffiliations;
	ContactAdapter friendAdapter;
	ContactAdapter blockedAdapter;
	ListView friendedContactsListView;
	ListView blockedContactsListView;
		
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_ok_connect_contact);
	    try {
			myAffiliations = Affiliations.readFromFile(this, getString(R.string.my_affiliations_file_name));
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    init();
	    
		ListView friendedContactsListView = (ListView) findViewById(R.id.friended_listview);
		ContactAdapter friendAdapter = new ContactAdapter(this, R.layout.contact_row, myAffiliations.getFriended());
		friendedContactsListView.setAdapter(friendAdapter);
		friendedContactsListView.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View v, final int position, long id)
			{
				AlertDialog.Builder adb = new AlertDialog.Builder(OkConnectContactActivity.this);
				adb.setTitle("You clicked on a Person!");
				adb.setMessage("Selected Person is: " + ((Contact) ((ListView) findViewById(R.id.friended_listview)).getItemAtPosition(position)).getName());
				adb.setNeutralButton("Remove from friended list", new DialogInterface.OnClickListener() {
					
				      public void onClick(DialogInterface dialog, int id) {

				       Contact user = (Contact) ((ListView) findViewById(R.id.friended_listview)).getItemAtPosition(position);
				       myAffiliations.getFriended().remove(user);
				       updateUI();
				      }
				}); 
				adb.setPositiveButton("View Friend's Profile", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Contact user = myAffiliations.getFriended().get(position);
						Intent intent = new Intent(getBaseContext(), OkConnectContactViewActivity.class);
						intent.putExtra("PassedContact", user);
						intent.putExtra("FriendStatus", "FRIENDED");
						startActivity(intent);
					}
				});
				adb.show();                     
			}
		});
		
		ListView blockedContactsListView = (ListView) findViewById(R.id.blocked_listview);
		ContactAdapter blockedAdapter = new ContactAdapter(this, R.layout.contact_row, myAffiliations.getBlocked());
		blockedContactsListView.setAdapter(blockedAdapter);
		blockedContactsListView.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View v, final int position, long id)
			{
				AlertDialog.Builder adb = new AlertDialog.Builder(OkConnectContactActivity.this);
				adb.setTitle("You clicked on a Person!");
				adb.setMessage("Selected Person is: " + ((Contact) ((ListView) findViewById(R.id.blocked_listview)).getItemAtPosition(position)).getName());
				adb.setNeutralButton("Remove from blocked list", new DialogInterface.OnClickListener() {
					
				      public void onClick(DialogInterface dialog, int id) {

				       Contact user = (Contact) ((ListView) findViewById(R.id.blocked_listview)).getItemAtPosition(position);
				       myAffiliations.getBlocked().remove(user);
				       updateUI();
				      }
				}); 
				adb.setPositiveButton("View Blocked User's Profile", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Contact user = myAffiliations.getBlocked().get(position);
						Intent intent = new Intent(getBaseContext(), OkConnectContactViewActivity.class);
						intent.putExtra("PassedContact", user);
						intent.putExtra("FriendStatus", "BLOCKED");
						startActivity(intent);
					}
				});
				adb.show();                     
			}
		});
		this.updateUI();
	}
	
	public void init () {		
		
		friendedContactsListView = (ListView) findViewById(R.id.friended_listview);
		friendAdapter = new ContactAdapter(this, R.layout.contact_row, myAffiliations.getFriended());
		friendedContactsListView.setAdapter(friendAdapter);
		
		blockedContactsListView = (ListView) findViewById(R.id.blocked_listview);
		blockedAdapter = new ContactAdapter(this, R.layout.contact_row, myAffiliations.getBlocked());
		blockedContactsListView.setAdapter(blockedAdapter);
	}
	
	public void updateUI () {
		this.runOnUiThread(new Runnable () {
			@Override
			public void run() {
				init();
				friendAdapter.notifyDataSetChanged();
				blockedAdapter.notifyDataSetChanged();
				
				friendedContactsListView.invalidate();
				blockedContactsListView.invalidate();
			}
			
		});
	}
	
	public void toProfileActivity2 (View v) {
		//button onClick method that takes the user to the profile activity
		Intent intent = new Intent (this, OkConnectProfileActivity.class);
		startActivity(intent);
	}
	
	public void toHomeActivity (View v) {
		//button onClick method that takes the user to the home activity
		Intent intent = new Intent (this, OkConnectMainActivity.class);
		startActivity(intent);
	}
	
	@Override 
	public void onPause () {
		try {
			Affiliations.writeToFile(this, getString(R.string.my_affiliations_file_name), myAffiliations);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onPause();
	}
}
