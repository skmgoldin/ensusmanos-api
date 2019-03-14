package sys.esm.daos;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.Date;
import java.util.LinkedList;
import org.junit.jupiter.api.*;

class WorkTest {

  @Test
  void shouldSerializeAndDeserialize() throws Exception {
    final Date clock = new Date();
    final LinkedList<URI> imageLocs = new LinkedList();
    imageLocs.add(new URI("google.com"));
    imageLocs.add(new URI("hidden.computer"));

    final long publishTime = clock.getTime();
    final Work work = new Work("hands", 10000, imageLocs);

    final ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
    final ObjectOutputStream objectOutStream = new ObjectOutputStream(byteOutStream);

    objectOutStream.writeObject(work);
    objectOutStream.flush();
    objectOutStream.close();

    final byte[] workBytes = byteOutStream.toByteArray();

    final ByteArrayInputStream byteInStream = new ByteArrayInputStream(workBytes);
    final ObjectInputStream objectInStream = new ObjectInputStream(byteInStream);

    final Work rehydratedWork = (Work) objectInStream.readObject();
    objectInStream.close();

    Assertions.assertTrue(rehydratedWork.getTitle().equals(work.getTitle()));
    Assertions.assertTrue(rehydratedWork.getUsdPrice() == work.getUsdPrice());
    Assertions.assertTrue(rehydratedWork.getImageLocs().equals(work.getImageLocs()));
    Assertions.assertTrue(
        ((publishTime - 10000) < rehydratedWork.getPublished())
            && (rehydratedWork.getPublished() < (publishTime + 10000)));
  }
}
