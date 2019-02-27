package sys.JoNet.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * The AttachedResources class abstracts away "canonical" names for attached resources (such as
 * databases) from a "reference" name. A reference name like "MY_DB" may thereby be used throughout
 * the application while the AttachedResources class figures out what the canonical name for that
 * resource is based on the application name and the current environment the application is running
 * in. "MY_DB" may be translated to "myapp.my_db.prod", for example, while its running in the
 * production environment.
 *
 * <p>Canonical names should be in the format <application>.<resource>.<environment> and satisfy the
 * regex [a-z0-9]+[.][a-z0-9_]+[.][a-z0-9]+
 */
public class AttachedResources {
  private static HashMap<String, String> refNameToCName = new HashMap();

  /**
   * The constructor must be passed an application prefix and an environmental postfix from its
   * invocation context. At instantiation, canonical names are generated for all of the provided
   * resource reference names.
   *
   * @param resourceReferenceNames the "friendly" names of attached resources which are referenced
   *     in application source code.
   * @param applicationPrefix the name of the application to generate canonical names for.
   * @param envPostFix the environment modifier to generate canonical names for.
   */
  public AttachedResources(
      String[] resourceReferenceNames, String applicationPrefix, String envPostfix) {

    for (String referenceName : resourceReferenceNames) {
      String resourceCanonicalName =
          ""
              .concat(applicationPrefix.toLowerCase(Locale.US))
              .concat("." + referenceName.toLowerCase(Locale.US))
              .concat("." + envPostfix.toLowerCase(Locale.US));

      refNameToCName.put(referenceName, resourceCanonicalName);
    }
  }

  public String getCanonicalName(String referenceName) {
    return refNameToCName.get(referenceName);
  }

  public List<String> getReferenceNames() {
    return new ArrayList(refNameToCName.keySet());
  }
}
