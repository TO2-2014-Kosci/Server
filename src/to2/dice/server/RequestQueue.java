package to2.dice.server;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * Created by Fan on 2015-01-11.
 */
public class RequestQueue extends Queue {

    private final QueueingConsumer consumer;

    public RequestQueue() throws IOException {
        super();

        consumer = new QueueingConsumer(channel);

        channel.queueDeclare("requests", false, false, false, null);
        channel.basicQos(1);
        channel.basicConsume("requests", false, consumer);
    }

    @Override
    public void run() {
        while(!Thread.interrupted()) {
            try {
                QueueingConsumer.Delivery delivery = consumer.nextDelivery();

                AMQP.BasicProperties props = delivery.getProperties();
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder()
                        .correlationId(props.getCorrelationId())
                        .build();

                String message = new String(delivery.getBody());
                System.out.println(message); //TODO Handle request

                channel.basicPublish("", props.getReplyTo(), replyProps, "ok".getBytes()); //TODO "ok" -> actual response
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
            catch (InterruptedException e) {}
            catch (IOException e) {
                System.out.println("Niepodziewany błąd podczas wysyłania do kolejki");
                e.printStackTrace();
            }
        }

        System.out.println("Zakończono odbieranie żądań");
    }
}
