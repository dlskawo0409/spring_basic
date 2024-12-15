package com.dlskawo0409.demo.member.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum Role {
    ADMIN("ROLE_ADMIN", "운영자"),
    USER("ROLE_USER", "로그인 회원"),
    GUEST("ROLE_GUEST", "손님");


    private final String key;
    private final String title;

    Role(String key, String title) {
        this.key = key;
        this.title = title;
    }


    @JsonCreator
    public static Role from(String input) {
        return Arrays.stream(values())
                .filter(role -> role.key.equalsIgnoreCase(input))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    @JsonValue
    public String getKey() {
        return this.key;
    }

    public static boolean isSameName(String input, Role role) {
        return role.name()
                .equalsIgnoreCase(input);
    }

}