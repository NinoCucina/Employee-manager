package com.vti.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileDto {
    private Integer id;
    private String role;
    private String username;
    private String fullName;
    private String departmentName;
}
