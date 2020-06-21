package territory;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import territory.game.LocalGame;
import territory.game.player.GUIPlayer;
import territory.gui.Controller;
import territory.joiner.JoinController;

import java.net.URL;

public class LocalMain extends Application {

    private double initialWidth = 1226, initialHeight = 657;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        URL gameLayout = getClass().getResource("gui/layout.fxml");
        URL joinerLayout = getClass().getResource("joiner/joiner.fxml");

        FXMLLoader mainLayoutLoader = new FXMLLoader(gameLayout);
        Scene gameScene = new Scene(mainLayoutLoader.load(), initialWidth, initialHeight);

        primaryStage.setTitle("Territory");

        //exit on close
        primaryStage.setOnCloseRequest(this::exitApplication);

        primaryStage.setScene(gameScene);
        primaryStage.show();

        Controller gameController = mainLayoutLoader.getController();
        gameController.init(gameScene);

        GUIPlayer guiPlayer = new GUIPlayer(gameController);

        LocalGame game = new LocalGame(guiPlayer);
        game.start();
    }

    private void exitApplication(WindowEvent windowEvent) {
        Platform.exit();
        System.exit(0);
    }
}