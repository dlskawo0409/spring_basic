package com.dlskawo0409.demo.member.application;


import com.dlskawo0409.demo.auth.jwt.JWTUtil;
import com.dlskawo0409.demo.common.Image.application.ImageService;
import com.dlskawo0409.demo.common.Image.domain.Image;
import com.dlskawo0409.demo.common.Image.domain.ImageType;
import com.dlskawo0409.demo.member.domain.Member;
import com.dlskawo0409.demo.member.domain.MemberRepository;
import com.dlskawo0409.demo.member.domain.Role;
import com.dlskawo0409.demo.member.dto.request.CustomMemberDetails;
import com.dlskawo0409.demo.member.dto.request.MemberJoinRequest;
import com.dlskawo0409.demo.member.dto.request.MemberUpdateRequest;
import com.dlskawo0409.demo.member.exception.MemberErrorCode;
import com.dlskawo0409.demo.member.exception.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.dlskawo0409.demo.member.exception.MemberErrorCode.MEMBER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ImageService imageService;
    private final JWTUtil jwtUtil;
//    private final S3Service s3Service;


    @Transactional
    public boolean createMemberService(MemberJoinRequest memberJoinRequest, MultipartFile multipartFile) throws IOException {

        checkUsernameDuplicate(memberJoinRequest.username());
        checkNicknameDuplicate(memberJoinRequest.nickname());

        if(memberJoinRequest.role().equalsKeyOrName(Role.ADMIN.getKey())){
            throw new MemberException.MemberBadRequestException(MemberErrorCode.ILLEGAL_ROLE);
        }

        Member member = Member.builder()
                .username(memberJoinRequest.username())
                .password(bCryptPasswordEncoder.encode(memberJoinRequest.password()))
                .nickname(memberJoinRequest.nickname())
                .role(memberJoinRequest.role())
                .build();
        memberRepository.save(member);
        Image profile = imageService.upload(multipartFile, String.valueOf(member.getMemberId()), ImageType.PROFILE);
        return true;
    }




    public Member getMemberService(CustomMemberDetails loginMember){
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
    public String updateProfile(CustomMemberDetails loginMember, MultipartFile multipartFile){
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
    public MemberUpdateResponse updateMember(MemberUpdateRequest pmemberUpdateRequest, MultipartFile multipartFile, CustomMemberDetails loginMember) {
        Member memberBefore = Optional.ofNullable(loginMember.getMember())
                .orElseThrow(()->new MemberException.MemberConflictException(MEMBER_NOT_FOUND.ILLEGAL_NICKNAME_ALREADY_EXISTS, loginMember.getMember().getNickname()));

        memberBefore = memberRepository.findByUsername(memberBefore.getUsername());

        if(!pmemberUpdateRequest.nickname().isEmpty() && !pmemberUpdateRequest.nickname().equals(memberBefore.getNickname())){
            checkNicknameDuplicate(pmemberUpdateRequest.nickname());
        }

        // Handle profile image if present
        if (multipartFile != null && !multipartFile.isEmpty()) {
            Image beforeImage = memberBefore.getProfile();
            Image profile = imageService.update(multipartFile, beforeImage.getImageId(), beforeImage.getImageType());
            memberBefore.setProfile(profile);
        }


        Member member = Member.builder()
                .username(memberBefore.getUsername())
                .password(pmemberUpdateRequest.password() == null || pmemberUpdateRequest.getPassword().trim().isEmpty() ? "" : bCryptPasswordEncoder.encode(memberUpdateDto.getPassword()))
                .nickname(memberUpdateDto.getNickname() == null || memberUpdateDto.getNickname().trim().isEmpty() ? null : memberUpdateDto.getNickname())
                .gender(memberUpdateDto.getGender() == null ? null : memberUpdateDto.getGender())
                .role(memberUpdateDto.getRole() == null ? Role.USER : memberUpdateDto.getRole())
                .profile(memberBefore.getProfile())
                .build();


        System.out.println( member.toString());
        // Save the updated member
        memberRepository.update(member);
        return MemberUpdateResponse.builder()
                .nickname(member.getNickname())
                .gender(member.getGender())
                .role(member.getRole())
                .build();

    }

    public void updateMemberCollegeId(Integer collegeId, CustomMemberDetails loginMember){
        memberRepository.updateCollegeId(collegeId, loginMember.getMemberId());
    }

    public String deleteMember(CustomMemberDetails loginMember) {
        Member member = Optional.ofNullable(loginMember.getMember())
                .orElseThrow(()->new MemberException.MemberConflictException(MEMBER_NOT_FOUND.ILLEGAL_NICKNAME_ALREADY_EXISTS, loginMember.getMember().getNickname()));

        member.setDeletedAt(LocalDateTime.now());
        memberRepository.save(member);
        return "non-active";
    }

}