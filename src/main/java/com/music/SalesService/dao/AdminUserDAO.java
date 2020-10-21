package com.music.SalesService.dao;

import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.music.SalesService.dao.DBConstants.ADMIN_TABLE;

@Repository
public class AdminUserDAO {

	public Boolean findAdminUser(Connection connection, String uid, String pwd) throws SQLException {
		System.out.println("Entered DAO layer:-");
		Statement stmt = connection.createStatement();
		try {
			ResultSet set = stmt.executeQuery(" select * from " + ADMIN_TABLE +
					" where username = '" + uid + "'" +
					" and password = '" + pwd + "'");
			if (set.next()) {
				set.close();
				return true;
			}
		} finally {
			stmt.close();
		}
		return false;
	}
}
