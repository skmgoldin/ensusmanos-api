package sys.esm.daos;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;
import java.util.LinkedList;
import lombok.Data;

@Data
public class Work implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final Date clock = new Date();

  private String title;
  private int usdPrice;
  private long published;
  private LinkedList<URI> imageLocs;

  public Work(String title, int usdPrice, LinkedList<URI> imageLocs) {
    this.title = title;
    this.usdPrice = usdPrice;
    this.published = clock.getTime();
    this.imageLocs = imageLocs;
  }
}
