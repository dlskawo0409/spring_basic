package com.dlskawo0409.demo.common.Image.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ImageType {

    PROFILE("PROFILE", "profile_image");

    private final String key;
    private final String url;

    ImageType(String key, String url) {
        this.key = key;
        this.url = url;
    }

    @JsonValue
    public String getKey() {
        return this.key;
    }


    public String getUrl(){
        return this.url;
    }
}
