package territory;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import territory.game.player.GUIPlayer;
import territory.gui.Controller;
import territory.joiner.JoinController;

import java.net.URL;

public class ClientMain extends Application {

    public static final int PORT = 4000;
    public static final String DEFAULT_HOST = "localhost";

    private double initialWidth = 605, initialHeight = 405;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        String host = getParameters().getNamed().getOrDefault("host", DEFAULT_HOST);
        System.out.format("Connecting to %s on port %d\n", host, PORT);

        URL gameLayout = getClass().getResource("gui/layout.fxml");
        URL joinerLayout = getClass().getResource("joiner/joiner.fxml");

        FXMLLoader mainLayoutLoader = new FXMLLoader(gameLayout);
        Scene gameScene = new Scene(mainLayoutLoader.load(), initialWidth, initialHeight);

        FXMLLoader joinerLoader = new FXMLLoader(joinerLayout);
        Scene joinerScene = new Scene(joinerLoader.load(), initialWidth, initialHeight);

        primaryStage.setTitle("Territory");

        //exit on close
        primaryStage.setOnCloseRequest(this::exitApplication);

        primaryStage.setScene(joinerScene);
        primaryStage.show();

        Controller gameController = mainLayoutLoader.getController();
        gameController.init(gameScene);

        GUIPlayer guiPlayer = new GUIPlayer(gameController);

        JoinController joinController = joinerLoader.getController();

        joinController.init(host, PORT, guiPlayer);

        joinController.onGameStart( game -> {
            System.out.println("Starting game");
            Platform.runLater(() -> {
                primaryStage.setScene(gameScene);
                primaryStage.setMaximized(true);
            });
        });

        joinController.start();
    }

    private void exitApplication(WindowEvent windowEvent) {
        Platform.exit();
        System.exit(0);
    }
}
