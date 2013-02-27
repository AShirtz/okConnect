package com.intel.c3.okconnect;

import java.io.IOException;
import java.io.StreamCorruptedException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.AdapterView.OnItemClickListener;

public class OkConnectProfileActivity extends Activity {
	
	private Button homeButton = null;
	private Button contactsButton = null;
	private EditText nameEditText = null;
	private EditText emailEditText = null;
	private RatingBar interestRatingBar = null;
	private EditText interestEditText = null;
	private Button interestAddButton = null;
	private ListView interestListView = null;
	private InterestAdapter interestAdapter = null;
	
	private Contact selfContact = null;
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ok_connect_profile);
		initializeUI();
	}
	
	private void initializeUI () {
		homeButton = (Button) findViewById(R.id.Home_Button_From_Profile);
		contactsButton = (Button) findViewById(R.id.Contact_Button_From_Profile);
		nameEditText = (EditText) findViewById(R.id.Name_Edit_Text);
		emailEditText = (EditText) findViewById(R.id.E_Mail_Edit_Text);
		interestRatingBar = (RatingBar) findViewById(R.id.Interest_Rating_Bar);
		interestAddButton = (Button) findViewById(R.id.Interest_Add_Button);
		interestEditText = (EditText) findViewById(R.id.Interest_Edit_Text);
		interestListView = (ListView) findViewById(R.id.Interest_ListView);
		
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
		
		interestAdapter = new InterestAdapter(this, R.layout.interest_row, selfContact.getInterests());
		interestListView.setAdapter(interestAdapter);
		
		interestListView.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View v, final int position, long id)
			{
				AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
				adb.setTitle("Delete?");
				adb.setMessage("Are you sure you want to delete " + selfContact.getInterests().get(position).getInterest() + "?");
				adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						selfContact.getInterests().remove(position);
						updateUI();
					}
				});
				adb.show();                     
			}
		});
		this.updateUI();
		
		homeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), OkConnectMainActivity.class);
				startActivity(intent);
			}
		});
		
		contactsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), OkConnectContactActivity.class);
				startActivity(intent);
			}
			
		});
		
		interestAddButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String interest = null;
				interest = interestEditText.getText().toString();
				int rating = 0;
				rating = (int) interestRatingBar.getRating();
				if (interest.length() > 0) {
					selfContact.addInterest(new Interest(interest, rating));
					interestAdapter.updateItems(selfContact.getInterests());
				}
				updateUI();
			}
			
		});
	}
	
	public Activity getActivity () {
		return this;
	}
	
	private void updateUI () {
		updateContact();
		if (selfContact != null) {
			this.runOnUiThread(new Runnable() {
				public void run() {
					nameEditText.setText(selfContact.getName());
					emailEditText.setText(selfContact.getEmail());
				}
			});
		}
		if (interestListView != null) {
			this.runOnUiThread(new Runnable() {
				public void run() {
					interestAdapter.notifyDataSetChanged();
					interestListView.invalidate();
				}
			});
		}
	}
	
	public void updateContact () {
		if (selfContact != null) {
			if (!TextUtils.isEmpty(nameEditText.getText().toString())) {
				selfContact.setName(nameEditText.getText().toString());
			}
			if (!TextUtils.isEmpty(emailEditText.getText().toString())) {
				selfContact.setEmail(emailEditText.getText().toString());
			}
		}
	}
	
	@Override
	protected void onPause () {
		updateContact();
		try {
			Contact.writeToFile(this, getString(R.string.self_contact_file_name), selfContact);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onPause();
	}
}
