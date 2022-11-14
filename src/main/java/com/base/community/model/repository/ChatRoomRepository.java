package com.base.community.model.repository;

import com.base.community.model.entity.ChatRoom;
import com.base.community.model.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByProject(Project project);
}
