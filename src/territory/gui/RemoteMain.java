package territory.gui;

import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import territory.game.GameNotStartedException;
import territory.game.ServerMain;
import territory.game.RemoteGame;
import territory.game.player.GUIPlayer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class RemoteMain extends Application {

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

        RemoteGame game = new RemoteGame(guiPlayer, ServerMain.HOST, ServerMain.PORT);

        try {
            //give the server time
            Thread.sleep(1000);
            game.start();
        }
        catch(GameNotStartedException exc){
            primaryStage.setScene(errorScene(exc));
        }
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
