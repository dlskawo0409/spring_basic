package com.dlskawo0409.demo.auth.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshEntity {
    private Long refreshId;
    private String email;
    private String refresh;
    private String expiration;
}