package com.dlskawo0409.demo.member.application;

import com.dlskawo0409.demo.member.domain.Member;
import com.dlskawo0409.demo.member.domain.MemberRepository;
import com.dlskawo0409.demo.member.dto.request.CustomMemberDetails;
import com.dlskawo0409.demo.member.exception.MemberErrorCode;
import com.dlskawo0409.demo.member.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
@RequiredArgsConstructor
public class CustomMemberDetailsService  implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member userData = Optional.ofNullable(memberRepository.findByUsername(username))
                .orElseThrow(() -> new MemberException.MemberConflictException(MemberErrorCode.MEMBER_NOT_FOUND, username));

        if (userData != null) {
            return new CustomMemberDetails(userData);
        }


        throw new UsernameNotFoundException("User not found with email: " + username);
    }

}
