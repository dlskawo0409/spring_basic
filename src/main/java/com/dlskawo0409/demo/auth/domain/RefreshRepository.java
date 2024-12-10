package com.dlskawo0409.demo.auth.domain;

import org.springframework.transaction.annotation.Transactional;


public interface RefreshRepository {
    Boolean existsByRefresh(String refresh);
    @Transactional
    void deleteByRefresh(String refresh);

    boolean save(RefreshEntity refreshEntity);
}
