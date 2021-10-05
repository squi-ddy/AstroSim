package astrosim.view.nodes.inspector;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.util.function.Consumer;

public class ColorInspectorSetting extends InspectorSetting<String> {
    // Implements InspectorSetting for Colors.
    private final Group toDisplay;
    private final Pane preview;
    private final TextField entryField;

    public ColorInspectorSetting(String name, String setting, Consumer<String> onUpdate) {
        super(name, setting, onUpdate, s -> s.matches("#[0-9a-fA-F]{6}"));
        this.entryField = new TextField();
        entryField.setText(setting);
        this.entryField.getStyleClass().add("themed-text-field");
        entryField.setPrefWidth(150);
        entryField.setMinWidth(entryField.getPrefWidth());
        entryField.setMinWidth(entryField.getPrefWidth());
        preview = new Pane();
        preview.setPrefWidth(15);
        preview.setPrefHeight(15);
        preview.setMinWidth(15);
        preview.setMinHeight(15);
        preview.setMaxWidth(15);
        preview.setMaxHeight(15);
        preview.setStyle("-fx-background-color: " + setting);
        HBox wrapper = new HBox(entryField, preview);
        wrapper.setSpacing(10);
        wrapper.setAlignment(Pos.CENTER);
        this.toDisplay = new Group(wrapper);
    }

    @Override
    public Group getToDisplay() {
            return toDisplay;
        }

    @Override
    public void addChangeListener(Runnable listener) {
        entryField.setOnKeyTyped(e -> {
            String color = this.entryField.getText();
            super.setNewValue(color);
            if (isValid()) preview.setStyle("-fx-background-color: " + color);
            listener.run();
        });
    }
}
