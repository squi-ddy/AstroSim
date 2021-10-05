package astrosim.view.nodes.inspector;

import javafx.scene.Group;
import javafx.scene.control.TextField;

import java.util.function.Consumer;
import java.util.function.Function;

public class StringInspectorSetting extends InspectorSetting<String> {
    // Implements InspectorSetting for Strings.
    private final Group toDisplay;
    private final TextField entryField;

    public StringInspectorSetting(String name, String setting, Consumer<String> onUpdate, Function<String, Boolean> isValid) {
        super(name, setting, onUpdate, isValid);
        this.entryField = new TextField();
        entryField.setText(setting);
        this.toDisplay = new Group(entryField);
        this.entryField.getStyleClass().add("themed-text-field");
        entryField.setPrefWidth(175);
        entryField.setMinWidth(entryField.getPrefWidth());
        entryField.setMinWidth(entryField.getPrefWidth());
    }

    @Override
    public Group getToDisplay() {
        return toDisplay;
    }

    @Override
    public void addChangeListener(Runnable listener) {
        entryField.setOnKeyTyped(e -> {
            super.setNewValue(this.entryField.getText());
            listener.run();
        });
    }
}
