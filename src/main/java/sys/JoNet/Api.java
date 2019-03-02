package sys.JoNet;

import static spark.Spark.*;

import sys.JoNet.auth.GenerateUserTokenRequestHandler;

public class Api {
  public static void main(String[] args) {
    port(Integer.parseInt(System.getenv("JONET_PORT")));
    post("/GenerateUserToken/", new GenerateUserTokenRequestHandler());
  }
}
