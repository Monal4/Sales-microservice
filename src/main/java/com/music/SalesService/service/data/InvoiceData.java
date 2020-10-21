package com.music.SalesService.service.data;

import com.music.SalesService.domain.Invoice;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class InvoiceData implements Serializable {

	private static final long serialVersionUID = 1L;
	private long invoiceId;
	private String userFullName;
	private String userAddress;
	private Date invoiceDate;
	private BigDecimal totalAmount;
	private boolean isProcessed;
	
	public InvoiceData () {}
	
	public InvoiceData(Invoice i) {
		invoiceId = i.getInvoiceId();
		userFullName = i.getUser().getFirstname() + " " + i.getUser().getLastname();
		userAddress = i.getUser().getAddress();
		invoiceDate = i.getInvoiceDate();
		totalAmount = i.getTotalAmount();
		isProcessed = i.isProcessed();
	}
	
	public long getInvoiceId() {
		return invoiceId;
	}
	
	public String getUserFullName() {
		return userFullName;
	}
	
	public String getUserAddress() {
		return userAddress;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	
	// getter for boolean: isX, not getIsX
	public boolean isProcessed() {
		return isProcessed;
	}

}
