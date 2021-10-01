package astrosim.view.nodes.inspector;

import javafx.scene.Group;
import javafx.scene.control.ChoiceBox;

import java.util.List;
import java.util.function.Consumer;

public class ListInspectorSetting extends InspectorSetting<Integer> {
    // Implements InspectorSetting for a list of strings.
    private final Group toDisplay;
    private final ChoiceBox<String> choiceBox;

    public ListInspectorSetting(String name, Integer setting, Consumer<Integer> onUpdate, List<String> options) {
        super(name, setting, onUpdate, v -> true);
        this.choiceBox = new ChoiceBox<>();
        choiceBox.getItems().addAll(options);
        choiceBox.getSelectionModel().select(setting);
        this.toDisplay = new Group(choiceBox);
        this.choiceBox.getStyleClass().add("themed-choice-box");
        this.choiceBox.setPrefWidth(175);
    }

    @Override
    public Group getToDisplay() {
        return toDisplay;
    }

    @Override
    public void addChangeListener(Runnable listener) {
        choiceBox.setOnAction(e -> {
            super.setNewValue(choiceBox.getSelectionModel().getSelectedIndex());
            listener.run();
        });
    }
}
