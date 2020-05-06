package territory.gui;

import javafx.geometry.Point2D;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import territory.game.GameNotStartedException;
import territory.game.LocalGame;
import territory.game.ServerMain;
import territory.game.RemoteGame;
import territory.game.player.ComputerPlayer;
import territory.game.player.GUIPlayer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import territory.game.player.RemotePlayer;
import territory.game.sprite.Sprite;

public class RemoteMain extends Application {

    public static final String HOST = "localhost";
    public static final int PORT = 1014;

    private double initialWidth = 605, initialHeight = 405;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("layout.fxml"));

        Parent root = loader.load();

        primaryStage.setTitle("Territory");

        //exit on close
        primaryStage.setOnCloseRequest(this::exitApplication);

        primaryStage.setScene(new Scene(root, initialWidth, initialHeight));
        primaryStage.show();

        Controller controller = loader.getController();
        controller.init();

        GUIPlayer guiPlayer = new GUIPlayer(controller);

        //start the server
        new Thread(this::startServer).start();

        //give the server time to start
        Thread.sleep(500);

        //start the client
        try {
            startClient(guiPlayer);
        }
        catch(GameNotStartedException exc){
            primaryStage.setScene(errorScene(exc));
        }

        Point2D A = new Point2D(0,-1);
        Point2D B = new Point2D(1,0);
        Point2D C = new Point2D(0,1);
        Point2D D = new Point2D(-1,0);

        System.out.println("A to B: " + Sprite.rotation(A.subtract(B)));
        System.out.println("B to A: " + Sprite.rotation(B.subtract(A)));
        System.out.println("B to C: " + Sprite.rotation(B.subtract(C)));
        System.out.println("A to C: " + Sprite.rotation(A.subtract(C)));
    }

    private void  startServer(){
        RemotePlayer player = new RemotePlayer(PORT);

        System.out.println("Connecting...");
        player.connect();
        System.out.println("Connected.");

        ComputerPlayer player2 = new ComputerPlayer();

        LocalGame game = new LocalGame(player);

        game.start();
    }

    private void startClient(GUIPlayer guiPlayer){
        RemoteGame game = new RemoteGame(guiPlayer, HOST, PORT);
        game.start();
    }

    private Scene errorScene(Exception error){
        return new Scene(new TextFlow(new Text(error.toString())));
    }

    private void exitApplication(WindowEvent windowEvent) {
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
