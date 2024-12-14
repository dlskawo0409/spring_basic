package com.dlskawo0409.demo.security;

import com.dlskawo0409.demo.auth.application.CustomOAuth2UserService;
import com.dlskawo0409.demo.auth.domain.RefreshRepository;
import com.dlskawo0409.demo.auth.jwt.CustomSuccessHandler;
import com.dlskawo0409.demo.auth.jwt.JWTFilter;
import com.dlskawo0409.demo.auth.jwt.JWTUtil;
import com.dlskawo0409.demo.auth.jwt.LoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final CorsConfigurationSource corsConfigurationSource;

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //csrf disable
        http
                .csrf((auth) -> auth.disable());

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource));


        //From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        //http basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

        http
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler)
                );


        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/",
                                "/login",
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/v3/api-docs/**",
                                "/logout"

                        ).permitAll()

                        .requestMatchers(HttpMethod.GET, "/members/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/members/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/members/**").hasAnyRole("ADMIN","AGENT", "USER")
                        .requestMatchers(HttpMethod.DELETE, "/members/**").hasAnyRole("ADMIN","AGENT", "USER")

                        .requestMatchers(HttpMethod.GET, "/images/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/images/**").hasAnyRole("ADMIN", "AGENT")
                        .requestMatchers(HttpMethod.PUT, "/images").hasAnyRole("ADMIN", "AGENT")
                        .requestMatchers(HttpMethod.DELETE, "/images").hasAnyRole("ADMIN", "AGENT")


                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated());
//        http
//                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);
//        http
//                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshRepository), UsernamePasswordAuthenticationFilter.class);

        http
                .logout(logout -> logout
                                .logoutUrl("/logout") // 로그아웃 요청 URL
//                .logoutSuccessUrl("/") // 로그아웃 성공 후 리다이렉트 URL
//                .invalidateHttpSession(true) // 세션 무효화
                                .deleteCookies("JSESSIONID") // 쿠키 삭제
                                .deleteCookies("Authorization")
                );
//        http
//                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);
        //세션 설정
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }


}