package com.athena.vo;

import lombok.Data;

@Data
public class AddUserReq {

    private String username;
    private String password;
    private String email;
    private String phone;
    private boolean enabled;
}
