package edu.unl.csce.obdme.api.entities;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The Class User.
 * This class represents a user associated with the obdme system.
 */
public class User implements Serializable {
	
	private static final long serialVersionUID = 694142388417272073L;

	/** The email. */
	@JsonProperty("email")
	private String email;
	
	/** The id. */
	@JsonProperty("id")
	private Long id;	
	
	/**
	 * Gets the email of the user.
	 *
	 * @return the email
	 */
	public String getEmail() {
		return this.email;
	}
	
	/**
	 * Gets the id of the user.
	 *
	 * @return the id
	 */
	public Long getId() {
		return this.id;
	}	
	
	/**
	 * Sets the email of the user.
	 *
	 * @param email the new email
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	
	/**
	 * Sets the id of the user.
	 *
	 * @param id the new id
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
}