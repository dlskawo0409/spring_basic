package com.dlskawo0409.demo.member.dto.response;

import com.dlskawo0409.demo.common.Image.domain.Image;
import lombok.Builder;

@Builder
public record MemberUpdateResponse(
        Long memberId,
        String username,
        String nickname,
        Image profile
) {
}
