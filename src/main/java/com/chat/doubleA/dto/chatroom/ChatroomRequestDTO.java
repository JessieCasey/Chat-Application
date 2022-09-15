package com.chat.doubleA.dto.chatroom;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChatroomRequestDTO {

    String title;
    String topic;
    String password;
    String action;

    public ChatroomRequestDTO(String action) {
        this.action = action;
    }
}
