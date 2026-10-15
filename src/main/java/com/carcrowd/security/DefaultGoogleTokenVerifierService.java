package com.carcrowd.security;

import com.carcrowd.dto.customer.GoogleUserPayload;
import com.carcrowd.exception.BadRequestException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class DefaultGoogleTokenVerifierService implements GoogleTokenVerifierService {

    private static final Logger log = LoggerFactory.getLogger(DefaultGoogleTokenVerifierService.class);

    private final String configuredClientId;
    private final GoogleIdTokenVerifier verifier;
    private final boolean isDevOrTest;

    public DefaultGoogleTokenVerifierService(
            @Value("${app.google.client-id:}") String configuredClientId,
            org.springframework.core.env.Environment environment
    ) {
        this.configuredClientId = configuredClientId != null ? configuredClientId.trim() : "";

        String[] activeProfiles = environment.getActiveProfiles();
        this.isDevOrTest = Arrays.stream(activeProfiles)
            .anyMatch(p -> p.equalsIgnoreCase("test") || p.equalsIgnoreCase("h2") || p.equalsIgnoreCase("dev"));

        GoogleIdTokenVerifier.Builder builder = new GoogleIdTokenVerifier.Builder(
            new NetHttpTransport(),
            GsonFactory.getDefaultInstance()
        );

        if (!this.configuredClientId.isBlank()) {
            builder.setAudience(Collections.singletonList(this.configuredClientId));
        }

        // Validate Google's official issuers
        builder.setIssuers(Arrays.asList("https://accounts.google.com", "accounts.google.com"));

        this.verifier = builder.build();
    }

    @Override
    public GoogleUserPayload verifyToken(String idTokenString) {
        if (idTokenString == null || idTokenString.isBlank()) {
            throw new BadRequestException("Google ID token is required");
        }

        String trimmedToken = idTokenString.trim();

        // Support dev/test simulation tokens only in non-production environments
        if (isDevOrTest && trimmedToken.startsWith("dev-test-")) {
            log.info("Simulated dev Google token accepted in active dev/test profile: {}", trimmedToken);
            String subSuffix = trimmedToken.replace("dev-test-", "").replace("google-id-token", "12345");
            if (subSuffix.isBlank()) subSuffix = "12345";
            return new GoogleUserPayload(
                "google-sub-" + subSuffix,
                "dev." + subSuffix + "@carcrowd.in",
                true,
                "Dev Customer " + subSuffix,
                null
            );
        }

        try {
            GoogleIdToken idToken = verifier.verify(trimmedToken);
            if (idToken == null) {
                log.warn("Google ID token verification failed (invalid signature, audience, or expired)");
                throw new BadRequestException("Invalid or expired Google identity token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();

            String sub = payload.getSubject();
            if (sub == null || sub.isBlank()) {
                throw new BadRequestException("Google ID token missing subject claim");
            }

            String email = payload.getEmail();
            boolean emailVerified = Boolean.TRUE.equals(payload.getEmailVerified());
            String name = (String) payload.get("name");
            String picture = (String) payload.get("picture");

            return new GoogleUserPayload(sub, email, emailVerified, name, picture);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception verifying Google ID token: {}", e.getMessage(), e);
            throw new BadRequestException("Failed to verify Google identity token: " + e.getMessage());
        }
    }
}
