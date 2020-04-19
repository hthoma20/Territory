package gui;

import game.player.GUIPlayer;
import game.LocalGame;
import game.player.Player;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

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

        Player guiPlayer = new GUIPlayer(controller);

        LocalGame game = new LocalGame(guiPlayer);

        game.start();

        Point2D ref = new Point2D(1, 0);

        System.out.println(ref.angle(1,1));
        System.out.println(ref.angle(0,1));
        System.out.println(ref.angle(-1,1));
        System.out.println(ref.angle(-1,0));
        System.out.println(ref.angle(-1,-1));
        System.out.println(ref.angle(0,-1));
        System.out.println(ref.angle(1,-1));
        System.out.println(ref.angle(1,0));
    }

    private void exitApplication(WindowEvent windowEvent) {
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
