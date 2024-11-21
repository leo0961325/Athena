package com.athena.vo;

import lombok.Data;

@Data
public class AddUserResp {

    private Integer id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private boolean enabled;
    private String test;
}
