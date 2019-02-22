package sys.JoNet.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.*;

class AuthTest {

    final String JONET_ENV;
    final String SYSTEM_KEY;

    public AuthTest() {
        Auth auth = new Auth();

        JONET_ENV = System.getenv("JONET_ENV");
        SYSTEM_KEY = auth.fetchSystemKey();
    }

    @Test
    @DisplayName("Should not log a user with invalid credentials in")
    void failLoginOnBadCredentials() {
    }

    @Test
    @DisplayName("Should log a user with valid credentials in")
    void passLoginOnGoodCredentials() throws AuthException {
        Auth auth = new Auth();
        
        String encodedToken = auth.loginUser("joann.arosemena@gmail.com", "password123");
        
        Algorithm algorithm = Algorithm.HMAC256(SYSTEM_KEY);
        JWTVerifier verifier = JWT.require(algorithm)
        .withIssuer("jonet")
        .build();

        DecodedJWT decodedToken = verifier.verify(encodedToken);
        System.out.println(decodedToken.getClaim("isAdmin").asBoolean());
    }

    @Test
    @DisplayName("Retrieve the JoNet system key from the secret manager")
    void fetchSystemKey() {
        Auth auth = new Auth();
        
        auth.fetchSystemKey();
    }

    @Test
    @DisplayName("Reject invalid signature")
    void rejectInvalidSignature() {
    }

    @Test
    @DisplayName("Accept valid signature")
    void acceptValidSignature() {
    }

    @Test
    @DisplayName("Read valid claim")
    void readValidClaim() {
    }

    @Test
    @DisplayName("Reject invalid claim")
    void rejectInvalidClaim() {
    }
}

