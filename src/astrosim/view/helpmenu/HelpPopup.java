package astrosim.view.helpmenu;

import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

public class HelpPopup {
    private final Group popup;
    private final StackPane root;

    public HelpPopup(StackPane root, String message) {
        this.root = root;
        VBox popupInternal = new VBox();
        popupInternal.getStyleClass().add("help-popup");
        popupInternal.setUserData("popup");
        popup = new Group(popupInternal);
        String[] lines = message.split("\n");
        for (String line : lines) {
            if (line.startsWith("-")) {
                Label firstLabel = new Label(line.substring(1, line.indexOf(':')));
                Label subLabel = new Label(line.substring(line.indexOf(':')));
                subLabel.getStyleClass().add("normal-label");
                firstLabel.getStyleClass().add("bold-label");
                TextFlow tf = new TextFlow(firstLabel, subLabel);
                tf.getStyleClass().add("text-flow");
                popupInternal.getChildren().add(tf);
            } else {
                Label label = new Label(line);
                label.getStyleClass().add("normal-label");
                popupInternal.getChildren().add(label);
            }
        }
        Button closeButton = new Button();
        SVGPath xSymbolShape = new SVGPath();
        xSymbolShape.setContent("M 5 5 L -5 -5 M -5 5 L 5 -5");
        xSymbolShape.setStyle("-fx-stroke-width: 2.5; -fx-stroke-line-cap: round;");
        closeButton.setGraphic(xSymbolShape);
        closeButton.getStyleClass().add("themed-button");
        closeButton.setOnAction(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(500));
            scale.setFromX(1);
            scale.setToX(0);
            scale.setNode(popup);
            scale.play();
            scale.setOnFinished(ev -> root.getChildren().remove(popup));
        });
        VBox wrapper = new VBox(closeButton);
        wrapper.setAlignment(Pos.CENTER);
        popupInternal.getChildren().add(wrapper);
    }

    public void show() {
        root.getChildren().removeIf(n -> n.getUserData() != null && n.getUserData().equals("popup"));
        root.getChildren().add(popup);
        ScaleTransition scale = new ScaleTransition(Duration.millis(500));
        scale.setFromX(0);
        scale.setToX(1);
        scale.setNode(popup);
        scale.play();
    }
}
