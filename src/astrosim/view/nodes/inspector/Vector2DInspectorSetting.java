package astrosim.view.nodes.inspector;

import astrosim.model.math.Vector2D;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import java.util.function.Consumer;
import java.util.function.Function;

public class Vector2DInspectorSetting extends InspectorSetting<Vector2D> {
    // Implements InspectorSetting for Vector2Ds.
    private final GridPane toDisplay;
    private final TextField entryFieldX;
    private final TextField entryFieldY;

    public Vector2DInspectorSetting(String name, Vector2D setting, Consumer<Vector2D> onUpdate, Function<Vector2D, Boolean> isValid) {
        super(name, setting, onUpdate, isValid);
        this.entryFieldX = new TextField();
        this.entryFieldY = new TextField();
        entryFieldX.setText(String.valueOf(setting.getX()));
        entryFieldY.setText(String.valueOf(setting.getY()));
        this.toDisplay = new GridPane();
        this.toDisplay.setHgap(10);
        this.toDisplay.setVgap(5);
        this.toDisplay.setAlignment(Pos.BASELINE_CENTER);
        this.entryFieldX.getStyleClass().add("themed-text-field");
        this.entryFieldY.getStyleClass().add("themed-text-field");
        this.toDisplay.add(this.entryFieldX, 1, 0);
        this.toDisplay.add(this.entryFieldY, 1, 1);
        Label xLabel = new Label("X");
        Label yLabel = new Label("Y");
        xLabel.getStyleClass().add("inspector-property-label");
        yLabel.getStyleClass().add("inspector-property-label");
        this.toDisplay.add(xLabel, 0, 0);
        this.toDisplay.add(yLabel, 0, 1);
        ColumnConstraints constraints = new ColumnConstraints();
        constraints.setPercentWidth(10);
        constraints.setHalignment(HPos.RIGHT);
        ColumnConstraints constraints1 = new ColumnConstraints();
        constraints1.setHalignment(HPos.CENTER);
        this.toDisplay.getColumnConstraints().addAll(constraints, constraints1);
    }

    @Override
    public Group getToDisplay() {
        return new Group(toDisplay);
    }

    @Override
    public void addChangeListener(Runnable listener) {
        EventHandler<KeyEvent> event = e -> {
            try {
                double x = Double.parseDouble(this.entryFieldX.getText());
                double y = Double.parseDouble(this.entryFieldY.getText());
                super.setNewValue(new Vector2D(x, y));
            } catch (NumberFormatException exception) {
                super.setNewValue(null);
            }
            listener.run();
        };
        entryFieldX.setOnKeyTyped(event);
        entryFieldY.setOnKeyTyped(event);
    }
}
