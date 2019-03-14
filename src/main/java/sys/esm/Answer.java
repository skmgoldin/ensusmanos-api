package sys.esm;

import lombok.Data;

@Data
public class Answer {

  private int code;
  private String body;

  public Answer(int code) {
    this.code = code;
  }

  public Answer(int code, String body) {
    this.code = code;
    this.body = body;
  }
}
