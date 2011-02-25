package edu.unl.csce.obdme.api.entities;

/**
 * The Class User.
 */
public class User {
	
	/** The email. */
	private String email;
	
	/** The passwordhash. */
	private String passwordhash;
	
	/** The id. */
	private Long id;
	
	
	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getEmail() {
		return this.email;
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Long getId() {
		return this.id;
	}
	
	/**
	 * Gets the password hash.
	 *
	 * @return the password hash
	 */
	public String getPasswordHash() {
		return this.passwordhash;
	}
	
	/**
	 * Sets the email.
	 *
	 * @param email the new email
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * Sets the password hash.
	 *
	 * @param passwordhash the new password hash
	 */
	public void setPasswordHash(String passwordhash) {
		this.passwordhash = passwordhash;
	}
	
}