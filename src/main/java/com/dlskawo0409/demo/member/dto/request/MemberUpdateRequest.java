package com.dlskawo0409.demo.member.dto.request;

import com.dlskawo0409.demo.member.domain.Role;

public record MemberUpdateRequest (
        String nickname,
        String password,
        Role role
){
}
