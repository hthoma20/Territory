package territory.game;


import territory.game.action.GameAction;
import territory.game.action.PlayerAction;
import territory.game.action.TickAction;
import territory.game.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is in charge of handling actions that need to be taken,
 * and is in charge of the territory.game loop
 */
public class LocalGame implements Game {
    private static int nextId = 0;

    private int tickSpeed = 1000/40; // millis/fps

    private boolean gameLoopRunning = false;

    private GameState state;
    private Player[] players;

    private ActionProcessor actionProcessor;

    private final List<GameAction> actionQueue = new ArrayList<>();

    public LocalGame(Player... players){

        initPlayers(players);

        this.state = new GameState(players.length);
        this.actionProcessor = new ActionProcessor(this);
    }

    /**
     * Initialize the territory.game with the given players
     * @param players the array of players in this territory.game
     */
    private void initPlayers(Player[] players){
        if(players.length > GameColor.values().length){
            throw new RuntimeException("Too many players.");
        }

        this.players = players;

        GameColor[] colors = GameColor.values();
        for(int i = 0; i < players.length; i++){
            this.players[i].setGame(this);
            this.players[i].setIndex(i);
            this.players[i].setColor(colors[i]);
        }
    }

    /**
     * Starts the territory.game loop
     */
    public void start(){
        if(gameLoopRunning){
            throw new RuntimeException("Game loop already running");
        }

        gameLoopRunning = true;
        new Thread(this::runGameLoop).start();
    }

    /**
     * Stops the territory.game loop
     */
    public void stop(){
        gameLoopRunning = false;
    }

    private void runGameLoop(){
        while(gameLoopRunning) {
            long tickStart = System.currentTimeMillis();

            //advance the territory.game
            for (Tickable tickable : state.getAllTickables()) {
                List<TickAction> actionsToTake = tickable.tick();
                if(actionsToTake != null){
                    actionQueue.addAll(actionsToTake);
                }
            }

            //send the players the new state
            this.sendStateToPlayers();

            //process actions taken
            processActionQueue();

            //sleep for the duration of the tick time
            long tickEnd = System.currentTimeMillis();
            long sleepTime = tickStart + tickSpeed - tickEnd;

            if(sleepTime > 0){
                sleep(sleepTime);
            }
        }
    }

    private void processActionQueue(){
        List<GameAction> actionsToProcess = null;

        synchronized(actionQueue){
            actionsToProcess = new ArrayList<>(actionQueue);
            actionQueue.clear();
        }

        for(GameAction action : actionsToProcess){
            actionProcessor.processAction(action);
        }
    }

    //send the state to all players
    private void sendStateToPlayers() {
        for(Player player : this.players){
            player.sendState(this.state.copy());
        }
    }

    @Override
    public void receiveAction(PlayerAction action){
        actionQueue.add(action);
    }

    private void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static int getUniqueId(){
        int id = LocalGame.nextId;
        LocalGame.nextId++;

        return id;
    }

    public GameState getState() {
        return state;
    }
}
