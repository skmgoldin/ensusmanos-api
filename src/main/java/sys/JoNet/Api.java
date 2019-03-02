package sys.JoNet;

import static spark.Spark.*;

import sys.JoNet.auth.GenerateUserTokenRequestHandler;

public class Api {
  public static void main(String[] args) {
    post("/GenerateUserToken/", new GenerateUserTokenRequestHandler());
  }
}
