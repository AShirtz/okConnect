package com.intel.c3.okconnect;

import java.io.Serializable;

public class Interest implements Serializable {

	public static final int MAX_RATING = 3;
	
	private String interest = null;
	private int rating = 0;
	
	public Interest (String i, int r) {
		this.setInterest(i);
		if (r > MAX_RATING) { r = MAX_RATING; }
		this.setRating(r);
	}

	public String getInterest() {
		return interest;
	}

	public void setInterest(String interest) {
		this.interest = interest;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}
	
	public boolean equals(Interest i) {
		return this.interest.equals(i.getInterest());
	}
	
	public int compareTo (Interest i) {
		return this.interest.compareToIgnoreCase(i.getInterest());
	}
}
