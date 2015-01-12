package to2.dice.server;

import to2.dice.game.GameState;

import java.io.IOException;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by Fan on 2015-01-11.
 */
public class StateQueue extends Queue {
    private SynchronousQueue<MessageServer.StateMessage> stateQueue;

    public StateQueue(SynchronousQueue<MessageServer.StateMessage> stateQueue) throws IOException {
        super();

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
                channel.basicPublish("game_states", gameName, null, "game_state :)".getBytes()); //TODO actual state
            }
            catch (IOException e) {
                System.out.println("Niepowodzenie podczas wysyłania do kolejki");
                e.printStackTrace();
            }
            catch (InterruptedException e) {}
        }
    }
}
