package sys.JoNet.utils;

import java.util.HashMap;

public class Environment {

  private HashMap<String, String> envars = new HashMap();
  private final String[] envarKeys = {"JONET_ENV"};

  public Environment() throws Exception {
    for (String key : envarKeys) {
      try {
        envars.put(key, System.getenv(key));
      } catch (NullPointerException e) {
        throw new Exception("Required envar " + key + " is null.");
      } catch (Exception e) {
        throw e;
      }
    }
  }

  public String getEnvar(String key) {
    return envars.get(key);
  }
}
