package com.music.SalesService.domain;

import javax.validation.constraints.NotNull;

public class User {
	private long id;
	private String firstname;
	private String lastname;
	private String emailAddress;
	
	private String address;

	public long getId() {
		return id;
	}

	public void setId(long user_id) {
		this.id = user_id;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	@NotNull(message="Email cannot be null") public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String email_address) {
		this.emailAddress = email_address;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
