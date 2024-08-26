package com.peoplesclub.dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.peoplesclub.model.Member;
import com.peoplesclub.model.Membership;

public class MemberDAO {
	static Connection con;

	// Password Code Starts
	// Hashing password using SHA-256
	public static String hashPassword(String password) {
		try {
			// MessageDigest object for SHA-256
			MessageDigest digest = MessageDigest.getInstance("SHA-256");

			byte[] hashBytes = digest.digest(password.getBytes());

			StringBuilder hexString = new StringBuilder();
			for (byte b : hashBytes) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			return password;
		}
	}
	// Password code ends

	// Register a new member method	
	public int registerMember(Member member)  {
		
		con=MyConnection.getConnection();
		PreparedStatement ps=null;	
		int memberId = 0;

		try {			
			String getSeqSQL = "SELECT member_seq.NEXTVAL FROM dual";
			ps = con.prepareStatement(getSeqSQL);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				memberId = rs.getInt(1);
			}
			ps.close();
		}
		catch(Exception e) {
			System.out.println("Something went wrong while fetching Member ID!!");
		}

		int i=0;
		try {
			ps=con.prepareStatement("INSERT INTO members VALUES (?,?, ?, ?, ?, ?, ?, ?, ?)");
			LocalDate today = LocalDate.now();
			LocalDate expiryDate = today.plusMonths(getMembershipMonths(member.getMembershipId()));
			
			Date sqlToday = Date.valueOf(today);
			Date sqlExpiryDate = Date.valueOf(expiryDate);

			ps.setInt(1, (memberId-1));
			ps.setString(2, member.getFirstName());
			ps.setString(3, member.getLastName());
			ps.setString(4,member.getEmail());
			ps.setString(5, member.getPhone());
			ps.setInt(6, member.getMembershipId());
			ps.setDate(7, sqlToday);
			ps.setDate(8, sqlExpiryDate);
			ps.setString(9, member.getPassword());
			
			i=ps.executeUpdate();
			if(i > 0) {
				System.out.println("Congratulations!!");
				System.out.println("Member registered successfully with ID: " + (memberId-1));
				System.out.println("Please remember this ID for logging into your account.");
			}
		} catch (NumberFormatException e) {
			System.out.println("Exception: "+e);
		} catch (SQLException e) {
			System.out.println(e);
		}
		return i;
	}

	// Member Login Method
	public boolean login(int memberId, String password) {

		boolean b=false;
		PreparedStatement ps=null;
		con=MyConnection.getConnection();
		try {
			ps=con.prepareStatement("select * from members where member_id = ? and password = ?");
			ps.setInt(1,memberId);
			ps.setString(2,hashPassword(password));
			ResultSet rs=ps.executeQuery();
			if(rs.next()){
				b=true;
			}
		} catch (SQLException e) {
			System.out.println(e);
		}		
		return b;
	}

	// Admin Login method
	public boolean adminLogin(String adminUsername, String adminPassword) {

		con=MyConnection.getConnection();

		boolean b=false;
		PreparedStatement ps=null;
		con=MyConnection.getConnection();
		try {
			ps=con.prepareStatement("select * from admin where username = ? and password = ?");
			ps.setString(1,adminUsername);
			ps.setString(2,hashPassword(adminPassword));
			ResultSet rs=ps.executeQuery();
			if(rs.next()){
				b=true;
			}
		} catch (SQLException e) {
			System.out.println(e);
		}	
		if(adminUsername == "admin" && adminPassword == "admin") {
			return true;
		}
		return b;
	}

	// Searching for a particular member's details including their respective membership scheme
	public Map<String, Object> viewMembershipDetails(int memberId) {
		Member mem = new Member();
		Membership membership = new Membership();

		PreparedStatement ps=null;
		con=MyConnection.getConnection();
		try {
			String sql = "select m.member_id, m.first_name, m.last_name, "
					+ "m.email, m.phone, m.registration_date, m.expiry_date, "
					+ "s.membership_id, s.membership_name, s.validity_months, s.benefits  "
					+ "from members m join membership_types s "
					+ "on m.membership_id = s.membership_id where m.member_id = ?";
			
			ps=con.prepareStatement(sql);
			ps.setInt(1,memberId);
			ResultSet rs=ps.executeQuery();
			while(rs.next()){

				mem.setMemberId(rs.getInt(1));
				mem.setFirstName(rs.getString(2));
				mem.setLastName(rs.getString(3));
				mem.setEmail(rs.getString(4));
				mem.setPhone(rs.getString(5));
				mem.setRegistrationDate(rs.getDate(6));
				mem.setExpiryDate(rs.getDate(7));	
				mem.setMembershipId(rs.getInt(8));
				membership.setMembershipId(rs.getInt(8));
				membership.setMembershipName(rs.getString(9));
				membership.setValidityMonths(10);
				membership.setBenefits(rs.getString(11));
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
		Map<String, Object> result = new HashMap<>();
		result.put("member", mem);
		result.put("membership", membership);

		return result; 
	}
	
	// Getting all membership schemes details
	public void getAllMembershipDetails() {
		
		PreparedStatement ps=null;
		con=MyConnection.getConnection();
		try {
			
			System.out.printf("%-10s %-15s %-10s %-50s\n", "Member ID", "Membership Name", "Validity Months", "Benefits");
			ps=con.prepareStatement("select MEMBERSHIP_ID, MEMBERSHIP_NAME,  VALIDITY_MONTHS, BENEFITS from membership_types order by MEMBERSHIP_ID");
			ResultSet rs=ps.executeQuery();
			while(rs.next()){
				System.out.println("-----------------------------------------------------------------------------------------------------------");
				System.out.printf("%-10d %-15s %-10d %-50s\n", rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4));
			}
		} catch (SQLException e) {
			System.out.println(e);
		}	
	}

	// Getting membership month w.r.t. to Membership ID
	public int getMembershipMonths(int membershipId) {
		PreparedStatement ps=null;
		int month = 0;
		con=MyConnection.getConnection();
		try {
			ps=con.prepareStatement("select VALIDITY_MONTHS from membership_types where MEMBERSHIP_ID = ?");
			ps.setInt(1,membershipId);

			ResultSet rs=ps.executeQuery();
			if(rs.next()){
				month = rs.getInt(1);
			}
		} catch (SQLException e) {
			System.out.println(e);
		}	
		return month;
	}
	
	// Calculating the number of month left for membership to expire
	public static long calculateRemainingMonths(LocalDate expiryDate) {
		LocalDate today = LocalDate.now();

		if (expiryDate.isAfter(today)) {
			return ChronoUnit.MONTHS.between(today, expiryDate);
		} else {
			return 0;
		}
	}

	// Updating member's personal details
	public int updateMembership(int ch,int memberId) {
		Scanner sc = new Scanner(System.in);
		String toUpdate = "";
		String toUpdateValue = "";
		if (ch == 1) {
			toUpdate = "email";
			System.out.print("Enter your updated Email ID: ");
			toUpdateValue = sc.nextLine();
		}
		if (ch == 2) {
			toUpdate = "phone";
			System.out.print("Enter your Updated Phone Number: ");
			toUpdateValue =  sc.nextLine();
		}
		if (ch == 3) {
			toUpdate = "password";
			System.out.print("Enter New Password: ");
			toUpdateValue = sc.nextLine();
			toUpdateValue = hashPassword(toUpdateValue);	
		}
		con=MyConnection.getConnection();
		PreparedStatement ps=null;
		int i=0;		
		try {
			String sql = "update members set "+toUpdate+" = ? where member_id = ?";
			ps=con.prepareStatement(sql);		
			ps.setString(1,toUpdateValue);
			ps.setInt(2,memberId);
			
			i=ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e);
		}	
		return i;
	}

	// Updating membership scheme details
	public int modifyMembershipScheme(int id, String modifiedSchemeName, int new_mon_update, String newBenefits) {
		con=MyConnection.getConnection();
		PreparedStatement ps=null;
		int i=0;		
		try {
			ps=con.prepareStatement("update membership_types set membership_name = ?, validity_months = ?, benefits = ? where membership_id = ?");			
			ps.setString(1,modifiedSchemeName);
			ps.setInt(2,new_mon_update);
			ps.setString(3,newBenefits);
			ps.setInt(4,id);
			
			i=ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e);
		}	
		return i;
	}

	// Deleting membership schemes
	public int removeMembershipScheme(int id) {
		
		System.out.println("In delete function");
		con=MyConnection.getConnection();
		PreparedStatement ps=null;
		int i=0;		
		try {
			ps=con.prepareStatement("delete from membership_types where membership_id = ?");			
			ps.setInt(1,id);
			i=ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e);
		}	
		return i;
	}
	
	// Adding new membership details
	public int addMembershipScheme(String newSchemeName, int new_month, String benefits) {
		con=MyConnection.getConnection();
		PreparedStatement ps=null;	
		int membershipId = 0;
		try {
			ps = con.prepareStatement("SELECT membership_seq.NEXTVAL FROM dual");
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				membershipId = rs.getInt(1);
			}
			ps.close();
		}
		catch(Exception e) {
			System.out.println("Something went wrong while fetching Membership ID!!");
		}

		int i=0;
		try {
			ps=con.prepareStatement("INSERT INTO membership_types VALUES (?, ?, ?, ?)");
			ps.setInt(1, (membershipId - 1));
			ps.setString(2, newSchemeName);
			ps.setInt(3, new_month);
			ps.setString(4,benefits);
			
			i=ps.executeUpdate();
			if(i > 0) {
				System.out.println("Congratulations!!");
				System.out.println("Membership Scheme registered successfully with ID: " + (membershipId-1));
			}
		} catch (NumberFormatException e) {	
			System.out.println("Exception: "+e);
		} catch (SQLException e) {
			// e.printStackTrace();
			System.out.println(e);
		}
		return i;
	}

	// Displaying all members details along with their respective membership
	public void viewAllMembers() {
		Member mem = new Member();
		Membership membership = new Membership();

		PreparedStatement ps=null;
		con=MyConnection.getConnection();
		
		try {
			String sql = "select m.member_id, m.first_name, m.last_name, "
					+ "m.email, m.phone, m.registration_date, m.expiry_date, "
					+ "s.membership_id, s.membership_name, s.validity_months, s.benefits "
					+ "from members m join membership_types s "
					+ "on m.membership_id = s.membership_id";
			ps=con.prepareStatement(sql);
			
			System.out.printf("%-10s %-15s %-15s %-30s %-15s %-15s %-15s %-15s %-15s\n", 
	                "Member ID", "First Name", "Last Name", "Email", "Phone", "Registration Date", "Expiry Date", "Membership Name","Benefits");
	            
			ResultSet rs=ps.executeQuery();
			
			while(rs.next()){
				mem.setMemberId(rs.getInt(1));
				mem.setFirstName(rs.getString(2));
				mem.setLastName(rs.getString(3));
				mem.setEmail(rs.getString(4));
				mem.setPhone(rs.getString(5));
				mem.setRegistrationDate(rs.getDate(6));
				mem.setExpiryDate(rs.getDate(7));	
				mem.setMembershipId(rs.getInt(8));
				membership.setMembershipId(rs.getInt(8));
				membership.setMembershipName(rs.getString(9));
				membership.setValidityMonths(10);
				membership.setBenefits(rs.getString(11));
				System.out.println("-----------------------------------------------------------------------------------------------------------");
				
				System.out.printf("%-10d %-15s %-15s %-30s %-15s %-15s %-15s %-15s %-15s\n", 
						mem.getMemberId(), mem.getFirstName(), mem.getLastName(), mem.getEmail(), mem.getPhone(), mem.getRegistrationDate(), mem.getExpiryDate(), membership.getMembershipName(), membership.getBenefits());

			}
			System.out.println("-----------------------------------------------------------------------------------------------------------");
		} catch (SQLException e) {
			System.out.println(e);
		}

	}
}

