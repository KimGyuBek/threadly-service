package com.threadly.user.command;

import com.threadly.user.UserGenderType;
import lombok.Builder;
import lombok.Getter;

/**
 * 사용자 초기 profile 설정 dto
 */
@Getter
@Builder
public class UserSetProfileCommand {

    private String nickname;
    private String statusMessage;
    private String bio;
    private UserGenderType gender;
    private String profileImageUrl;


}
