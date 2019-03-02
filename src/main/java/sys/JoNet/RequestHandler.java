package sys.JoNet;

public interface RequestHandler<P extends Validatable> {
  Answer process(P payload);
}
