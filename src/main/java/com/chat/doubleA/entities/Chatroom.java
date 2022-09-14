package com.chat.doubleA.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@Document("chatrooms")
public class Chatroom {

    @Id
    private String id;

    @Indexed(unique = true)
    private String title;

    private String topic;

    private String password;

    private LocalDateTime createdAt;

    @DBRef
    private User owner;

    @DBRef
    private List<User> members = new ArrayList<>();

    private List<ChatMessage> messages = new ArrayList<>();

    public Chatroom(String title, String topic, String password, User owner) {
        this.title = title;
        this.topic = topic;
        this.password = password;
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chatroom chatroom = (Chatroom) o;

        if (!Objects.equals(id, chatroom.id)) return false;
        return Objects.equals(title, chatroom.title);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }

    public int getParticipantsCount() {
        return members.size() + 1;
    }

    public String getUnifiedPassword() {
        if (getPassword() == null)
            return "";

        return getPassword().replaceAll("[^A-Za-z0-9]", "");
    }
}