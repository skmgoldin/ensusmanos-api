package sys.JoNet.core;

import static spark.Spark.*;

public class Core {
  public static void main(String[] args) {
    post(
        "/works",
        (req, res) -> {
          return "Hello";
        });
  }
}
