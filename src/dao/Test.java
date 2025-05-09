package dao;


import java.sql.*;
public class Test {
	public static void main(String[]args) {
		Connection Con=SingletonConnection.getInstance();
		System.out.println("connection stablish");

	}

}
