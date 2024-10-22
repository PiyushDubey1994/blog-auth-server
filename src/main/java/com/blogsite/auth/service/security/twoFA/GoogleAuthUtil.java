package com.blogsite.auth.service.security.twoFA;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GoogleAuthUtil {
    private static final Logger logger = LoggerFactory.getLogger(GoogleAuthUtil.class);

    public void GoogleAuthenticator() {

    }
    @Value("${google-auth.issuer}")
    private String issuer;

    @Value("${google-auth.label}")
    private String label;

    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    public GoogleAuthenticatorKey createCredentials() {
        return gAuth.createCredentials();
    }

    public boolean authorize(String secret, int code) {
        return gAuth.authorize(secret, code);
    }

    public String getQRCode(GoogleAuthenticatorKey key) {
        String accountName = "Indian Blog";
        return GoogleAuthenticatorQRGenerator.getOtpAuthURL(issuer, accountName, key);
    }
}
