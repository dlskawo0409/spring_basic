package com.dlskawo0409.demo.member.application;


import com.dlskawo0409.demo.common.Image.application.ImageService;
import com.dlskawo0409.demo.common.Image.domain.Image;
import com.dlskawo0409.demo.common.Image.domain.ImageType;
import com.dlskawo0409.demo.member.domain.Member;
import com.dlskawo0409.demo.member.domain.MemberRepository;
import com.dlskawo0409.demo.member.domain.Role;
import com.dlskawo0409.demo.member.dto.request.CustomMemberDetails;
import com.dlskawo0409.demo.member.dto.request.MemberJoinRequest;
import com.dlskawo0409.demo.member.dto.request.MemberUpdateRequest;
import com.dlskawo0409.demo.member.dto.response.MemberUpdateResponse;
import com.dlskawo0409.demo.member.exception.MemberErrorCode;
import com.dlskawo0409.demo.member.exception.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static com.dlskawo0409.demo.member.exception.MemberErrorCode.MEMBER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ImageService imageService;


    @Transactional
    public Member createMemberService(MemberJoinRequest memberJoinRequest, MultipartFile multipartFile) throws Exception {

        checkUsernameDuplicate(memberJoinRequest.username());
        checkNicknameDuplicate(memberJoinRequest.nickname());

        Member member  = Member.builder()
                .username(memberJoinRequest.username())
                .password(bCryptPasswordEncoder.encode(memberJoinRequest.password()))
                .nickname(memberJoinRequest.nickname())
                .role(Role.USER)
                .build();

        memberRepository.save(member);
        Image profile = imageService.upload(multipartFile, String.valueOf(member.getMemberId()), ImageType.PROFILE);
        member.setProfile(profile);
        memberRepository.save(member);
        return member;
    }

    @Transactional(readOnly = true)
    public Optional<Member> getMemberService(CustomMemberDetails loginMember){
        return memberRepository.findById(loginMember.getMemberId());
    }


    private void checkUsernameDuplicate(String username) {
        if (memberRepository.existsByUsername(username)) {
            throw new MemberException.MemberConflictException(MemberErrorCode.MEMBER_ALREADY_EXIST, username);
        }
    }

    private void checkNicknameDuplicate(String nickname){
        if(memberRepository.existsByNickname(nickname)){
            throw new MemberException.MemberConflictException(MemberErrorCode.ILLEGAL_NICKNAME_ALREADY_EXISTS, nickname);
        }

    }


    public boolean duplicateUsernameService(String username){
        try{
            checkUsernameDuplicate(username);
        }catch(Exception e){
            return true;
        }
        return false;
    }

    public boolean duplicateNicknameService(String nickName) {
        try{
            checkNicknameDuplicate(nickName);
        }catch(Exception e){
            return true;
        }
        return false;
    }

    @Transactional
    public String updateProfile(CustomMemberDetails loginMember, MultipartFile multipartFile) throws IOException {
        Member member = Optional.ofNullable(loginMember.getMember())
                .orElseThrow(()->new MemberException.MemberConflictException(MEMBER_NOT_FOUND.ILLEGAL_NICKNAME_ALREADY_EXISTS, loginMember.getMember().getUsername()));

        member = memberRepository.findByUsername(member.getUsername());
        Image beforeProfile = member.getProfile();
        Image profile = imageService.update(multipartFile, beforeProfile.getImageId(), beforeProfile.getImageType());
        Image image = memberRepository.findByUsername(member.getUsername()).getProfile();

        imageService.delete(image.getImageId());

        // s3 이미지 삭제도 다음에 넣도록 하자! lazy 하게 해야 해서 to do로 남겨줌
        return profile.getImageUrl();
    }

    @Transactional
    public MemberUpdateResponse updateMember(MemberUpdateRequest memberUpdateRequest, MultipartFile multipartFile, CustomMemberDetails loginMember) throws IOException {
        Member member = Optional.ofNullable(loginMember.getMember())
                .orElseThrow(()->new MemberException.MemberConflictException(MEMBER_NOT_FOUND.ILLEGAL_NICKNAME_ALREADY_EXISTS, loginMember.getMember().getNickname()));

        member = memberRepository.findByUsername(member.getUsername());

        if(!memberUpdateRequest.nickname().isEmpty() && !memberUpdateRequest.nickname().equals(member.getNickname())){
            checkNicknameDuplicate(memberUpdateRequest.nickname());
        }

        // Handle profile image if present
        if (multipartFile != null && !multipartFile.isEmpty()) {
            Image beforeImage = member.getProfile();
            Image profile = imageService.update(multipartFile, beforeImage.getImageId(), beforeImage.getImageType());
            member.setProfile(profile);
        }

        // Save the updated member
        memberRepository.save(member);

        return MemberUpdateResponse.builder()
                .memberId(member.getMemberId())
                .username(member.getUsername())
                .nickname(member.getNickname())
                .profile(member.getProfile())
                .build();

    }

    public void deleteMember(CustomMemberDetails loginMember) {
        Member member = Optional.ofNullable(loginMember.getMember())
                .orElseThrow(()->new MemberException.MemberConflictException(MEMBER_NOT_FOUND.ILLEGAL_NICKNAME_ALREADY_EXISTS, loginMember.getMember().getNickname()));
        memberRepository.delete(member);
    }

}