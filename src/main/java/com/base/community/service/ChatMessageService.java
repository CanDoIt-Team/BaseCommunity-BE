package com.base.community.service;

import com.base.community.dto.ChatMessageDto;
import com.base.community.exception.CustomException;
import com.base.community.model.entity.ChatMessage;
import com.base.community.model.entity.ChatRoom;
import com.base.community.model.entity.Member;
import com.base.community.model.repository.ChatMessageRepository;
import com.base.community.model.repository.ChatRoomRepository;
import com.base.community.model.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.base.community.exception.ErrorCode.NOT_FOUND_CHATROOM;
import static com.base.community.exception.ErrorCode.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final RabbitTemplate template;

    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    public void enterMessage(ChatMessageDto message, String chatRoomId) {
        Member member = memberRepository.findById(message.getMemberId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        message.setMessage(member.getNickname() + "님이 채팅방에 참여하였습니다.");
        template.convertAndSend("chat.exchange", "room." + chatRoomId, message);

        //template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }

    public void sendMessage(ChatMessageDto message, String chatRoomId) {
        Member member = memberRepository.findById(message.getMemberId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        ChatRoom chatRoom = chatRoomRepository.findById(Long.parseLong(chatRoomId))
                .orElseThrow(() -> new CustomException(NOT_FOUND_CHATROOM));

        ChatMessage chatMessage = chatMessageRepository.save(ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(member.getNickname())
                .message(message.getMessage())
                .createdAt(LocalDateTime.now())
                .build());

        message.setSendTime(chatMessage.getCreatedAt());

        template.convertAndSend("chat.exchange", "room." + chatRoomId, message);
    }
}
