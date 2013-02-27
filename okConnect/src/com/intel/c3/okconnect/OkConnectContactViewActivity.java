package com.intel.c3.okconnect;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class OkConnectContactViewActivity extends Activity {
	
	private Button toProfile, toHome, toContact, FriendBtn, BlockBtn, RemoveBtn;
	TextView userName, eMail;
	Intent INTENT;
	String ERROR;
	String FriendStatus;
	InterestAdapter interestAdapter;
	ArrayList<Interest> interests = new ArrayList<Interest>();
	private ArrayList<Interest> listHolder;
	private Affiliations myAffiliations;
	Contact user;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	    setContentView(R.layout.ok_connect_contact_view_activity);
    
	    INTENT = getIntent();
	    FriendStatus = INTENT.getStringExtra("FriendStatus");
	    user = (Contact) INTENT.getSerializableExtra("PassedContact");
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
	}
	
	public void init(){
		toProfile = (Button) findViewById(R.id.Profile_Button_From_Contact_View);
		toHome = (Button) findViewById(R.id.Home_Button_From_Contact_View);
		toContact = (Button) findViewById(R.id.Contact_Button_From_Contact_View);
		FriendBtn = (Button) findViewById(R.id.Friend_User_Button);
		BlockBtn = (Button) findViewById(R.id.Block_User_Button);
		RemoveBtn = (Button) findViewById(R.id.Remove_User_Button);
		userName = (TextView) findViewById(R.id.Name_Text_View);
		eMail = (TextView) findViewById(R.id.E_Mail_Text_View);
		
		userName.setText(user.getName());
		eMail.setText(user.getEmail());
		if(FriendStatus.compareTo("FRIENDED") == 0) FriendBtn.setVisibility(View.GONE);
	    if(FriendStatus.compareTo("BLOCKED") == 0)  BlockBtn.setVisibility(View.GONE);
	    if(FriendStatus.compareTo("STRANGER") == 0) RemoveBtn.setVisibility(View.GONE);
		
		toHome.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent (getApplicationContext(), OkConnectMainActivity.class);
				startActivity(intent);
			}
		});
		toProfile.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent (getApplicationContext(), OkConnectProfileActivity.class);
				startActivity(intent);
			}
		});
		toContact.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent (getApplicationContext(), OkConnectContactActivity.class);
				startActivity(intent);
			}
		});
		FriendBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				myAffiliations.addFriended(user);
			}
		});
		BlockBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				myAffiliations.addBlocked(user);
			}
		});
		RemoveBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				for (Contact contact : myAffiliations.getBlocked()) {
					if (contact.getId().compareTo(user.getId()) == 0) {
						myAffiliations.getBlocked().remove(contact);
					}
				}
				for (Contact contact : myAffiliations.getFriended()) {
					if (contact.getId().compareTo(user.getId()) == 0) {
						myAffiliations.getFriended().remove(contact);
					}
				}
			}
		});
		
		ListView InterestsList = (ListView) findViewById(R.id.Other_Users_ListView);
		listHolder = (ArrayList<Interest>) user.getInterests();
		interestAdapter = new InterestAdapter(this, R.layout.contact_row, listHolder);
		InterestsList.setAdapter(interestAdapter);
		
		
	}
	
	@Override
	public void onPause(){
		try {
			Affiliations.writeToFile(this, getString(R.string.my_affiliations_file_name), myAffiliations);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onPause();
	}
	
}
