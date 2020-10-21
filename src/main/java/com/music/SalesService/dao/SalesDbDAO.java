package com.music.SalesService.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static com.music.SalesService.dao.DBConstants.USER_TABLE;
import static com.music.SalesService.dao.DBConstants.INVOICE_TABLE;
import static com.music.SalesService.dao.DBConstants.LINEITEM_TABLE;
import static com.music.SalesService.dao.DBConstants.SYS_TABLE;

@Repository
public class SalesDbDAO {
	@Autowired
	private DataSource dataSource;
	@Autowired
	private DbUtils dbUtil;

	public SalesDbDAO(DbUtils dbUtil,DataSource ds) throws SQLException {
		this.dataSource = ds;
		this.dbUtil = dbUtil;
	}

	public void initializeDb() throws SQLException {
		System.out.println("initDB dataSource = " + dataSource);
		Connection connection = dataSource.getConnection();
		System.out.println("initDB conn = " + connection);
		dbUtil.clearTable(connection, LINEITEM_TABLE);
		dbUtil.clearTable(connection, INVOICE_TABLE);
		dbUtil.clearTable(connection, USER_TABLE);
		initSysTable(connection);
		connection.close();
	}

	private void initSysTable(Connection connection) throws SQLException {
		Statement stmt = connection.createStatement();
		try {
			stmt.execute("update " + SYS_TABLE + " set invoice_id = 1, user_id = 1, lineitem_id = 1"); 
		} finally {
			stmt.close();
		}
	}
	
	// The following transaction control methods are duplicated
	// in the CatalogDbDAO, but need to be part of the particular DB's API,
	// so the duplication is tolerated.
	
	public Connection startTransaction() throws SQLException {
		System.out.println("Entered Sales Dao Layer...");
		Connection connection = dataSource.getConnection();
		connection.setAutoCommit(false);
		return connection;
	}

	public void commitTransaction(Connection connection) throws SQLException {
		// the commit call can throw, and then the caller needs to rollback
		connection.commit();
		connection.close();
	}

	public void rollbackTransaction(Connection connection) throws SQLException {
		connection.rollback();
		connection.close();
	}
	
	// If the caller has already seen an exception, it probably
	// doesn't want to handle a failing rollback, so it can use this.
	// Then the caller should issue its own exception based on the
	// original exception.
	public void rollbackAfterException(Connection connection) {
		try {
			rollbackTransaction(connection);
		} catch (Exception e) {	
			// discard secondary exception--probably server can't be reached
		}
		try {
			connection.close();
		} catch (Exception e) {
			// discard secondary exception--probably server can't be reached
		}
	}

}
