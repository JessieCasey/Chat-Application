package com.chat.doubleA;

import com.chat.doubleA.entities.ChatMessage;
import com.chat.doubleA.entities.Chatroom;
import com.chat.doubleA.entities.Role;
import com.chat.doubleA.entities.User;
import com.chat.doubleA.repositories.ChatroomRepository;
import com.chat.doubleA.repositories.RoleRepository;
import com.chat.doubleA.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class Bootstrap {

    @Bean
    CommandLineRunner runner(RoleRepository roleRepository, UserRepository userRepository, ChatroomRepository chatroomRepository, PasswordEncoder passwordEncoder) {
        roleRepository.deleteAll();
        userRepository.deleteAll();
        chatroomRepository.deleteAll();

        return args -> {
            if (!roleRepository.existsByName("user") && !roleRepository.existsByName("admin") && !roleRepository.existsByName("moderator")) {

                Role userRole = new Role("user");
                Role adminRole = new Role("admin");
                Role moderRole = new Role("moderator");
                roleRepository.insert(userRole);
                roleRepository.insert(adminRole);
                roleRepository.insert(moderRole);

                if (!userRepository.existsByEmail("t@gmail.com") && !userRepository.existsByEmail("k@gmail.com")) {
                    User tony = new User();
                    tony.setEmail("t@gmail.com");
                    tony.setPassword(passwordEncoder.encode("Tony1"));
                    tony.setUsername("Tony");
                    tony.setRole(userRole);
                    userRepository.insert(tony);

                    User kate = new User();
                    kate.setEmail("k@gmail.com");
                    kate.setPassword(passwordEncoder.encode("Kate1"));
                    kate.setUsername("Kate");
                    kate.setRole(userRole);
                    userRepository.insert(kate);

                    if (!chatroomRepository.existsByTitle("Gastronomy Lobby")) {
                        Chatroom chatroom = new Chatroom("Gastronomy", "Cooking", passwordEncoder.encode("123"), tony);
                        chatroom.setCreatedAt(LocalDateTime.now());
                        chatroom.getMembers().add(kate);
                        chatroom.getMessages().add(new ChatMessage(ChatMessage.MessageType.CHAT, "Hello world!", tony.getUsername()));
                        chatroom.getMessages().add(new ChatMessage(ChatMessage.MessageType.CHAT, "Hi! Everybody!", kate.getUsername()));
                        Chatroom insert = chatroomRepository.insert(chatroom);

                        tony.setOwner(insert);
                        kate.getMember().add(insert);
                        userRepository.save(kate);
                        userRepository.save(tony);

                    }
                }
            }
        };
    }
}
