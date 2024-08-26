package com.peoplesclub.model;

// Membership class
public class Membership {

	private int membershipId;
	private String membershipName;
	private int validityMonths;
	private String benefits; 

	// Constructor
	public Membership() {}

	public Membership(int membershipId, String membershipName, int validityMonths, String benefits) {
		super();
		this.membershipId = membershipId;
		this.membershipName = membershipName;
		this.validityMonths = validityMonths;
		this.benefits = benefits;
	}

	// Getters and Setter
	public String getBenefits() {
		return benefits;
	}

	public void setBenefits(String benefits) {
		this.benefits = benefits;
	}

	public int getMembershipId() {
		return membershipId;
	}
	
	public void setMembershipId(int membershipId) {
		this.membershipId = membershipId;
	}
	
	public String getMembershipName() {
		return membershipName;
	}
	
	public void setMembershipName(String membershipName) {
		this.membershipName = membershipName;
	}
	
	public int getValidityMonths() {
		return validityMonths;
	}
	
	public void setValidityMonths(int validityMonths) {
		this.validityMonths = validityMonths;
	}

	public void displayMembershipDetails() {
		System.out.println("Membership Name: "+getMembershipName());
		System.out.println("Benefits: "+getBenefits());
	}
}
