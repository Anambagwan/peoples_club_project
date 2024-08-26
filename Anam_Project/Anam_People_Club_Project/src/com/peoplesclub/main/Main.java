package com.peoplesclub.main;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Scanner;

import com.peoplesclub.dao.MemberDAO;
import com.peoplesclub.model.Member;
import com.peoplesclub.model.Membership;

public class Main {

	private static MemberDAO memberService = new MemberDAO();

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		int choice = 0;
		char ch;
		try {
			do {
				System.out.println();
				System.out.println("*************************************");
				System.out.println("***** WELCOME TO PEOPLE'S CLUB ******");
				System.out.println("*************************************");
				System.out.println("1. Register as a New Member");
				System.out.println("2. Login as a Member");
				System.out.println("3. Admin Login");
				System.out.println("4. Exit");
				System.out.print("Please enter your choice: ");
				try {
					choice = sc.nextInt();
					sc.nextLine();  
				} catch(Exception e) {
					System.out.println("Invalid Choice!");
				}
				try {
					switch (choice) {
					case 1:
						// New member registration
						registerMember(sc);
						break;
						
					case 2:
						// Member Login
						memberLogin(sc);
						break;
						
					case 3:
						// Admin Login
						adminLogin(sc);
						break;
						
					case 4:
						// Exiting application
						System.out.println("Exiting... Thank you!");
						return;
						
					default:
						System.out.println("Invalid option, please try again.");
					}
				} catch (SQLException e) {
					System.out.println("Database error occurred.");
				}

				System.out.print("Do you want to go to main menu? Press Y: ");
				ch = sc.nextLine().charAt(0);

			} while(ch == 'y' || ch == 'Y');
		} catch(Exception e) {
			System.out.println("Invalid Choice!");
		}
		System.out.println("Thank you!!");
	}

	// Register new member
	private static void registerMember(Scanner sc) throws SQLException {
		System.out.println("-------------------------------------");
		System.out.println("------ Register New Member ----------");
		System.out.println("-------------------------------------");
		System.out.print("Enter your First Name: ");
		String fname = sc.nextLine();
		System.out.print("Enter your Last Name: ");
		String lname = sc.nextLine();
		System.out.print("Enter your Email: ");
		String email = sc.nextLine();
		System.out.print("Enter your Phone Number: ");
		String phone = sc.nextLine();

		System.out.println("Choose Membership Type: ");
		System.out.println("--------MEMBERSHIP SCHEMES------------");
		memberService.getAllMembershipDetails();
		System.out.print("Enter Membership ID: ");
		int membershipId = sc.nextInt();
		sc.nextLine();
		System.out.print("Set Your Password: ");
		String password = sc.nextLine();
		System.out.println("Thank you entering details... Please wait...");

		// Creating a new Member object
		Member newMember = new Member(
				0, 
				fname,
				lname,
				email,
				phone,
				membershipId,
				new Date(System.currentTimeMillis()), // Current date
				new Date(System.currentTimeMillis()), // Expiry date
				MemberDAO.hashPassword(password) // Hashed password
				);

		// Register the member
		int i = memberService.registerMember(newMember);

		if(i>0){
			System.out.println("Thank you!!");
		}
		else{
			System.out.println("Insertion Failed!");
		}	
	}

	// Method for member login
	private static void memberLogin(Scanner sc) throws SQLException {
		System.out.println("-------------------------------------");
		System.out.println("---------- Member Login -------------");
		System.out.println("-------------------------------------");
		System.out.print("Enter Your Member ID: ");
		int memberId = sc.nextInt();  
		sc.nextLine();
		System.out.print("Enter your password: ");
		String password = sc.nextLine();
		System.out.println();
		boolean success = memberService.login(memberId, password);
		if (success) {
			System.out.println("Login successful!");
			memberMenu(sc, memberId);  
		} else {
			System.err.println("Login failed. Invalid Member ID or password. Try again!");
			System.out.println("Note: If new user, try registering yourself!");
		}
	}

	// Method for admin login
	private static void adminLogin(Scanner sc) throws SQLException {
		System.out.println("-------------------------------------");
		System.out.println("------------ Admin Login ------------");
		System.out.println("-------------------------------------");
		System.out.print("Enter Admin Username: ");
		String adminUsername = sc.nextLine();
		System.out.print("Enter Admin Password: ");
		String adminPassword = sc.nextLine();

		boolean success = memberService.adminLogin(adminUsername, adminPassword);
		if (success) {
			System.out.println("Admin Login Successful!");
			adminMenu(sc);
		} else {
			System.err.println("Admin login failed. Invalid credentials.");
		}
	}

	// Member-specific menu after login
	private static void memberMenu(Scanner sc, int memberId) throws SQLException {
		while (true) {
			System.out.println("-------------------------------------");
			System.out.println("------------ Member Menu ------------");
			System.out.println("-------------------------------------");
			System.out.println("1. View Personal & Membership Details");
			System.out.println("2. Update Personal Details");
			System.out.println("3. Logout");
			System.out.print("Enter your choice: ");
			int choice = sc.nextInt();
			sc.nextLine();  

			switch (choice) {
			
			case 1:
				// View Personal & Membership Details
				Map<String, Object> result = memberService.viewMembershipDetails(memberId);
				Member member = (Member) result.get("member");
				Membership membership = (Membership) result.get("membership");
				member.displayMemberDetails();
				membership.displayMembershipDetails();
				
				LocalDate localExpiryDate = member.getExpiryDate().toLocalDate();
				long month = memberService.calculateRemainingMonths(localExpiryDate);
				System.out.println("Month Remaining for Membership to Expire: "+month+" Months!");
				if(month > 0) {
					System.out.println("Membership Status: Active");
				}
				else {
					System.out.println("Membership Status: Expired");
				}
				break;
				
			case 2:
				// Update Personal Details
				System.out.print("What do you want to update? \n1: Email\n2. Phone\n3. Password\nEnter your Choice: ");
				int ch = sc.nextInt();
				sc.nextLine();
				System.out.println();
				
				int i = memberService.updateMembership(ch,memberId);
				if(i>0){
					System.out.println("Details Updated Successfully!!");
				}
				else{
					System.out.println("Updation Failed!");
				}	
				break;
				
			case 3:
				// Logging out
				System.out.println("Logging out...");
				return;
				
			default:
				System.out.println("Invalid option, please try again.");
			}
		}
	}

	// Admin-specific menu after login
	private static void adminMenu(Scanner sc) throws SQLException {
		while (true) {
			System.out.println("-------------------------------------");
			System.out.println("------------ Admin Menu -------------");
			System.out.println("-------------------------------------");
			System.out.println("1. View All Members");
			System.out.println("2. Add New Membership Scheme");
			System.out.println("3. Modify Existing Membership Scheme");
			System.out.println("4. Remove Membership Scheme");
			System.out.println("5. View all Membership Scheme");
			System.out.println("6. Logout");
			System.out.print("Enter your choice: ");
			int choice = sc.nextInt();
			sc.nextLine(); 

			switch (choice) {
			case 1:
				// Displaying all members Details
				memberService.viewAllMembers();
				break;
				
			case 2:
				// Adding new membership scheme
				System.out.print("Enter New Membership Type Name: ");
				String newSchemeName = sc.nextLine();
				System.out.print("Enter Validity of Membership in months: ");
				int new_mon = sc.nextInt();
				sc.nextLine();
				System.out.print("Enter Membership Benefits: ");
				String benefits = sc.nextLine();
				
				int i = memberService.addMembershipScheme(newSchemeName,new_mon, benefits);
				if(i>0){
					System.out.println("Thank you!");
				}
				else{
					System.out.println("Insertion Failed!");
				}	
				break;
				
			case 3:
				// Modify Existing Membership Scheme
				System.out.print("Enter membership type ID to modify: ");
				int id = sc.nextInt();  
				sc.nextLine();  
				
				System.out.print("Enter modified membership type name: ");
				String modifiedSchemeName = sc.nextLine();

				System.out.print("Enter modified Validity of Months: ");
				int new_mon_update = sc.nextInt(); 
				sc.nextLine(); 

				System.out.print("Enter modified membership benefits: ");
				String newBenefits = sc.nextLine();  
				System.out.println("Updating Details....");

				int i2 = memberService.modifyMembershipScheme(id, modifiedSchemeName, new_mon_update, newBenefits);

				if(i2 > 0){
					System.out.println("Membership Updated!");
				} else {
					System.out.println("Updation Failed!");
				}
				break;
				
			case 4:
				// Remove Membership Scheme
				System.out.print("Enter membership type ID to remove: ");
				int remove_id = sc.nextInt();
				sc.nextLine();  
				
				int i3 = memberService.removeMembershipScheme(remove_id);
				if(i3 > 0){
					System.out.println("Membership deleted successfully!");
				} else {
					System.out.println("Deletion Failed!");
				}
				break;
				
			case 5:
				// Displaying all existing membership schemes
				System.out.println("--------MEMBERSHIP SCHEMES------------");
				memberService.getAllMembershipDetails();
				break;
				
			case 6:
				// Logging out
				System.out.println("Logging out... \nThank you!");
				return;
				
			default:
				System.out.println("Invalid option, please try again.");
			}
		}

	}

}
