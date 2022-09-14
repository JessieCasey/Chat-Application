package com.chat.doubleA.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatMessage {

    public enum MessageType {
        CHAT, JOIN, LEAVE, UPDATE
    }

    private MessageType type;
    private String content;
    private String sender;
    private LocalDateTime createdAt;

    public ChatMessage() {
        this.createdAt = LocalDateTime.now();
    }

    public ChatMessage(MessageType type) {
        this();
        this.type = type;
    }

    public ChatMessage(MessageType type, String sender) {
        this(type);
        this.sender = sender;
    }

    public ChatMessage(MessageType type, String content, String sender) {
        this(type, sender);
        this.content = content;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "type=" + type +
                ", content='" + content + '\'' +
                ", sender='" + sender + '\'' +
                '}';
    }
}