package com.dlskawo0409.demo.member.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum Role {
    ADMIN("ROLE_ADMIN", "운영자"),
    USER("ROLE_USER", "로그인 회원"),
    GUEST("ROLE_GUEST", "손님"),
    AGENT("ROLE_AGENT","중개인");

    private final String key;
    private final String title;

    Role(String key, String title) {
        this.key = key;
        this.title = title;
    }


    @JsonCreator
    public static Role from(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null; // 또는 기본 값 설정
        }
        return Arrays.stream(values())
                .filter(role -> role.key.equalsIgnoreCase(input) || role.name().equalsIgnoreCase(input))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 역할 키: " + input));
    }

    @JsonValue
    public String getKey() {
        return this.key;
    }

    public boolean equalsKeyOrName(String input) {
        if (input == null) {
            return false;
        }
        return this.key.equalsIgnoreCase(input) || this.name().equalsIgnoreCase(input);
    }




}