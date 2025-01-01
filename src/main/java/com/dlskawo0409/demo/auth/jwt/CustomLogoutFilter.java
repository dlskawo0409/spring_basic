package com.dlskawo0409.demo.auth.jwt;

import com.dlskawo0409.demo.auth.application.RedisRefreshTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Objects;

public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
//    private final RefreshRepository refreshRepository;
    private final RedisRefreshTokenService redisRefreshTokenService;

//    public CustomLogoutFilter(JWTUtil jwtUtil, RefreshRepository refreshRepository) {
//        this.jwtUtil = jwtUtil;
//        this.refreshRepository = refreshRepository;
//    }

    public CustomLogoutFilter(JWTUtil jwtUtil, RedisRefreshTokenService redisRefreshTokenService){
        this.jwtUtil = jwtUtil;
        this.redisRefreshTokenService = redisRefreshTokenService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        //path and method verify
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/logout$")) {
            System.out.println("logout");
            filterChain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        Cookie refresh = null;
        Cookie authorization = null;
        Cookie jsessionId = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) { // 쿠키 배열이 null인지 확인
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("Authorization")) {
                    authorization = cookie;
                }
                if(cookie.getName().equals("refresh")){
                    refresh = cookie;
                }
                if(cookie.getName().equals("JSESSIONID")){
                    jsessionId = cookie;
                }
            }
        }

        if(authorization != null){
            authorization.setMaxAge(0);
        }
        if(jsessionId != null){
            jsessionId.setMaxAge(0);
        }
        if(refresh != null){
            redisRefreshTokenService.deleteRefreshToken(refresh.getValue());
            refresh.setMaxAge(0);
        }

    }
}