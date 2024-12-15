package com.dlskawo0409.demo.member.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByNickname(String nickname);
    void delete(Member member);
}