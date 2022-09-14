package com.chat.doubleA.service;

import com.chat.doubleA.entities.Chatroom;
import com.chat.doubleA.entities.User;

public interface ChatroomService {

    Chatroom create(User user, Chatroom chatroom);

    void connect(User byEmail, String title);

    void delete(User byEmail, String id);

    Chatroom readByTitle(String title);

    Chatroom readById(String id);

    void save(Chatroom chatroom);

    Chatroom update(Chatroom chatroom);

}
