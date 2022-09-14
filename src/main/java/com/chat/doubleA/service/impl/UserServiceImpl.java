package com.chat.doubleA.service.impl;

import com.chat.doubleA.entities.Chatroom;
import com.chat.doubleA.entities.User;
import com.chat.doubleA.exceptions.NullEntityReferenceException;
import com.chat.doubleA.repositories.ChatroomRepository;
import com.chat.doubleA.repositories.RoleRepository;
import com.chat.doubleA.repositories.UserRepository;
import com.chat.doubleA.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service("userServiceImpl")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ChatroomRepository chatroomRepository;
    private final RoleRepository roleRepository;

    @Lazy
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ChatroomRepository chatroomRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.chatroomRepository = chatroomRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User create(User user) {
        if (user != null) {
            user.setPassword(user.getPassword());
            user.setRole(roleRepository.findByName("user").orElseThrow(NullEntityReferenceException::new));

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        }
        throw new NullEntityReferenceException("User cannot be 'null'");
    }

    @Override
    public User readById(String id) {
        return userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("User with id " + id + " not found"));
    }

    @Override
    public void delete(String id) {
        User user = readById(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        userRepository.delete(user);
    }

    @Override
    public List<Chatroom> getAllByUser(User user) {
        List<Chatroom> chatrooms = chatroomRepository.findAllByOwner_Id(user.getId());
        if (!user.getMember().contains(null))
            chatrooms.addAll(user.getMember());

        return chatrooms.isEmpty() ? new ArrayList<>() : chatrooms;
    }
}