package com.dlskawo0409.demo.auth.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDTO {
    private Long memberId;
    private String role;
    private String name;
    private String username;
}
