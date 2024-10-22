package com.blogsite.auth.exception;

import lombok.Data;

@Data
public class MessageResponse {
    private String message;
    private String qrcode;

    public MessageResponse(String message, String qrCode) {
        this.message = message;
        this.qrcode = qrCode;
    }

}