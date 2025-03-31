package com.project.LaptopShop.domain.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordDTO {
    private String password;
    private String code;
}
