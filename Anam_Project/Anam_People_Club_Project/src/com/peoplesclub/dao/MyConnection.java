package com.peoplesclub.dao;

import java.sql.*;

public class MyConnection {
	
	// Connecting to Oracle SQL 10g
	private final static String driver="oracle.jdbc.OracleDriver";
	private final static String url="jdbc:oracle:thin:@localhost:1521:xe";
	private final static String userName="system";
	private final static String password="anam2003";
	static Connection con;
	public static Connection getConnection(){
		try {
			Class.forName(driver);
			con=DriverManager.getConnection(url,userName,password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}
	public static void closeResources(AutoCloseable... resources) {
		for (AutoCloseable resource : resources) {
			if (resource != null) {
				try {
					resource.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
