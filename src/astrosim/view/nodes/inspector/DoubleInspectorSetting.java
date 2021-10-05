package astrosim.view.nodes.inspector;

import javafx.scene.Group;
import javafx.scene.control.TextField;

import java.util.function.Consumer;
import java.util.function.Function;

public class DoubleInspectorSetting extends InspectorSetting<Double> {
    // Implements InspectorSetting for Doubles.
    private final Group toDisplay;
    private final TextField entryField;

    public DoubleInspectorSetting(String name, Double setting, Consumer<Double> onUpdate, Function<Double, Boolean> isValid) {
        super(name, setting, onUpdate, isValid);
        this.entryField = new TextField();
        entryField.setText(setting.toString());
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
            try {
                super.setNewValue(Double.parseDouble(this.entryField.getText()));
            } catch (NumberFormatException exception) {
                super.setNewValue(null);
            }
            listener.run();
        });
    }
}
