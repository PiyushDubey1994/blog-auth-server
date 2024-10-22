package com.blogsite.auth.payload.response;


import lombok.Data;

import java.util.List;

@Data
public class JwtResponse {
    public JwtResponse() {

    }

    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private boolean enabled2fa;
    private List<String> roles;

    public JwtResponse(String accessToken, Long id, String username, String email,boolean enabled2fa, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.enabled2fa=enabled2fa;
        this.roles = roles;
    }


}