package sys.esm;

public interface RequestHandler<P extends Validatable> {
  Answer process(P payload);
}
