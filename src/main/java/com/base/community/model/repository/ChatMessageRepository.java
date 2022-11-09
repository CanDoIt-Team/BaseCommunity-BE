package com.base.community.model.repository;

import com.base.community.model.entity.ChatMessage;
import com.base.community.model.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
