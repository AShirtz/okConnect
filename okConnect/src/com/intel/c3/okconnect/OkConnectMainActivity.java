package com.intel.c3.okconnect;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class OkConnectMainActivity extends ServiceUsingActivity {
	
	Button profileButton = null;
	Button contactsButton = null;
	ListView currentContactsListView = null;
	ContactAdapter contactAdapter = null;	
	List<Contact> currentContactsList = new ArrayList<Contact>();
	Contact selfContact = null;
	Affiliations myAffiliations = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ok_connect_main);
		loadDataFromFile();
		initializeUI();
		doStartService();
		updateUI();
	}
	
	public void initializeUI () {
		profileButton = (Button) findViewById(R.id.Profile_Button_From_Main);
		contactsButton = (Button) findViewById(R.id.Contacts_Button_From_Main);
		currentContactsListView = (ListView) findViewById(R.id.Current_Contacts_List_View);
		currentContactsListView.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View v, final int position, long id)
			{
				AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
				adb.setTitle("You clicked on a Person!");
				adb.setMessage("Selected Person is: " + currentContactsList.get(position).getName());
				adb.setPositiveButton("View profile", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Contact user = currentContactsList.get(position);
						Intent intent = new Intent(((Dialog)dialog).getContext(),OkConnectContactViewActivity.class);
						intent.putExtra("PassedContact", user);
						intent.putExtra("FriendStatus", "STRANGER");
						startActivity(intent);
					}
				});
				adb.show();                     
			}
		});
		
		//TODO: set adapter for currentContactListView
		contactAdapter = new ContactAdapter(this, R.layout.contact_row, currentContactsList);
		currentContactsListView.setAdapter(contactAdapter);
		profileButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), OkConnectProfileActivity.class);
				startActivity(intent);
			}
			
		});
		
		contactsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), OkConnectContactActivity.class);
				startActivity(intent);
			}
			
		});
	}
	
	public Activity getActivity () {
		return this;
	}
	
	public void updateUI () {
		this.runOnUiThread(new Runnable () {
			public void run () {
				contactAdapter.notifyDataSetChanged();
				currentContactsListView.invalidate();
			}
		});
	}
	
	public void loadDataFromFile() {
		//TODO: load selfContact from file
		try {
			selfContact = Contact.readFromFile(this, getString(R.string.self_contact_file_name));
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
		//TODO: load myAffiliations from file
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
		if (myAffiliations == null) {
			myAffiliations = new Affiliations();
		}
	}
	
	@Override
	public void onServiceConnected() {
		service.setActivity(this);
		service.update();
		contactsListChanged();
	}
	
	public void contactsListChanged () {
		if (service != null) {
			this.currentContactsList = service.getContactsList();
			for (Contact contact : this.currentContactsList) {
				contact.setMatchPercentage(contact.compareInterests(this.selfContact.getInterests()));
			}
			contactAdapter.updateItems(currentContactsList);
			updateUI();
		}
	}
	
	public void setSelfContact (Contact contact) {
		this.selfContact = contact;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause () {
		try {
			Contact.writeToFile(this, getString(R.string.self_contact_file_name), selfContact);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Affiliations.writeToFile(this, getString(R.string.my_affiliations_file_name), myAffiliations);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		super.onPause();
	}
}
