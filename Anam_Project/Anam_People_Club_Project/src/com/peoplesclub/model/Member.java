package com.peoplesclub.model;

// Member class
import java.sql.Date;

public class Member {

	private int memberId;
	private String firstName;
	private String lastName;
	private String email;
	private String phone;
	private int membershipId;
	private Date registrationDate;
	private Date expiryDate;
	private String password;

	public Member() {}

	public Member(int memberId, String firstName, String lastName, String email, String phone, int membershipId,
			Date registrationDate, Date expiryDate, String password) {
		super();
		this.memberId = memberId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phone = phone;
		this.membershipId = membershipId;
		this.registrationDate = registrationDate;
		this.expiryDate = expiryDate;
		this.password = password;
	}

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getMembershipId() {
		return membershipId;
	}

	public void setMembershipId(int membershipId) {
		this.membershipId = membershipId;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public void displayMemberDetails() {
		System.out.println("--------------------------------");
		System.out.println("Member ID: "+memberId);
		System.out.println("Member Name: "+firstName+" "+lastName);
		System.out.println("Member Email: "+email);
		System.out.println("Member Phone: "+phone);
		System.out.println("Membership ID: "+membershipId);
		System.out.println("Membership registration Date: "+registrationDate);
		System.out.println("Membership Expiry Date: "+expiryDate);	
	}
}
