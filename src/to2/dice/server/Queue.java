package to2.dice.server;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;

/**
 * Created by Fan on 2015-01-11.
 */
public abstract class Queue implements Runnable {
    protected Connection connection;
    protected Channel channel;

    public Queue() throws IOException {
        this("localhost");
    }

    public Queue(String host) throws IOException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        connection = factory.newConnection();
        channel = connection.createChannel();
    }

    public void close() {
        try {
            channel.close();
            connection.close();
        }
        catch (Exception e) {}
    }
}
