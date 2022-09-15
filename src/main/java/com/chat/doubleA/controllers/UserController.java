package com.chat.doubleA.controllers;

import com.chat.doubleA.config.socket.WebSocketEventListener;
import com.chat.doubleA.dto.chatroom.ChatroomRequestDTO;
import com.chat.doubleA.dto.users.UserRequestDTO;
import com.chat.doubleA.entities.Chatroom;
import com.chat.doubleA.entities.User;
import com.chat.doubleA.repositories.UserRepository;
import com.chat.doubleA.security.SecurityUser;
import com.chat.doubleA.service.RoleService;
import com.chat.doubleA.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
    private final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, UserRepository userRepository, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/create")
    public String create(@Validated @ModelAttribute("user") User user, BindingResult result) {
        if (result.hasErrors()) {
            return "registration";
        }

        userService.create(user);
        return "redirect:/home";
    }

    @GetMapping("/action/{action}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MODERATOR')")
    public String handleUserAction(Model model,
                                   @PathVariable String action) {
        logger.info("[GetMapping] method 'handleUserAction'");

        model.addAttribute("userDTO", new UserRequestDTO());
        model.addAttribute("action", action);

        return "read-user";
    }

    @PostMapping("/action/{action}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MODERATOR')")
    public String handleUserAction(@Payload UserRequestDTO userRequestDTO,
                                   @PathVariable String action) {
        logger.info("[PostMapping] method 'handleUserAction'");
        String id = userService.readByUsername(userRequestDTO.getUsername()).getId();

        if (action.equalsIgnoreCase("Permit")) {
            User user = userService.readById(id);
            user.setRole(roleService.readByName("USER"));
            userService.update(user);

            return "redirect:/board";

        } else if (action.equalsIgnoreCase("Read")) {
            return "redirect:/users/" + id;
        } else {
            return "redirect:/ban/" + id;
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MODERATOR')")
    public String readById(Model model,
                           @PathVariable String id) {

        User user = userService.readById(id);

        model.addAttribute("dateTimeFormatter", dateTimeFormatter);
        model.addAttribute("user", user);
        return "user-info";
    }

    @GetMapping("/ban/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MODERATOR')")
    public String banUser(@PathVariable String id) {

        User user = userService.readById(id);
        if (!user.getRole().getName().equals("ADMIN")) {
            user.setRole(roleService.readByName("BANNED"));
            userService.update(user);
        }
        return "redirect:/board";
    }
}