package sys.JoNet.daos;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.junit.jupiter.api.*;

class UserTest {

  @Test
  void userIsSerializableAndDeserializable() throws IOException, ClassNotFoundException {
    final User user = new User("John", "Doe");
    final ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
    final ObjectOutputStream objectOutStream = new ObjectOutputStream(byteOutStream);

    objectOutStream.writeObject(user);
    objectOutStream.flush();
    objectOutStream.close();

    final byte[] userBytes = byteOutStream.toByteArray();

    final ByteArrayInputStream byteInStream = new ByteArrayInputStream(userBytes);
    final ObjectInputStream objectInStream = new ObjectInputStream(byteInStream);

    final User rehydratedUser = (User) objectInStream.readObject();
    objectInStream.close();

    Assertions.assertTrue(rehydratedUser.getUsername().equals(user.getUsername()));
    Assertions.assertTrue(rehydratedUser.getSecretHash().equals(user.getSecretHash()));
    Assertions.assertTrue(rehydratedUser.isAdmin() == user.isAdmin());
  }
}
