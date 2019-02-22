package sys.JoNet.core;

import org.json.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CoreTest {

	@Test
	@DisplayName("Post a new work")
	void postNewWork() {

        // Mock a message body to pass to the request handler
        JSONObject msgBody = new JSONObject();
        msgBody.put("title", "hands");
        msgBody.put("year", 2019);
        msgBody.put("price", 99.99);
        msgBody.put("editionOf", 10);
        msgBody.put("archived", false);

        // Data access object, abstracts away database implementation from handlers
        // TODO: Fetch envars to instantiate DAO
        // TODO: Since I'll use this everywhere, can I instantiate it in the class and
        // share the object between tests?
        // DAO dao = new DAO(/* params */);

        // Instantiate a new postWorkHandler and pass it a message
        // PostWorkHandler postWorkHandler = new postWorkHandler(dao);
        // postWorkHandler.process(msgBody);
	}
}
