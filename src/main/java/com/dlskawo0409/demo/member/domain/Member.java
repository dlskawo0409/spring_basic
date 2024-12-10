package com.dlskawo0409.demo.member.domain;

import com.dlskawo0409.demo.common.Image.domain.Image;
import com.dlskawo0409.demo.common.domain.BasicEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nimbusds.openid.connect.sdk.claims.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Builder
@Schema(description = "Member")
public class Member extends BasicEntity implements UserDetails {

    private static final String VALID_EMAIL_URL_REGEX = "^[a-zA-Z0-9]+@[a-zA-Z]+\\.[a-zA-Z]{2,}$";
    private static final int NICKNAME_LENGTH = 20;

    private Long memberId;
    private String username;
    private String password;
    private String nickname;
    private Gender gender;
    private Role role;
    private Image profile;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getKey()));
    }

    @Override
    public String getUsername() {
        return username;
    }
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return memberId.equals(member.memberId) && username.equals(member.username) && nickname.equals(member.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, username, nickname);
    }

    public void setImage(Image profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return "Member{" +
                "memberId=" + memberId +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", gender='" + gender + '\'' +
                ", role=" + role +
                ", profile=" + profile +
                '}';
    }
}