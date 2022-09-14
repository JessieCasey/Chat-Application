package com.chat.doubleA.controllers;

import com.chat.doubleA.config.socket.WebSocketEventListener;
import com.chat.doubleA.dto.chatroom.ChatroomDTO;
import com.chat.doubleA.dto.chatroom.ChatroomRequestDTO;
import com.chat.doubleA.entities.ChatMessage;
import com.chat.doubleA.entities.Chatroom;
import com.chat.doubleA.entities.User;
import com.chat.doubleA.repositories.ChatroomRepository;
import com.chat.doubleA.repositories.UserRepository;
import com.chat.doubleA.security.SecurityUser;
import com.chat.doubleA.service.ChatroomService;
import com.chat.doubleA.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.String.format;

@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/chat")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    private final SimpMessageSendingOperations messagingTemplate;
    private final UserRepository userRepository;
    private final ChatroomService chatroomService;
    private final UserService userService;

    @Autowired
    public ChatController(SimpMessageSendingOperations messagingTemplate, UserRepository userRepository, ChatroomService chatroomService, UserService userService) {
        this.messagingTemplate = messagingTemplate;
        this.userRepository = userRepository;
        this.chatroomService = chatroomService;
        this.userService = userService;
    }

    @MessageMapping("/chat/{roomId}/sendMessage")
    public void sendMessage(@DestinationVariable String roomId, @Payload ChatMessage chatMessage) {
        logger.info("[MessageMapping] method 'sendMessage'");

        Chatroom chatroom = chatroomService.readByTitle(roomId);

        if (chatMessage.getType().equals(ChatMessage.MessageType.CHAT)) {
            chatroom.getMessages().add(chatMessage);

            User user = userService.readByUsername(chatMessage.getSender());
            user.getMessages().add(chatMessage);

            chatroomService.save(chatroom);
            userService.save(user);
        }

        messagingTemplate.convertAndSend(format("/topic/%s", roomId), chatMessage);

    }

    @MessageMapping("/chat/{roomId}/addUser")
    public void addUser(@DestinationVariable String roomId, @Payload ChatMessage message,
                        SimpMessageHeaderAccessor headerAccessor) {
        logger.info("[MessageMapping] method 'addUser'");

        messagingTemplate.convertAndSend(format("/topic/%s", roomId), new ChatMessage(ChatMessage.MessageType.UPDATE));

        Chatroom chatroom = chatroomService.readByTitle(roomId);

        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("username", message.getSender());

        if (message.getType().equals(ChatMessage.MessageType.CHAT))
            chatroom.getMessages().add(message);

        chatroom.getMessages().forEach(x -> messagingTemplate.convertAndSend(format("/topic/%s", roomId), x));

        chatroomService.save(chatroom);
    }

    @GetMapping("/connection/{title}")
    @PreAuthorize("@chatController.idComparator(#authentication.principal.id, #title)")
    public String connectChatroom(Model model,
                                  Authentication authentication,
                                  @PathVariable String title) {
        logger.info("[GetMapping] method 'connectChatroom'");

        User authUser = userService.
                readByEmail(((SecurityUser) authentication.getPrincipal()).getUsername());

        Chatroom chatroom = chatroomService.readByTitle(title);

        model.addAttribute("chatroom", chatroom);
        model.addAttribute("authUser", authUser.getUsername());

        return "connection";
    }

    @GetMapping("/invite/server/{title}/{id}/{username}/{password}")
    public String inviteToChatroom(Model model,
                                   Authentication authentication,
                                   @PathVariable String title,
                                   @PathVariable String id,
                                   @PathVariable String username,
                                   @PathVariable String password) {
        logger.info("[GetMapping] method 'inviteToChatroom'");

        User authUser = userService.
                readByEmail(((SecurityUser) authentication.getPrincipal()).getUsername());

        Chatroom chatroom = chatroomService.readByTitle(title);

        if (authUser.getMember().contains(chatroom) || chatroom.getOwner().equals(authUser)) {
            logger.warn("In method 'inviteToChatroom' user is already member of the chatroom");
            return "redirect:/";

        } else if (chatroom.getId().equals(id) && chatroom.getUnifiedPassword().equals(password)) {
            chatroom.getMembers().add(authUser);
            authUser.getMember().add(chatroom);

            userService.update(authUser);
            chatroomService.update(chatroom);

            model.addAttribute("chatroom", chatroom);
            model.addAttribute("message", "You were invited with a link by " + username);
            model.addAttribute("authUser", authUser.getUsername());

            return "connection";

        } else {
            logger.error("Access denied due to changing original link");
            throw new AccessDeniedException("Access denied due to changing original link");
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteChatroom(Authentication authentication,
                                 @PathVariable String id) {
        logger.info("[GetMapping] method 'deleteChatroom'");

        User authUser = userService.
                readByEmail(((SecurityUser) authentication.getPrincipal()).getUsername());

        Chatroom chatroom = chatroomService.readById(id);
        if (chatroom.getOwner().equals(authUser)) {
            chatroomService.delete(authUser, id);
        } else {
            authUser.getMember().remove(chatroom);
            userRepository.save(authUser);
        }

        return "redirect:/";
    }

    @PostMapping("/{action}")
    public String handleChatroom(Authentication authentication,
                                 @Payload ChatroomDTO chatroomDTO,
                                 @PathVariable String action) {
        logger.info("[PostMapping] method 'handleChatroom'");

        User authUser = userService.
                readByEmail(((SecurityUser) authentication.getPrincipal()).getUsername());

        if (action.equals("Add")) {
            chatroomService.create(authUser, new Chatroom(chatroomDTO.getTitle(), chatroomDTO.getTopic(), chatroomDTO.getPassword(), authUser));
        } else {
            chatroomService.connect(authUser, chatroomDTO.getTitle());
        }

        return "redirect:/";
    }

    @GetMapping("/{action}")
    public String handleChatroom(Model model,
                                 @PathVariable String action) {
        logger.info("[GetMapping] method 'handleChatroom'");

        model.addAttribute("chatroom", new ChatroomRequestDTO(action));
        return "create-chat";
    }

    public boolean idComparator(String authenticatedUserId, String title) {
        logger.info("Method 'idComparator' was invoked");
        Chatroom chatroom = chatroomService.readByTitle(title);

        if (chatroom.getOwner().getId().equals(authenticatedUserId))
            return true;

        AtomicBoolean hasAuthority = new AtomicBoolean(false);

        chatroom.getMembers().forEach(x -> {
            if (x.getId().equals(authenticatedUserId))
                hasAuthority.set(true);
        });

        return hasAuthority.get();
    }
}