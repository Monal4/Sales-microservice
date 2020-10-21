package com.music.SalesService.ui.controllers.beans;

public class CartItemBean {
	
	private long Id;
	private int quantity;
	
	public CartItemBean(long Id, int quantity) {
		this.Id = Id;
		this.quantity = quantity;
	}
	
	public void setId(long Id) {
		this.Id = Id;
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public long getId() {
		return Id;
	}
	
	public int getQuantity() {
		return quantity;
	}

}
