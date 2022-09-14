package com.chat.doubleA.service.impl;

import com.chat.doubleA.entities.Chatroom;
import com.chat.doubleA.entities.User;
import com.chat.doubleA.exceptions.NullEntityReferenceException;
import com.chat.doubleA.repositories.ChatroomRepository;
import com.chat.doubleA.repositories.UserRepository;
import com.chat.doubleA.service.ChatroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ChatroomServiceImpl implements ChatroomService {

    private final ChatroomRepository chatroomRepository;
    private final UserRepository userRepository;

    @Lazy
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ChatroomServiceImpl(ChatroomRepository chatroomRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.chatroomRepository = chatroomRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public Chatroom create(User user, Chatroom chatroom) {
        if (chatroom != null) {
            chatroom.setCreatedAt(LocalDateTime.now());
            chatroom.setPassword(passwordEncoder.encode(chatroom.getPassword()));
            user.getOwner().add(chatroom);
            return chatroomRepository.save(chatroom);
        }
        throw new NullEntityReferenceException("Chatroom cannot be 'null'");
    }

    @Override
    public void save(Chatroom chatroom) {
        chatroomRepository.save(chatroom);
    }

    @Override
    public void connect(User user, String title) {
        Chatroom chatroom = chatroomRepository.findByTitle(title).orElseThrow(NullPointerException::new);
        user.getMember().add(chatroom);
        chatroom.getMembers().add(user);
        chatroomRepository.save(chatroom);
        userRepository.save(user);
    }

    @Override
    public void delete(User user, String id) {
        Chatroom chatroom = chatroomRepository.findById(id).orElseThrow(NullPointerException::new);
        user.getMember().remove(chatroom);
        user.getOwner().remove(chatroom);
        userRepository.save(user);
        chatroomRepository.delete(chatroom);
    }

    @Override
    public Chatroom update(Chatroom chatroom) {
        System.out.println(chatroom);
        if (chatroom != null) {
            readById(chatroom
                    .getId());
            return chatroomRepository.save(chatroom);
        } else {
            throw new NullEntityReferenceException("Chatroom cannot be 'null'");
        }
    }

    @Override
    public Chatroom readByTitle(String title) {
        return chatroomRepository.findByTitle(title).orElseThrow(NullPointerException::new);
    }

    @Override
    public Chatroom readById(String id) {
        return chatroomRepository.findById(id).orElseThrow(NullPointerException::new);
    }
}
