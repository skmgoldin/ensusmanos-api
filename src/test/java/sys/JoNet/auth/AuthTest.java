package sys.JoNet.auth;

import software.amazon.awssdk.services.dynamodb.*;
import software.amazon.awssdk.services.dynamodb.model.*;

import sys.JoNet.utils.AttachedResources;
import sys.JoNet.utils.Environment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.*;

import java.util.Map;

class AuthTest {

    final String SYSTEM_KEY;
    final Auth auth;

    public AuthTest() throws Exception {
        Environment environment = new Environment();
        AttachedResources attachedResources = new AttachedResources(
                                            new String[] {"SYSTEM_KEY", "USERS_DB"}, "jonet",
                                            environment.getEnvar("JONET_ENV"));
        auth = new Auth(attachedResources);

        SYSTEM_KEY = auth.fetchSystemKey();

        AttributeValue testUserEmail = AttributeValue
            .builder()
            .s("joann.arosemena@gmail.com")
            .build();
        AttributeValue testUserSecretHash = AttributeValue
            .builder()
            .s("ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f")
            .build();

        PutItemRequest putRequest = PutItemRequest.builder()
            .tableName(attachedResources.getCanonicalName("USERS_DB"))
            .item(Map.of("user", testUserEmail,
                         "secretHash", testUserSecretHash))
            .build();

        DynamoDbClient ddbc = DynamoDbClient.create();
        ddbc.putItem(putRequest);
    }

    @Test
    @Disabled
    void failLoginOnBadCredentials() {
    }

    @Test
    void passLoginOnGoodCredentials() throws AuthException {
        String encodedToken = auth.loginUser("joann.arosemena@gmail.com", "password123");
        
        Algorithm algorithm = Algorithm.HMAC256(SYSTEM_KEY);
        JWTVerifier verifier = JWT.require(algorithm)
        .withIssuer("jonet")
        .build();

        DecodedJWT decodedToken = verifier.verify(encodedToken);
        System.out.println(decodedToken.getClaim("isAdmin").asBoolean());
    }

    @Test
    @Disabled
    void rejectInvalidSignature() {
    }

    @Test
    @Disabled
    void acceptValidSignature() {
    }

    @Test
    @Disabled
    void readValidClaim() {
    }

    @Test
    @Disabled
    void rejectInvalidClaim() {
    }
}

