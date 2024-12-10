package com.dlskawo0409.demo.member.domain;

import org.springframework.data.repository.query.Param;

public interface MemberRepository{
    Member findById(Long id);
    Member  findByUsername(String Username);
    boolean existsByUsername(String username);
    boolean existsByNickname(String nickname);
    void save(Member member);
    boolean update(Member member);
}