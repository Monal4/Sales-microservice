package com.music.SalesService.dao;

import com.music.SalesService.domain.Invoice;
import com.music.SalesService.domain.LineItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import static com.music.SalesService.dao.DBConstants.*;

@Repository
public class InvoiceDAO {
	@Autowired
	private UserDAO userdb;
	@Autowired
	private LineItemDAO lineitemdb;

	public InvoiceDAO(LineItemDAO linedb, UserDAO udb, ProductDAO prddb)  {
		lineitemdb = linedb;
		userdb = udb;
	}

	private void advanceInvoiceID(Connection connection) throws SQLException
	{
		Statement stmt = connection.createStatement();
		try {
			stmt.executeUpdate(" update " + SYS_TABLE
					+ " set invoice_id = invoice_id + 1");
		} finally {
			stmt.close();
		}
	}

	private int getNextInvoiceID(Connection connection) throws SQLException
	{
		int nextIID;
		Statement stmt = connection.createStatement();
		try {
			ResultSet set = stmt.executeQuery(" select invoice_id from " + SYS_TABLE);
			set.next();
			nextIID = set.getInt("invoice_id");
		} finally {
			stmt.close();
		}
		advanceInvoiceID(connection); // the id has been taken, so set +1 for next one
		return nextIID;
	}

	public void insertInvoice(Connection connection, Invoice invoice)throws SQLException{
		Statement stmt = connection.createStatement();
		int invoiceID =  getNextInvoiceID(connection);
		invoice.setInvoiceId(invoiceID);
		String YN = "";
		if (invoice.isProcessed()){
			YN = "y";
		}else{
			YN = "n";
		}
		try{
			String sqlString = "insert into "+ INVOICE_TABLE + " values (" +
			invoiceID + ", " + 
			invoice.getUser().getId()+ " , " + "current_timestamp, " +
			// dbDAO.formatTimestamp(invoice.getInvoiceDate()) + ", " +   // another way
			invoice.getTotalAmount().toPlainString() + ", " +
			"'" + YN +"')";
			stmt.execute(sqlString);
			for (LineItem item: invoice.getLineItems()){
			   lineitemdb.insertLineItem(connection, invoiceID, item);
			}
		} finally {
			stmt.close();
		}
	}

	public Invoice findInvoice(Connection connection, long invoiceId) throws SQLException{
		Invoice invoice = null;
		Statement stmt = connection.createStatement();
		try 
		{
			String sqlString =  " select * from " + 
			INVOICE_TABLE + " i, " +
			LINEITEM_TABLE + " l " +		
			" where i.invoice_id = " + invoiceId + 
			" and i.invoice_id = l.invoice_id ";
			ResultSet set = stmt.executeQuery(sqlString);
			if (set.next()){ // if the result is not empty
				// first row: set up Invoice from invoice columns
				invoice= new Invoice(set.getInt("invoice_id"),
						userdb.findUserByID(connection, set.getInt("user_id")),
						set.getTimestamp("invoice_date"),
						set.getString("is_processed") == "y",
						null,// items added below
						set.getBigDecimal("total_amount"));
				Set<LineItem> items = new HashSet<LineItem>();
				LineItem item = new LineItem(set.getLong("lineitem_id"), set.getString("product_code"), invoice, set.getInt("quantity"));
				items.add(item);
				while (set.next()){ // if the invoice has more than one item
					item = new LineItem(set.getLong("lineitem_id"), set.getString("product_code"), invoice, set.getInt("quantity"));
					items.add(item);
				}
				invoice.setLineItems(items);
			}
			set.close();
		} finally {
			stmt.close();
		}
		
		return invoice;
	}

	public Set<Invoice> findAllUnprocessedInvoices(Connection connection) throws SQLException{
		Set<Invoice> invoices = new HashSet<Invoice>();
		Statement stmt = connection.createStatement();
		Invoice invoice;
		
		String sqlString =  " select invoice_id from " + INVOICE_TABLE  +		
		" where is_processed = 'n'";
		try{
			ResultSet set = stmt.executeQuery(sqlString);
			while (set.next()){
				invoice = this.findInvoice(connection, set.getInt("invoice_id"));
				invoices.add(invoice);
			}
			set.close();
		}finally{
			stmt.close();
		}
		return invoices;
	}

	public Set<Invoice> findAllInvoices(Connection connection) throws SQLException{
		Set<Invoice> invoices = new HashSet<Invoice>();
		Statement stmt = connection.createStatement();
		Invoice invoice;
		
		String sqlString =  " select invoice_id from " + INVOICE_TABLE ;
		try{

			ResultSet set = stmt.executeQuery(sqlString);
			while (set.next()){
				invoice = this.findInvoice(connection, set.getInt("invoice_id"));
				invoices.add(invoice);
			}
			set.close();
		}finally{
			stmt.close();
		}
		return invoices;
	}

	public void updateInvoice(Connection connection, Invoice i)throws SQLException{
		Statement stmt = connection.createStatement();
		try{
			String sqlString = "update "+ INVOICE_TABLE + " set is_processed = 'y'" +
			" where invoice_id = " + i.getInvoiceId() ;
			stmt.execute(sqlString);
		} finally {
			stmt.close();
		}
	}
}
