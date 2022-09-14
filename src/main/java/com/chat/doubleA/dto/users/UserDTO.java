package com.chat.doubleA.dto.users;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

	private String id;
	private String username;
	private String description;
	private String email;
	private String password;
	private String status;
	private String createdAt;

	public UserDTO(String id, String username, String description, String email,
                   String password, String status) {
		this.id = id;
		this.username = username;
		this.description = description;
		this.email = email;
		this.password = password;
		this.status = status;
	}

}
