package shibuyajava.artemis;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class RestConsumer {

  public static void main(String[] args) throws Exception {
    Client client = ClientBuilder.newBuilder().build();

    try {
      Response response = client
        .target("http://localhost:8080/queues/orders")
        .request()
        .head();

      String msgPullConsumersLink = response.getHeaderString("msg-pull-consumers");

      Form form = new Form();
      form.param("autoAck", "false");

      response = client
        .target(msgPullConsumersLink)
        .request()
        .post(Entity.form(form));

      response.close();

      String msgAckNextLink = response.getHeaderString("msg-acknowledge-next");

      response = client
        .target(msgAckNextLink)
        .request(MediaType.APPLICATION_JSON)
        .post(null);

      if (response.getStatus() == Response.Status.OK.getStatusCode()) {
        System.out.println(response.readEntity(String.class));
      } else {
        System.out.println("Failed: " + response.getStatusInfo());
      }

    } finally {
      client.close();
    }
  }

}
