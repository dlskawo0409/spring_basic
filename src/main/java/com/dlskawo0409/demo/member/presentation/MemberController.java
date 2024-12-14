package com.dlskawo0409.demo.member.presentation;

import com.dlskawo0409.demo.member.application.MemberService;
import com.dlskawo0409.demo.member.dto.request.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "Member", description = "회원관리 API")
public class MemberController {
    private final MemberService memberService;

    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "회원가입 OK!!"),
            @ApiResponse(responseCode = "400", description = "이메일 중복 혹은 닉네임 중복!"),
            @ApiResponse(responseCode = "500", description = "서버에러!!") })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> joinMember(@Valid @RequestPart("member") MemberJoinRequest memberJoinRequest,
                                        @Parameter(
                                                description = "multipart/form-data 형식의 이미지를 input으로 받습니다.",
                                                content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
                                        )
                                        @RequestPart("image") MultipartFile multipartFile) throws Exception {
        memberService.createMemberService (memberJoinRequest, multipartFile) ;
        return new ResponseEntity<>("회원이 성공적으로 추가되었습니다.", HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "회원 정보 확인", description = "회원 정보를 확인합니다.")
    public ResponseEntity<?> getMemberController(@AuthenticationPrincipal CustomMemberDetails loginMember){
        return ResponseEntity.ok(memberService.getMemberService(loginMember));
    }


    @PostMapping("/check-email")
    @Operation(summary = "이메일 중복 확인", description = "입력된 이메일의 중복 여부를 확인합니다.")
    public ResponseEntity<?> checkEmail(@RequestBody MemberUsernameDuplicateRequest memberUsernameDuplicateRequest){
        boolean isExist = memberService.duplicateUsernameService(memberUsernameDuplicateRequest.username());
        return ResponseEntity.ok(isExist);
    }

    @PostMapping("/check-nickname")
    @Operation(summary = "닉네임 중복 확인", description = "입력된 닉네임의 중복 여부를 확인합니다.")
    public ResponseEntity<?> checkNickname(@RequestBody MemberNickNameDuplicateRequest memberNickNameDuplicateRequest){
        boolean isExist = memberService.duplicateNicknameService(memberNickNameDuplicateRequest.nickname());
        return ResponseEntity.ok(isExist);
    }

    @PutMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "프로필 이미지 변경", description = "프로필 이미지를 변경 합니다.")
    public ResponseEntity<?> modifyProfile(
            @Parameter(
                    description = "multipart/form-data 형식의 이미지를 input으로 받습니다.",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestPart("image") MultipartFile multipartFile,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) throws IOException {
        var memberProfileUpdateResponse = memberService.updateProfile(loginMember, multipartFile);
        return ResponseEntity.ok(memberProfileUpdateResponse);
    }

    @PutMapping(
            value = "",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<?> modifyMember(
            @Valid @RequestPart("member") MemberUpdateRequest memberUpdateDto,
            @RequestPart(value = "image", required = false) MultipartFile multipartFile,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) throws IOException {
        var memberUpdateResponse = memberService.updateMember(memberUpdateDto, multipartFile, loginMember);
        return ResponseEntity.ok(memberUpdateResponse);
    }


    @DeleteMapping("")
    public ResponseEntity<?> removeMember(@AuthenticationPrincipal CustomMemberDetails loginMember){
        memberService.deleteMember(loginMember);
        return ResponseEntity.noContent().build();
    }




}