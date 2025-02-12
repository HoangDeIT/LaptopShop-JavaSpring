package com.project.LaptopShop.domain.response;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterDTO {
    private long id;
    private String userName;
    private String address;
    private String email;
    private Instant createdAt;
    private String createdBy;
}
