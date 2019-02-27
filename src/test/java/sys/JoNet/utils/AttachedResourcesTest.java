package sys.JoNet.utils;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AttachedResourcesTest {
  final AttachedResources attachedResources;
  final Environment environment;

  final String[] resourceReferenceNames = {"SYSTEM_KEY", "USERS_DB", "WORKS_DB"};

  public AttachedResourcesTest() throws Exception {
    environment = new Environment();
    attachedResources =
        new AttachedResources(resourceReferenceNames, "JoNet", environment.getEnvar("JONET_ENV"));
  }

  @Test
  void canonicalNamesGeneratedForAllReferenceNames() {
    List<String> returnedReferenceNames = attachedResources.getReferenceNames();

    Assertions.assertTrue(
        returnedReferenceNames.containsAll(Arrays.asList(resourceReferenceNames)));
  }

  @Test
  void canonicalNamesCorrectlyGenerated() {
    List<String> referenceNames = attachedResources.getReferenceNames();

    // Make sure that all canonical names match a correct regex format
    for (String referenceName : referenceNames) {
      String canonicalName = attachedResources.getCanonicalName(referenceName);

      Assertions.assertTrue(canonicalName.matches("[a-z0-9]+[.][a-z0-9_]+[.][a-z0-9]+"));

      // Do a sanity check by explicitly constructing a canonical value
      // we know should exist
      if (referenceName.equals("SYSTEM_KEY")) {
        String sanityCheckValue =
            "".concat("jonet.system_key").concat("." + environment.getEnvar("JONET_ENV"));

        Assertions.assertTrue(canonicalName.equals(sanityCheckValue));
      }
    }
  }
}
