package com.base.community.service;

import com.base.community.exception.CustomException;
import com.base.community.model.entity.ChatRoom;
import com.base.community.model.entity.Member;
import com.base.community.model.entity.Project;
import com.base.community.model.repository.ChatMessageRepository;
import com.base.community.model.repository.ChatRoomRepository;
import com.base.community.model.repository.MemberRepository;
import com.base.community.model.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.base.community.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final ChatRoomRepository chatRoomRepository;

    public ChatRoom getChatRoom(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_CHATROOM));
    }

    public ChatRoom createChatRoom(Long memberId, String roomName) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        Project project = projectRepository.findByLeader(member)
                .orElseThrow(() -> new CustomException(NOT_FOUND_PROJECT));

        ChatRoom.builder()
                .roomName(roomName)
                .project(project)
                .createdAt(LocalDateTime.now())
                .build();

        return chatRoomRepository.save(ChatRoom.builder()
                .roomName(roomName)
                .project(project)
                .createdAt(LocalDateTime.now())
                .build());
    }

    public String deleteChatRoom(Long roomId) {
        chatRoomRepository.deleteById(roomId);
        return "삭제가 완료되었습니다.";
    }
}
