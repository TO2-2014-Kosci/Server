package to2.dice.server;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import to2.dice.messaging.Response;
import to2.dice.messaging.request.*;

import java.io.IOException;

/**
 * Created by Fan on 2015-01-11.
 */
public class RequestQueue extends Queue {

    private final QueueingConsumer consumer;

    public RequestQueue(Server server, String host) throws IOException {
        super(server, host);

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
                Request request = Request.parse(message);

                if (request.getType() == Request.Type.GET_ROOM_LIST) {
                    channel.basicPublish("", props.getReplyTo(), replyProps, ResponseSerializer.serializeRoomList(server.getRoomList()).toString().getBytes());
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
                else {
                    Response response;

                    switch (request.getType()) {
                        case LOGIN:
                            LoginRequest loginRequest = (LoginRequest) request;
                            response = server.login(loginRequest.getLogin());
                            break;
                        case CREATE_GAME:
                            CreateGameRequest createGameRequest = (CreateGameRequest) request;
                            response = server.createRoom(createGameRequest.getSettings(), createGameRequest.getLogin());
                            break;
                        case GAME_ACTION:
                            GameActionRequest gameActionRequest = (GameActionRequest) request;
                            response = server.handleGameAction(gameActionRequest.getAction());
                            break;
                        default:
                            throw new NotImplementedException(); //TODO
                    }

                    channel.basicPublish("", props.getReplyTo(), replyProps, ResponseSerializer.serializeResponse(response).toString().getBytes()); //TODO "ok" -> actual response
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            }
            catch (InterruptedException e) {}
            catch (ShutdownSignalException e) {}
            catch (IOException e) {
                System.out.println("Niepodziewany błąd podczas wysyłania do kolejki");
                e.printStackTrace();
            }
        }

        System.out.println("Zakończono odbieranie żądań");
    }
}
