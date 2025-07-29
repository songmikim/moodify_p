package xyz.moodf.member.controllers;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RequestLogin implements Serializable {
    private String email;
    private String password;
    private boolean autoLogin;
    private String redirectUrl;
    private List<String> fieldErrors; // 필드명_에러코드
    private List<String> globalErrors;
}
