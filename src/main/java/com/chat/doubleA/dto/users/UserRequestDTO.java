package com.chat.doubleA.dto.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {
    @JsonProperty("user_name")
    private String username;

    private String email;

    private String password;

    private String role;
}