package com.carcrowd.security;

import com.carcrowd.dto.customer.GoogleUserPayload;

public interface GoogleTokenVerifierService {

    /**
     * Validates Google identity token including signature, issuer, audience, and expiration.
     * Extracts Google stable subject identifier (sub), email, name, and picture.
     *
     * @param idTokenString Raw ID token passed from frontend Google Identity Services
     * @return Verified GoogleUserPayload
     */
    GoogleUserPayload verifyToken(String idTokenString);
}
