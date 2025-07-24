package xyz.moodf.member.controllers;

import lombok.Data;

@Data
public class RequestFindIdPassword {
    private String mbId;
    private String mbEmail;
}
