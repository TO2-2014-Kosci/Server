package to2.dice.server;

import com.rabbitmq.client.ConnectionFactory;
import to2.dice.controllers.GameController;
import to2.dice.game.GameState;

import java.io.IOException;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by Fan on 2015-01-09.
 */
public class MessageServer implements Runnable {
    private final SynchronousQueue<StateMessage> stateMessagesQueue;

    private RequestQueue requestQueue;
    private StateQueue stateQueue;
    private Thread requestThread, stateThread;

    public MessageServer(Server server, String queueServer) {
        this.stateMessagesQueue = new SynchronousQueue<StateMessage>();

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(queueServer);
        try {
            this.requestQueue = new RequestQueue(server, queueServer);
            this.stateQueue = new StateQueue(server, queueServer, stateMessagesQueue);
        }
        catch(IOException e) {
            System.out.println("Niepowodzenie podczas tworzenia kolejek");
            e.printStackTrace();
        }

        this.requestThread = new Thread(requestQueue);
        this.stateThread = new Thread(stateQueue);
    }

    @Override
    public void run() {
        this.requestThread.start();
        this.stateThread.start();
    }

    public void close() {
        this.requestThread.interrupt();
        this.stateThread.interrupt();
        this.requestQueue.close();
        this.stateQueue.close();
    }

    public void pushState(GameController controller, GameState state) {
        StateMessage message = new StateMessage();
        message.setGameController(controller);
        message.setGameState(state);
        try {
            stateMessagesQueue.put(message);
        } catch (InterruptedException e) {
            System.out.println("Niepowodzenie podczas wysyłania stanu gry");
            e.printStackTrace();
        }
    }

    public class StateMessage {
        private GameController gameController;
        private GameState gameState;

        public GameController getGameController() {
            return gameController;
        }

        public void setGameController(GameController gameController) {
            this.gameController = gameController;
        }

        public GameState getGameState() {
            return gameState;
        }

        public void setGameState(GameState gameState) {
            this.gameState = gameState;
        }
    }
}
