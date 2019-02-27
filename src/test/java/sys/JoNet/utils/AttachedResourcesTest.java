package sys.JoNet.utils;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AttachedResourcesTest {
  private static final String[] resourceRefNames = {"SYSTEM_KEY", "USERS_DB"};
  private static final String appName = "jonet";
  private static final String env = System.getenv("JONET_ENV");
  private static AttachedResources attachedResources =
      new AttachedResources(resourceRefNames, appName, env);

  @Test
  void canonicalNamesGeneratedForAllReferenceNames() {
    List<String> returnedReferenceNames = attachedResources.getReferenceNames();

    Assertions.assertTrue(returnedReferenceNames.containsAll(Arrays.asList(resourceRefNames)));
  }

  @Test
  void canonicalNamesCorrectlyGenerated() {
    List<String> referenceNames = attachedResources.getReferenceNames();

    // Make sure that all canonical names match a correct regex format
    for (String referenceName : referenceNames) {
      @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
      String canonicalName = attachedResources.getCanonicalName(referenceName);

      Assertions.assertTrue(canonicalName.matches("[a-z0-9]+[.][a-z0-9_]+[.][a-z0-9]+"));

      // Do a sanity check by explicitly constructing a canonical value
      // we know should exist
      if (referenceName.equals("SYSTEM_KEY")) {
        String sanityCheckValue = "".concat("jonet.system_key").concat("." + env);

        Assertions.assertTrue(canonicalName.equals(sanityCheckValue));
      }
    }
  }
}
