package territory;

import territory.game.player.GUIPlayer;
import territory.game.LocalGame;
import territory.game.player.Player;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import territory.gui.Controller;

public class LocalMain extends Application {

    private double initialWidth = 605, initialHeight = 405;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("layout.fxml"));

        Parent root = loader.load();

        primaryStage.setTitle("Territory");

        //exit on close
        primaryStage.setOnCloseRequest(this::exitApplication);

        Scene scene = new Scene(root, initialWidth, initialHeight);
        primaryStage.setScene(scene);
        primaryStage.show();

        Controller controller = loader.getController();
        controller.init(scene);

        Player guiPlayer = new GUIPlayer(controller);

        LocalGame game = new LocalGame(guiPlayer);

        game.start();
    }

    private void exitApplication(WindowEvent windowEvent) {
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
