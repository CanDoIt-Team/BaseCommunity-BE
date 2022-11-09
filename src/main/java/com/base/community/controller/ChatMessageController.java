package com.base.community.controller;

import com.base.community.dto.ChatMessageDto;
import com.base.community.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatMessageController {
    private final ChatMessageService chatMessageService;

    @MessageMapping("chat.enter.{chatRoomId}")
    public void enter(ChatMessageDto message, @DestinationVariable String chatRoomId){
        log.info("# chat enter member id : " + message.getMemberId());
        chatMessageService.enterMessage(message, chatRoomId);
    }

    @MessageMapping("chat.message.{chatRoomId}")
    public void message(ChatMessageDto message, @DestinationVariable String chatRoomId){
        log.info("# chat message member id : " + message.getMemberId());
        chatMessageService.sendMessage(message, chatRoomId);
    }
}