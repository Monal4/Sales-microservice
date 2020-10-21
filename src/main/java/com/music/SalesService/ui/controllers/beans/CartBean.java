package com.music.SalesService.ui.controllers.beans;

import java.util.HashSet;
import java.util.Set;

public class CartBean {

	private Set<CartItemBean> items;
	
	public CartBean() {
		items = new HashSet<>();
	}
	
	public void setCart(Set<CartItemBean> items) {
		this.items = items;
	}
	
	public Set<CartItemBean> getCart() {
		return items;
	}
	
}
