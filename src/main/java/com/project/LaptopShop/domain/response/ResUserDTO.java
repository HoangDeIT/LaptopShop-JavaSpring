package com.project.LaptopShop.domain.response;

import java.time.Instant;

import com.project.LaptopShop.util.constant.RoleEnum;
import com.project.LaptopShop.util.constant.TypeEnum;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResUserDTO {
    private long id;
    private String userName;
    private String email;
    private RoleEnum role;
    private TypeEnum type;
    private String image;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

}
