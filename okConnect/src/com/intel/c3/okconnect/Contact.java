package com.intel.c3.okconnect;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class Contact implements Serializable {

	private UUID id = null;
	private String name = null;
	private String email = null;
	List<Interest> interests = null;
	private double matchPercentage = 0.0;
	transient private Bitmap avatar = null;
	
	public Contact (String n, UUID i, List<Interest> l, String e, Bitmap b) {
		this.setName(n);
		this.setId(i);
		this.setEmail(e);
		if (l == null) {
			interests = new ArrayList<Interest>();
		}
		else {
			interests = l;
		}
		this.setAvatar(b);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}
	
	public List<Interest> getInterests() {
		return this.interests;
	}
	
	public void addInterest (Interest I) {
		boolean foundInterest = false;
		for (Interest J : interests) {
			if (J.compareTo(I) == 0) {
				foundInterest = true;
				J.setRating(I.getRating());
			}
		}
		if (!foundInterest) {
			this.interests.add(I);
		}
	}
	
	public double compareInterests (List<Interest> list) {
		double result = 0.0;
		if (this.interests != null && list != null) {
			for (Interest I : list) {
				for (Interest J : this.interests) {
					if (I.compareTo(J) == 0) { result += I.getRating() + J.getRating(); }
				}
			}
		}
		result = ((result)/(((list.size())+(this.interests.size()))*Interest.MAX_RATING)) * 100;
		this.setMatchPercentage(result);
		return result;
	}

	public Bitmap getAvatar() {
		return avatar;
	}

	public void setAvatar(Bitmap avatar) {
		this.avatar = avatar;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public boolean equals(Contact contact) {
		return this.id.equals(contact.getId());
	}

	public static Contact readFromFile(Context context, String fileName) throws StreamCorruptedException, IOException, ClassNotFoundException {
		Contact result = null;
		FileInputStream fileIn = null;
		fileIn = context.openFileInput(fileName);
		ObjectInputStream objectIn = null;
		if (fileIn != null) {
			objectIn = new ObjectInputStream (fileIn);
		}
		if (objectIn != null) {
			result = (Contact) objectIn.readObject();
			objectIn.close();
			fileIn.close();
		}
		return result;
	}
	
	public static void writeToFile (Context context, String fileName, Contact contact) throws IOException {
		if (contact == null) { return; }
		FileOutputStream fileOut = null;
		fileOut = context.openFileOutput(fileName, Context.MODE_PRIVATE);
		ObjectOutputStream objectOut = null;
		if (fileOut != null) {
			objectOut = new ObjectOutputStream(fileOut);
		}
		if (objectOut != null) {
			objectOut.writeObject(contact);
			objectOut.close();
			fileOut.close();
		}
	}

	public double getMatchPercentage() {
		return matchPercentage;
	}

	public void setMatchPercentage(double matchPercentage) {
		this.matchPercentage = matchPercentage;
	}
}
