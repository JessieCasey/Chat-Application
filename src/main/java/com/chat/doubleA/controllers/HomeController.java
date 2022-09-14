package com.chat.doubleA.controllers;

import com.chat.doubleA.config.socket.WebSocketEventListener;
import com.chat.doubleA.entities.User;
import com.chat.doubleA.repositories.UserRepository;
import com.chat.doubleA.security.SecurityUser;
import com.chat.doubleA.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.format.DateTimeFormatter;

@Controller

public class HomeController {
    private final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public HomeController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping({"/", "home"})
    public String home(Model model, Authentication authentication) {
        logger.info("[MessageMapping] method 'sendMessage'");

        User authUser = userRepository
                .findByEmail(((SecurityUser) authentication.getPrincipal()).getUsername())
                .orElseThrow(NullPointerException::new);

        model.addAttribute("dateTimeFormatter", dateTimeFormatter);
        model.addAttribute("chatrooms", userService.getAllByUser(authUser));
        model.addAttribute("authUser", authUser.getUsername());

        return "home";
    }
}