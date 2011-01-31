package edu.unl.csce.obdme.api.entities;

public class User {
	
	private String email;
	private String passwordhash;
	private Long id;
	
	
	public String getEmail() {
		return this.email;
	}
	
	public Long getId() {
		return this.id;
	}
	
	public String getPasswordHash() {
		return this.passwordhash;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public void setPasswordHash(String passwordhash) {
		this.passwordhash = passwordhash;
	}
	
}