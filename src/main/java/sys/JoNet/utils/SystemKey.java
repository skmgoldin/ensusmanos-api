package sys.JoNet.utils;

import com.google.common.io.BaseEncoding;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Date;
import java.util.Random;
import software.amazon.awssdk.services.secretsmanager.*;
import software.amazon.awssdk.services.secretsmanager.model.*;

public class SystemKey {
  private static final long UPDATE_INTERVAL = 86400000;
  private static final Date clock = new Date();
  private static long lastUpdated = 0;
  private static String SYSTEM_KEY = getSystemKey();

  // The system key is used for signing and verifying user JWTs.
  public static String getSystemKey() {
    if ((clock.getTime() - lastUpdated) > UPDATE_INTERVAL) {
      if (System.getenv("JONET_API_TEST").equals("true")) {
        SYSTEM_KEY = getTestSystemKey();
      } else {
        SYSTEM_KEY = getEnvSystemKey();
      }
    }

    lastUpdated = new Date().getTime();
    return SYSTEM_KEY;
  }

  private static String getEnvSystemKey() {
    SecretsManagerClient client = SecretsManagerClient.builder().build();
    String keyArn = System.getenv("JONET_API_SYSTEM_KEY_ARN");

    // Create and send a request to get the secret value.
    GetSecretValueRequest request = GetSecretValueRequest.builder().secretId(keyArn).build();
    GetSecretValueResponse response = client.getSecretValue(request);

    // Return the secret as a string
    if (response.secretString() != null) {
      return response.secretString();
    } else {
      return new String(
          Base64.getDecoder().decode(response.secretBinary().asByteArray()),
          Charset.forName("UTF-8"));
    }
  }

  private static String getTestSystemKey() {
    final Random random = new Random();

    final byte[] randomBytes = new byte[32];
    random.nextBytes(randomBytes);
    return BaseEncoding.base16().encode(randomBytes);
  }
}
