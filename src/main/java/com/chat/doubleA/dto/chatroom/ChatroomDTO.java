package com.chat.doubleA.dto.chatroom;

import com.chat.doubleA.entities.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChatroomDTO {
    private String id;

    private String title;

    private String topic;

    private String password;

    private User owner;

    private List<User> members;

    private List<String> messages;

    public ChatroomDTO(String title, String topic, String password, User owner) {
        this.title = title;
        this.topic = topic;
        this.password = password;
        this.owner = owner;
    }
}
