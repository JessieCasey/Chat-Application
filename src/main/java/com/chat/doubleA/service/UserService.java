package com.chat.doubleA.service;


import com.chat.doubleA.entities.Chatroom;
import com.chat.doubleA.entities.User;

import java.util.List;

public interface UserService {
    User create(User user);

    User readById(String id);

    User readByEmail(String id);

    User readByUsername(String title);

    void delete(String id);

    List<Chatroom> getAllByUser(User user);

    List<User> getAll();

    User update(User user);

    void save(User user);
}