package com.chat.doubleA.controllers;

import com.chat.doubleA.config.socket.WebSocketEventListener;
import com.chat.doubleA.entities.User;
import com.chat.doubleA.repositories.UserRepository;
import com.chat.doubleA.security.SecurityUser;
import com.chat.doubleA.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Autowired
    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping({"/", "home"})
    public String home(Model model, Authentication authentication) {
        logger.info("[MessageMapping] method 'sendMessage'");

        User authUser = userService.
                readByEmail(((SecurityUser) authentication.getPrincipal()).getUsername());

        if (authUser.getRole().getName().equals("BANNED")) {
            throw new AccessDeniedException("You are banned");
        }

        model.addAttribute("dateTimeFormatter", dateTimeFormatter);
        model.addAttribute("chatrooms", userService.getAllByUser(authUser));
        model.addAttribute("authUser", authUser.getUsername());

        return "home";
    }

    @GetMapping({"/board"})
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MODERATOR')")
    public String adminBoard(Model model, Authentication authentication) {
        logger.info("[GetMapping] method 'adminBoard'");

        User authUser = userService.
                readByEmail(((SecurityUser) authentication.getPrincipal()).getUsername());

        logger.info("Admin " + authUser.getUsername() + "entered to the admin board");
        model.addAttribute("dateTimeFormatter", dateTimeFormatter);
        model.addAttribute("users", userService.getAll());
        model.addAttribute("authUser", authUser.getUsername());

        return "adminBoard";
    }
}