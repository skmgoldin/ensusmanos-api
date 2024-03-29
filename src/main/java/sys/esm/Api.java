package sys.esm;

import static spark.Spark.*;

import sys.esm.auth.GenerateUserTokenRequestHandler;

public class Api {
  public static void main(String[] args) {
    port(Integer.parseInt(System.getenv("ESM_API_PORT")));
    post("/GenerateUserToken/", new GenerateUserTokenRequestHandler());
    get(
        "/ping/",
        (req, res) -> {
          System.out.println("/ping/");
          res.status(200);
          return "OK";
        });
  }
}
