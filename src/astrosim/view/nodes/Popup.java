package astrosim.view.nodes;

import javafx.animation.ScaleTransition;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class Popup {
    private final VBox popupBox;
    private final Group popupGroup;
    private final StackPane root;

    public Popup(StackPane root) {
        this.root = root;
        popupBox = new VBox();
        popupBox.getStyleClass().add("popup");
        popupBox.getChildren().add(new Group());
        popupGroup = new Group(popupBox);
    }

    public void setContent(Node node) {
        popupBox.getChildren().set(0, node);
    }

    public void show() {
        root.getChildren().add(popupGroup);
        root.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> hide());
        ScaleTransition scale = new ScaleTransition(Duration.millis(500));
        scale.setFromX(0);
        scale.setToX(1);
        scale.setNode(popupBox);
        scale.play();
    }

    private void hide() {
        ScaleTransition scale = new ScaleTransition(Duration.millis(500));
        scale.setFromX(1);
        scale.setToX(0);
        scale.setNode(popupBox);
        scale.play();
        scale.setOnFinished(e -> root.getChildren().remove(popupGroup));
    }
}
