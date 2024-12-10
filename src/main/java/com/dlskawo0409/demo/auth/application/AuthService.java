package com.dlskawo0409.demo.auth.application;

import com.dlskawo0409.demo.auth.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    public String login(String username, String password) {
        try {
            // 사용자 인증
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // 인증 성공 시 JWT 토큰 생성 및 반환
            String category = "user"; // 예시로 사용, 필요에 따라 실제 역할을 지정
            CustomMemberDetails userDetails = (CustomMemberDetails) authentication.getPrincipal();
            Long memberId = userDetails.getMember().getMemberId(); // member_id 접근
            log.debug(String.valueOf(memberId));
            String role = authentication.getAuthorities().iterator().next().getAuthority();

            Long expirationTime = 3600000L; // 예시로 1시간 만료 시간을 설정

            return jwtUtil.createJwt(category, username, role, expirationTime, memberId);
        } catch (AuthenticationException e) {
            throw new RuntimeException("로그인 실패: 잘못된 ID 또는 비밀번호입니다.", e);
        }
    }

    public String getUsernameFromToken(String token) {
        return jwtUtil.getUsername(token);
    }

    public String getCategoryFromToken(String token) {
        return jwtUtil.getCategory(token);
    }

    public Boolean isTokenExpired(String token) {
        return jwtUtil.isExpired(token);
    }
}
