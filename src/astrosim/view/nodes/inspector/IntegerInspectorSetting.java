package astrosim.view.nodes.inspector;

import javafx.scene.Group;
import javafx.scene.control.TextField;

import java.util.function.Consumer;
import java.util.function.Function;

public class IntegerInspectorSetting extends InspectorSetting<Integer> {
    // Implements InspectorSetting for Integers..
    private final Group toDisplay;
    private final TextField entryField;

    public IntegerInspectorSetting(String name, Integer setting, Consumer<Integer> onUpdate, Function<Integer, Boolean> isValid) {
        super(name, setting, onUpdate, isValid);
        this.entryField = new TextField();
        entryField.setText(setting.toString());
        this.toDisplay = new Group(entryField);
        this.entryField.getStyleClass().add("themed-text-field");
    }

    @Override
    public Group getToDisplay() {
        return toDisplay;
    }

    @Override
    public void addChangeListener(Runnable listener) {
        entryField.setOnKeyTyped(e -> {
            try {
                super.setNewValue(Integer.parseInt(this.entryField.getText()));
            } catch (NumberFormatException exception) {
                super.setNewValue(null);
            }
            listener.run();
        });
    }
}
