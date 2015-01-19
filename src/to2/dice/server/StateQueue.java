package to2.dice.server;

import com.rabbitmq.client.ShutdownSignalException;
import to2.dice.messaging.request.ResponseSerializer;

import java.io.IOException;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by Fan on 2015-01-11.
 */
public class StateQueue extends Queue {
    private SynchronousQueue<MessageServer.StateMessage> stateQueue;

    public StateQueue(Server server, String host, SynchronousQueue<MessageServer.StateMessage> stateQueue) throws IOException {
        super(server, host);

        this.stateQueue = stateQueue;
        channel.exchangeDeclare("game_states", "direct");
    }

    @Override
    public void run() {
        String gameName;

        while(!Thread.interrupted()) {
            try {
                MessageServer.StateMessage state = stateQueue.take();
                gameName = state.getGameController().getGameInfo().getSettings().getName();
                String stateJson = ResponseSerializer.serializeGameState(state.getGameState()).toString();
                channel.basicPublish("game_states", gameName, null, stateJson.getBytes());
            }
            catch (IOException e) {
                System.out.println("Niepowodzenie podczas wysyłania do kolejki");
                e.printStackTrace();
            }
            catch (InterruptedException e) {}
            catch (ShutdownSignalException e) {}
        }
    }
}
