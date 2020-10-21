package com.music.SalesService.dao;

import com.music.SalesService.domain.LineItem;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.music.SalesService.dao.DBConstants.LINEITEM_TABLE;
import static com.music.SalesService.dao.DBConstants.SYS_TABLE;


@Repository
public class LineItemDAO {

//	public LineItemDAO(DbUtils db) {
//
//	}

	public void insertLineItem(Connection connection, long invoiceID, LineItem item) throws SQLException{
		Statement stmt = connection.createStatement();
		int lineitem_id = getNextLineItemID(connection);
		item.setId(lineitem_id);
		try {
			String sqlString = "insert into " + LINEITEM_TABLE + 
			" (lineitem_id, invoice_id, product_code, quantity) values ("
			+ item.getId() + ", " + invoiceID + ", '"
			+ item.getProductCode() + "', " + item.getQuantity() + ") ";
			stmt.execute(sqlString);
		} finally {
			stmt.close();
		}
	}

	private void advanceLineItemID(Connection connection) throws SQLException
	{
		Statement stmt = connection.createStatement();
		try {
			stmt.executeUpdate(" update " + SYS_TABLE
					+ " set lineitem_id = lineitem_id + 1");
		} finally {
			stmt.close();
		}
	}

	private int getNextLineItemID(Connection connection) throws SQLException
	{
		int nextLID;
		Statement stmt = connection.createStatement();
		try {
			ResultSet set = stmt.executeQuery(" select lineitem_id from " + SYS_TABLE);
			set.next();
			nextLID = set.getInt("lineitem_id");
		} finally {
			stmt.close();
		}
		advanceLineItemID(connection);
		return nextLID;
	}
}
