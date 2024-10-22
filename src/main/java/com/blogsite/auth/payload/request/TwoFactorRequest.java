package com.blogsite.auth.payload.request;

import lombok.Data;

@Data
public class TwoFactorRequest {
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private int code;
    private String email;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
