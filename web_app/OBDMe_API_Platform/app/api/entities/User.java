package api.entities;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class User implements Serializable {
	
	public User(Long id, String email) {
		this.id = id;
		this.email = email;
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
	
	public static User fromModelUser(models.obdme.User modelUser) {
		User fromModel = new User(modelUser.getId(), modelUser.email);
		return fromModel;
	}

}