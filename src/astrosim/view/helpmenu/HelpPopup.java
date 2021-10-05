package astrosim.view.helpmenu;

import astrosim.view.nodes.Popup;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

public class HelpPopup extends Popup {

    public HelpPopup(StackPane root, String message) {
        super(root);
        VBox popupInternal = new VBox();
        String[] lines = message.split("\n");
        for (String line : lines) {
            if (line.startsWith("-")) {
                Label firstLabel = new Label(line.substring(1, line.indexOf(':')));
                Label subLabel = new Label(line.substring(line.indexOf(':')));
                subLabel.getStyleClass().add("help-normal-label");
                firstLabel.getStyleClass().add("help-bold-label");
                TextFlow tf = new TextFlow(firstLabel, subLabel);
                tf.getStyleClass().add("text-flow");
                popupInternal.getChildren().add(tf);
            } else {
                Label label = new Label(line);
                label.getStyleClass().add("help-normal-label");
                popupInternal.getChildren().add(label);
            }
        }
        super.setContent(popupInternal);
    }
}
