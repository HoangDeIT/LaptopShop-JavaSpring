package com.project.LaptopShop.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.LaptopShop.util.constant.RoleEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
public class ResLoginDTO {
    @JsonProperty("access_token")
    private String accessToken;
    private UserLogin user;

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserLogin {
        private long id;
        private String email;
        private String userName;
        private RoleEnum role;
    }

}
