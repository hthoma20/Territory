package territory.joiner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import territory.game.player.GUIPlayer;
import territory.gui.Controller;

public class ClientMain extends Application {

    public static final int PORT = 4000;
    public static final String HOST = "localhost";

    private double initialWidth = 605, initialHeight = 405;

    public static void main(String[] args){

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        //give a second for the server to start
        try{
            Thread.sleep(5000);
        }
        catch(InterruptedException ignored){}

        FXMLLoader mainLayoutLoader = new FXMLLoader(getClass().getResource("../gui/layout.fxml"));
        Scene gameScene = new Scene(mainLayoutLoader.load(), initialWidth, initialHeight);

        FXMLLoader joinerLoader = new FXMLLoader(getClass().getResource("./joiner.fxml"));
        Scene joinerScene = new Scene(joinerLoader.load(), initialWidth, initialHeight);

        primaryStage.setTitle("Territory");

        //exit on close
        primaryStage.setOnCloseRequest(this::exitApplication);

        primaryStage.setScene(joinerScene);
        primaryStage.show();

        Controller gameController = mainLayoutLoader.getController();
        gameController.init();

        GUIPlayer guiPlayer = new GUIPlayer(gameController);

        JoinController joinController = joinerLoader.getController();

        joinController.init(HOST, PORT, guiPlayer);

        joinController.onGameStart( game -> {
            System.out.println("Starting game");
            Platform.runLater(() -> primaryStage.setScene(gameScene));
        });

        joinController.start();
    }

    private void exitApplication(WindowEvent windowEvent) {
        Platform.exit();
        System.exit(0);
    }
}
