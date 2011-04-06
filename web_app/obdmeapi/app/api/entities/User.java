package api.entities;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("serial")
public class User implements Serializable {
	
	public User(Long id, String email) {
		this.id = id;
		this.email = email;
	}
	
	public User(models.obdmedb.User modelUser) {
		this.id = modelUser.getId();
		this.email = modelUser.getEmail();
	}
	
	@SerializedName("id")
	private Long id;
	
	@SerializedName("email")
	private String email;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}