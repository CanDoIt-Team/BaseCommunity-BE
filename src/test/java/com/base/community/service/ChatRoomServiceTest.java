package com.base.community.service;

import com.base.community.model.entity.*;
import com.base.community.model.repository.ChatRoomRepository;
import com.base.community.model.repository.MemberRepository;
import com.base.community.model.repository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.base.community.type.MemberCode.MEMBER_STATUS_ING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ChatRoomService chatRoomService;

    @DisplayName("채팅방 조회")
    @Test
    void get_chat_room_test() {
        // given
        List<ProjectSkill> projectSkills = new ArrayList<>();
        projectSkills.add(ProjectSkill.builder().name("spring").build());
        projectSkills.add(ProjectSkill.builder().name("react").build());

        Project project = Project.builder()
                .id(1L)
                .title("프로젝트")
                .content("내용")
                .maxTotal(5)
                .nowTotal(0)
                .isComplete(false)
                .developPeriod("3")
                .startDate(LocalDate.now())
                .projectSkills(projectSkills)
                .build();

        LocalDateTime now = LocalDateTime.now();

        given(chatRoomRepository.findById(anyLong()))
                .willReturn(Optional.of(ChatRoom.builder()
                        .id(1L)
                        .roomName("채팅방1")
                        .createdAt(now)
                        .project(project)
                        .build()));

        // when
        ChatRoom room = chatRoomService.getChatRoom(1L);

        // then
        assertEquals(1L, room.getId());
        assertEquals("채팅방1", room.getRoomName());
        assertEquals(now, room.getCreatedAt());
    }

    @DisplayName("채팅방 생성")
    @Test
    void create_chat_room_test() {
        // given
        List<MemberSkills> memberSkills = new ArrayList<>();
        memberSkills.add(MemberSkills.builder().name("java").build());
        memberSkills.add(MemberSkills.builder().name("spring").build());
        Member member = Member.builder()
                .id(1L)
                .email("test@test.com")
                .password("1234")
                .name("테스트")
                .nickname("멍멍이")
                .birth(LocalDate.now())
                .phone("01012345678")
                .skills(memberSkills)
                .emailAuth(true)
                .emailAuthDate(LocalDateTime.now())
                .userStatus(MEMBER_STATUS_ING.getStatus())
                .build();

        List<ProjectSkill> projectSkills = new ArrayList<>();
        projectSkills.add(ProjectSkill.builder().name("spring").build());
        projectSkills.add(ProjectSkill.builder().name("react").build());
        Project project = Project.builder()
                .id(1L)
                .title("프로젝트")
                .content("내용")
                .maxTotal(5)
                .nowTotal(0)
                .isComplete(false)
                .developPeriod("3")
                .startDate(LocalDate.now())
                .projectSkills(projectSkills)
                .build();

        LocalDateTime now = LocalDateTime.now();

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        given(projectRepository.findByLeader(any())).willReturn(Optional.of(project));
        given(chatRoomRepository.save(any())).willReturn(ChatRoom.builder()
                        .id(1L)
                        .roomName("채팅방1")
                        .project(project)
                        .createdAt(now)
                        .build());

        // when
        ChatRoom chatRoom = chatRoomService.createChatRoom(1L, "채팅방1");

        // then
        assertEquals(1L, chatRoom.getId());
        assertEquals("채팅방1", chatRoom.getRoomName());
        assertEquals(now, chatRoom.getCreatedAt());
        assertEquals("프로젝트", chatRoom.getProject().getTitle());
    }

    @DisplayName("채팅방 삭제")
    @Test
    void delete_chat_room_test() {
        String s = chatRoomService.deleteChatRoom(anyLong());
        assertEquals("삭제가 완료되었습니다.", s);
    }
}