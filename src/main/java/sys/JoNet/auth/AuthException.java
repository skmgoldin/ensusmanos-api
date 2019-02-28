package sys.JoNet.auth;

class UserAuthException extends Exception {
  public UserAuthException() {
    super("User presented invalid credentials");
  }
}
