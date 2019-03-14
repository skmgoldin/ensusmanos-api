package sys.esm;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * The AbstractRequestHandler class templatizes basic functionality which request handlers should
 * furnish.
 */
public abstract class AbstractRequestHandler<P extends Validatable>
    implements RequestHandler<P>, Route {

  private Class<P> payloadClass;

  public AbstractRequestHandler(Class<P> payloadClass) {
    this.payloadClass = payloadClass;
  }

  public final Answer process(P payload) {
    if (!payload.isValid()) {
      return new Answer(400);
    } else {
      return processImpl(payload);
    }
  }

  protected abstract Answer processImpl(P payload);

  public Object handle(Request req, Response res) throws Exception {
    if (!isAuthenticated(req.headers("Authentication"))) {
      res.status(401);
      return "";
    }
    try {
      ObjectMapper objMapper = new ObjectMapper();
      P payload = objMapper.readValue(req.body(), payloadClass);
      Answer answer = process(payload);
      res.status(answer.getCode());
      res.body(answer.getBody());
      return answer.getBody();
    } catch (JsonMappingException | JsonParseException e) {
      res.status(400);
      res.body(e.getMessage());
      return e.getMessage();
    }
  }

  protected boolean isAuthenticated(String authHeader) {
    return true;
  }
}
