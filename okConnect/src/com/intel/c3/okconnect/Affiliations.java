package com.intel.c3.okconnect;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;

public class Affiliations implements java.io.Serializable{

	ArrayList<Contact> friended = new ArrayList<Contact>();
	ArrayList<Contact> blocked = new ArrayList<Contact>();
	
	public ArrayList<Contact> getFriended () { return friended; }
	
	public void addFriended (Contact contact) {
		boolean isPresent = false;
		for (Contact A : friended) {
			if (A.getId().compareTo(contact.getId()) == 0) {
				isPresent = true;
			}
		}
		if (!isPresent) {
			friended.add(contact);
		}
		isPresent = false;
		for (Contact A : blocked) {
			if (A.getId().compareTo(contact.getId()) == 0) {
				blocked.remove(A);
			}
		}
	}
	
	public void setFriended (ArrayList<Contact>  list) {
		for (Contact newContact : list) {
			if (blocked.contains(newContact)) { blocked.remove(newContact); }
		}
		friended.addAll(list);
	}
	
	public ArrayList<Contact> getBlocked () { return blocked; }
	
	public void addBlocked (Contact contact) {
		boolean isPresent = false;
		for (Contact A : blocked) {
			if (A.getId().compareTo(contact.getId()) == 0) {
				isPresent = true;
			}
		}
		if (!isPresent) {
			blocked.add(contact);
		}
		isPresent = false;
		for (Contact A : friended) {
			if (A.getId().compareTo(contact.getId()) == 0) {
				friended.remove(A);
			}
		}
	}
	
	public void setBlocked (ArrayList<Contact>  list) {
		for (Contact newContact : list) {
			if (friended.contains(newContact)) { friended.remove(newContact); }
		}
		blocked.addAll(list);
	}
	
	public static Affiliations readFromFile (Activity activity, String fileName) throws StreamCorruptedException, IOException, ClassNotFoundException {
		Affiliations result = null;
		FileInputStream fileIn = null;
		fileIn = activity.openFileInput(fileName);
		ObjectInputStream objectIn = null;
		if (fileIn != null) {
			objectIn = new ObjectInputStream(fileIn);
		}
		if (objectIn != null) {
			result = (Affiliations) objectIn.readObject();
			objectIn.close();
			fileIn.close();
		}
		return result;
	}
	
	public static void writeToFile (Activity activity, String fileName, Affiliations affiliations) throws IOException {
		if (affiliations == null) { return; }
		FileOutputStream fileOut = null;
		fileOut = activity.openFileOutput(fileName, Context.MODE_PRIVATE);
		ObjectOutputStream objectOut = null;
		if (fileOut != null) {
			objectOut = new ObjectOutputStream(fileOut);
		}
		if (objectOut != null) {
			objectOut.writeObject(affiliations);
			objectOut.close();
			fileOut.close();
		}
	}
}
