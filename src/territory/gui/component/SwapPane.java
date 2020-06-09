package territory.gui.component;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SwapPane extends VBox {

    public void show(Node child){
        if( !super.getChildren().contains(child)){
            throw new RuntimeException("Calling show on non-child");
        }

        for(Node node : super.getChildren()){
            node.setVisible(false);
        }

        child.setVisible(true);
    }
}
