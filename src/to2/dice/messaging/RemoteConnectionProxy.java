package to2.dice.messaging;

import com.rabbitmq.client.*;
import org.json.JSONObject;
import to2.dice.game.GameInfo;
import to2.dice.game.GameSettings;
import to2.dice.game.GameState;
import to2.dice.messaging.AbstractConnectionProxy;
import to2.dice.messaging.GameAction;
import to2.dice.messaging.Response;
import to2.dice.messaging.request.*;
import to2.dice.server.ServerMessageListener;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by Fan on 2015-01-12.
 */
public class RemoteConnectionProxy extends AbstractConnectionProxy {
    private StateReceiver stateReceiver;
    private RequestHandler requestHandler;
    private String currentRoom;

    public RemoteConnectionProxy(Object serverLink, ServerMessageListener listener) throws ConnectException {
        super(serverLink, listener);
    }

    @Override
    public Response login(String login) throws TimeoutException {
        Request request = new LoginRequest(login);
        Response response = getResponse(request);

        if (response.isSuccess())
            loggedInUser = login;

        return response;
    }

    @Override
    public List<GameInfo> getRoomList() throws TimeoutException {
        try {
            Request request = new GetRoomListRequest();
            String response = requestHandler.sendRequest(request.toString());
            return ResponseSerializer.deserializeRoomList(new JSONObject(response));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected boolean connect(Object serverLink) {
        try {
            stateReceiver = new StateReceiver(this, (String)serverLink);
            stateReceiver.start();
            requestHandler = new RequestHandler((String)serverLink);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected Response sendGameAction(GameAction action) {
        Request request = new GameActionRequest(loggedInUser, action);
        String roomName = null;
        if (action.getType() == GameActionType.JOIN_ROOM) {
            roomName = ((JoinRoomAction) action).getGameRoom();
            stateReceiver.bind(roomName);
        }

        Response response = getResponse(request);

        if (action.getType() == GameActionType.JOIN_ROOM)
            if (response.isSuccess())
                this.currentRoom = roomName;
            else
                stateReceiver.unbind(roomName);

        return response;
    }

    @Override
    public Response createRoom(GameSettings settings) throws TimeoutException {
        Request request = new CreateGameRequest(loggedInUser, settings);
        stateReceiver.bind(settings.getName());
        Response response = getResponse(request);

        if (response.isSuccess())
            this.currentRoom = settings.getName();
        else
            stateReceiver.unbind(settings.getName());

        return response;
    }

    private Response getResponse(Request request) {
        try {
            String response = requestHandler.sendRequest(request.toString());
            return ResponseSerializer.deserializeResponse(new JSONObject(response));
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(Response.Type.FAILURE, "Błąd podczas połączenia z kolejką");
        }
    }

    private class StateReceiver extends Thread {
        private final Connection connection;
        private final Channel channel;
        private final String queueName;
        private final QueueingConsumer consumer;
        private RemoteConnectionProxy proxy;

        public StateReceiver(RemoteConnectionProxy proxy, String host) throws IOException {
            this.proxy = proxy;
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(host);
            connection = factory.newConnection();
            channel = connection.createChannel();

            channel.exchangeDeclare("game_states", "direct");
            queueName = channel.queueDeclare().getQueue();

            consumer = new QueueingConsumer(channel);
            channel.basicConsume(queueName, true, consumer);
        }

        public void run() {
            while (!this.isInterrupted()) {
                try {
                    QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                    String message = new String(delivery.getBody());

                    proxy.sendState(ResponseSerializer.deserializeGameState(new JSONObject(message)));
                }
                catch (InterruptedException e) {}
            }
        }

        public boolean bind(String name) {
            try {
                channel.queueBind(queueName, "game_states", name);
                return true;
            } catch (IOException e) {
                System.out.println("Niepowodzenie podczas dostępu do kolejki");
                e.printStackTrace();
                return false;
            }
        }

        public boolean unbind(String name) {
            try {
                channel.queueUnbind(queueName, "game_states", name);
                return true;
            } catch (IOException e) {
                System.out.println("Niepowodzenie podczas dostępu do kolejki");
                e.printStackTrace();
                return false;
            }
        }
    }

    private class RequestHandler {
        private final Connection connection;
        private final Channel channel;
        private final String replyQueueName;
        private final QueueingConsumer consumer;

        private RequestHandler(String host) throws IOException {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(host);
            connection = factory.newConnection();
            channel = connection.createChannel();

            replyQueueName = channel.queueDeclare().getQueue();
            consumer = new QueueingConsumer(channel);
            channel.basicConsume(replyQueueName, true, consumer);
        }

        public String sendRequest(String request) throws IOException, InterruptedException {
            String response;
            String corrId = java.util.UUID.randomUUID().toString();

            AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                    .correlationId(corrId)
                    .replyTo(replyQueueName)
                    .build();

            channel.basicPublish("", "requests", props, request.getBytes());

            while (true) {
                QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                    response = new String(delivery.getBody());
                    break;
                }
            }

            return response;
        }

        public void close() {
            try {
                channel.close();
                connection.close();
            }
            catch (Exception e) {}
        }
    }
}
