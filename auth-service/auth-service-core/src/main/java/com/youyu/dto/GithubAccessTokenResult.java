package com.youyu.dto;

import lombok.Data;

@Data
public class GithubAccessTokenResult {
    String accessToken;

    String scope;

    String tokenType;
}
