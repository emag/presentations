package shibuyajava.artemis;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

public class RestProducer {

  public static void main(String[] args) throws Exception {
    Client client = ClientBuilder.newBuilder().build();

    try {
      Response response = client
        .target("http://localhost:8080/queues/orders")
        .request()
        .head();

      String msgCreateLink = response.getHeaderString("msg-create");

      response = client
        .target(msgCreateLink)
        .request()
        .post(Entity.json("{\"order\" : \"Test Order\"}"));

      if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
        System.out.println("Success!");
      } else {
        System.out.println("Failed to post: " + response.getStatusInfo());
      }

    } finally {
      client.close();
    }

  }

}
