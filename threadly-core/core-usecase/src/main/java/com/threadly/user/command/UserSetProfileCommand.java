package com.threadly.user.command;

import com.threadly.user.UserGenderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 초기 profile 설정 dto
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSetProfileCommand {

    private String userId;
    private String nickname;
    private String statusMessage;
    private String bio;
    private UserGenderType gender;
    private String profileImageUrl;


}
