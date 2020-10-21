
package com.music.SalesService.dao;

import com.music.SalesService.domain.User;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.music.SalesService.dao.DBConstants.USER_TABLE;
import static com.music.SalesService.dao.DBConstants.SYS_TABLE;

@Repository
public class UserDAO {

//	public UserDAO() {
//	}

    public void insertUser(Connection connection, User usr) throws SQLException {
        Statement stmt = connection.createStatement();
        int userId = getNextUserID(connection);
        usr.setId(userId);
        try {
            String simplifiedLastname = usr.getLastname().replace("'", ""); //remove 's
            String sqlString = "insert into " + USER_TABLE +
                    " (user_id, firstname, lastname, email_address, address1) values ("
                    + usr.getId() + ", '" + usr.getFirstname() + "', '"
                    + simplifiedLastname + "', '" + usr.getEmailAddress() + "', '"
                    + usr.getAddress() + "') ";
            stmt.execute(sqlString);
        } finally {
            stmt.close();
        }
    }

    public void updateUserAddress(Connection connection, User usr) throws SQLException {
        Statement stmt = connection.createStatement();
        try {
            String sqlString = "update " + USER_TABLE +
                    " set address1 = '" + usr.getAddress() + "'" +
                    " where email_address = '" + usr.getEmailAddress() + "'";
            stmt.execute(sqlString);
        } finally {
            stmt.close();
        }
    }

    private void advanceUserID(Connection connection) throws SQLException {
        Statement stmt = connection.createStatement();
        try {
            stmt.executeUpdate(" update " + SYS_TABLE
                    + " set user_id = user_id + 1");
        } finally {
            stmt.close();
        }
    }

    public int getNextUserID(Connection connection) throws SQLException {
        int nextUID;
        Statement stmt = connection.createStatement();
        try {
            ResultSet set = stmt.executeQuery(" select user_id from " + SYS_TABLE);
            set.next();
            nextUID = set.getInt("user_id");
        } finally {
            stmt.close();
        }
        advanceUserID(connection);
        return nextUID;
    }

    public User findUserByID(Connection connection, long userId) throws SQLException {
        User usr = null;
        Statement stmt = connection.createStatement();
        try {
            ResultSet set = stmt.executeQuery(" select * from " + USER_TABLE +
                    " where user_id = " + userId);
            if (set.next()) {
                usr = new User();
                usr.setId(set.getInt("user_id"));
                usr.setFirstname(set.getString("firstname"));
                usr.setLastname(set.getString("lastname"));
                usr.setEmailAddress(set.getString("email_address"));
                usr.setAddress(set.getString("address1"));
                set.close();
            }
        } finally {
            stmt.close();
        }
        return usr;
    }

    public User findUserByEmail(Connection connection, String email) throws SQLException {
        User usr = null;
        Statement stmt = connection.createStatement();
        try {
            ResultSet set = stmt.executeQuery(" select * from " + USER_TABLE +
                    " where email_address = '" + email + "'");
            if (set.next()) {
                usr = new User();
                usr.setId(set.getInt("user_id"));
                usr.setFirstname(set.getString("firstname"));
                usr.setLastname(set.getString("lastname"));
                usr.setEmailAddress(set.getString("email_address"));
                usr.setAddress(set.getString("address1"));
                set.close();
            }
        } finally {
            stmt.close();
        }
        return usr;
    }

}
