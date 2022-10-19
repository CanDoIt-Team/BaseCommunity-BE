package com.base.community.model.repository;

import com.base.community.model.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

    Optional<Member> findByEmailAuthKey(String uuid);

    Optional<Member> findByEmailAndName(String email, String name);

    Optional<Member> findByChangePasswordKey(String uuid);

    Optional<Member> findByEmail(String email);
}
