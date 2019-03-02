package sys.JoNet.utils;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Date;
import software.amazon.awssdk.services.secretsmanager.*;
import software.amazon.awssdk.services.secretsmanager.model.*;

public class SystemKey {
  private static final String[] resourceRefNames = {"SYSTEM_KEY"};
  private static final String appName = "jonet";
  private static final String env = System.getenv("JONET_ENV");
  private static final AttachedResources attachedResources =
      new AttachedResources(resourceRefNames, appName, env);

  private static final long UPDATE_INTERVAL = 86400000;
  private static final Date clock = new Date();
  private static long lastUpdated = 0;
  private static String SYSTEM_KEY = getSystemKey();

  // The system key is used for signing and verifying user JWTs.
  public static String getSystemKey() {
    if ((clock.getTime() - lastUpdated) > UPDATE_INTERVAL) {
      SecretsManagerClient client = SecretsManagerClient.builder().build();
      String keyCName = attachedResources.getCanonicalName("SYSTEM_KEY");

      // Create and send a request to get the secret value.
      GetSecretValueRequest request = GetSecretValueRequest.builder().secretId(keyCName).build();
      GetSecretValueResponse response = client.getSecretValue(request);

      // Return the secret as a string
      if (response.secretString() != null) {
        SYSTEM_KEY = response.secretString();
      } else {
        SYSTEM_KEY =
            new String(
                Base64.getDecoder().decode(response.secretBinary().asByteArray()),
                Charset.forName("UTF-8"));
      }
    }

    return SYSTEM_KEY;
  }
}
