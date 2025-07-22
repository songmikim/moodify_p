package xyz.moodf.member.controllers;

import lombok.Data;
import xyz.moodf.global.search.CommonSearch;
import xyz.moodf.member.constants.Authority;

import java.util.List;

@Data
public class MemberSearch extends CommonSearch {
    private List<Authority> authority;
}
