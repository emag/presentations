package shibuyajava.artemis;

import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSContext;
import javax.jms.Queue;

public class Consumer {

  public static void main(final String[] args) throws Exception {
    Queue queue = ActiveMQJMSClient.createQueue("exampleQueue");

    try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
         JMSContext context = cf.createContext()) {

      String message = context
        .createConsumer(queue)
        .receiveBody(String.class);

      System.out.println("Received: " + message);
    }

  }

}
