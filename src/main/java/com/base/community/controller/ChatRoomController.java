package com.base.community.controller;

import com.base.community.model.entity.ChatRoom;
import com.base.community.security.TokenProvider;
import com.base.community.service.ChatRoomService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/chat/room")
@Log4j2
public class ChatRoomController {

    private final TokenProvider tokenProvider;
    private final ChatRoomService chatRoomService;

    @ApiOperation(value = "채팅방 생성")
    @PostMapping(value = "/{roomName}")
    public ResponseEntity<ChatRoom> createChatRoom(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                 @PathVariable String roomName) {
        log.info("# Create Chat Room , name: " + roomName);
        return ResponseEntity.ok(chatRoomService
                .createChatRoom(tokenProvider.getUser(token).getId(), roomName));
    }

    @ApiOperation(value = "채팅방 조회")
    @GetMapping("/{roomId}")
    public ResponseEntity<ChatRoom> getChatRoom(@PathVariable Long roomId) {
        log.info("# get Chat Room, roomID : " + roomId);
        return ResponseEntity.ok(chatRoomService.getChatRoom(roomId));
    }

    @ApiOperation(value = "채팅방 삭제")
    @DeleteMapping("/{roomId}")
    public ResponseEntity<String> deleteChatRoom(@PathVariable Long roomId) {
        log.info("# delete Chat Room, roomID : " + roomId);
        return ResponseEntity.ok(chatRoomService.deleteChatRoom(roomId));
    }
}