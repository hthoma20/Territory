package territory.game;


import territory.game.action.GameAction;
import territory.game.action.player.PlayerAction;
import territory.game.action.tick.TickAction;
import territory.game.info.GameOverInfo;
import territory.game.info.PlayerSetupInfo;
import territory.game.player.Player;
import territory.util.GlobalConstants;
import territory.util.ObjectUtils;

import java.rmi.ServerError;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is in charge of handling actions that need to be taken,
 * and is in charge of the territory.game loop
 */
public class LocalGame implements Game {

    //territory needed to win the game
    private int territoryNeeded = GlobalConstants.TERRITORY_NEEDED;

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
            this.players[i].sendInfo(new PlayerSetupInfo(i, colors[i]));
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

            //advance the territory game
            for (Tickable tickable : state.getAllTickables()) {
                List<TickAction> actionsToTake = tickable.tick(state);
                if(actionsToTake != null){
                    actionQueue.addAll(actionsToTake);
                }
            }

            //send the players the new state
            this.sendStateToPlayers();

            //check if the game is over
            GameColor winner = getWinner();
            if(winner != null){
                this.sendGameOverInfoToPlayers(winner);
                return;
            }

            //process actions taken
            processActionQueue();

            //sleep for the duration of the tick time ( (tickEnd - tickStart) + sleepTime = tickSpeed )
            long tickEnd = System.currentTimeMillis();
            long sleepTime = tickStart + tickSpeed - tickEnd;

            if(sleepTime < 10){
                System.out.println(String.format("Sleep time: %d", sleepTime));
            }

            if(sleepTime > 0){
                sleep(sleepTime);
            }

            assertInvariants();
        }
    }

    /**
     * Determine if there is a winner in the current state
     * @return the color of the winner, or null if there is no winner
     */
    private GameColor getWinner(){
        for(Player player : this.players){
            TerritoryList playerTerritories = state.getPlayerTerritories(player.getIndex());

            if(playerTerritories.area() >= territoryNeeded){
                return player.getColor();
            }
        }

        return null;
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
        GameState stateCopy = ObjectUtils.transitiveCopy(this.state);
        for(Player player : this.players){
            player.sendState(stateCopy);
        }
    }

    //send info to all players that the game is over
    private void sendGameOverInfoToPlayers(GameColor winningColor){
        GameOverInfo info = new GameOverInfo(winningColor);

        for(Player player : this.players){
            player.sendInfo(info);
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

    public GameState getState() {
        return state;
    }

    public int getTerritoryNeeded(){
        return territoryNeeded;
    }

    public Player getPlayer(GameColor color) {
        Player player = players[color.ordinal()];

        if(player.getColor() != color){
            throw new RuntimeException(
                    String.format("Player in position %d doesn't have color %s.", color.ordinal(), color));
        }

        return player;
    }




    private void assertInvariants(){
        if(!colorsMatchPlayers()){
            System.out.println("Player colors don't line up with indices");
            System.exit(1);
        }
    }

    private boolean colorsMatchPlayers(){
        int index = 0;
        for(GameColor color : GameColor.values()){

            if(index++ >= players.length){
                break;
            }

            if(getPlayer(color).getColor() != color){
                return false;
            }
        }

        return true;
    }
}
