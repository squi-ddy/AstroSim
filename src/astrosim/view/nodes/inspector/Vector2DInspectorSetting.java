package astrosim.view.nodes.inspector;

import astrosim.model.math.Vec2;
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

public class Vector2DInspectorSetting extends InspectorSetting<Vec2> {
    // Implements InspectorSetting for Vector2Ds.
    private final GridPane toDisplay;
    private final TextField entryFieldX;
    private final TextField entryFieldY;

    public Vector2DInspectorSetting(String name, Vec2 setting, Consumer<Vec2> onUpdate, Function<Vec2, Boolean> isValid) {
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
        this.entryFieldY.setPrefWidth(150);
        this.entryFieldX.setPrefWidth(150);
        this.toDisplay.add(this.entryFieldX, 1, 0);
        this.toDisplay.add(this.entryFieldY, 1, 1);
        Label xLabel = new Label("X");
        Label yLabel = new Label("Y");
        xLabel.getStyleClass().add("inspector-property-label");
        yLabel.getStyleClass().add("inspector-property-label");
        this.toDisplay.add(xLabel, 0, 0);
        this.toDisplay.add(yLabel, 0, 1);
        ColumnConstraints constraints = new ColumnConstraints();
        constraints.setPrefWidth(15);
        constraints.setMinWidth(15);
        constraints.setMaxWidth(15);
        constraints.setHalignment(HPos.RIGHT);
        ColumnConstraints constraints1 = new ColumnConstraints();
        constraints1.setHalignment(HPos.CENTER);
        this.toDisplay.getColumnConstraints().addAll(constraints, constraints1);
        entryFieldX.setPrefWidth(150);
        entryFieldX.setMinWidth(entryFieldX.getPrefWidth());
        entryFieldX.setMinWidth(entryFieldX.getPrefWidth());
        entryFieldY.setPrefWidth(150);
        entryFieldY.setMinWidth(entryFieldY.getPrefWidth());
        entryFieldY.setMinWidth(entryFieldY.getPrefWidth());
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
                super.setNewValue(new Vec2(x, y));
            } catch (NumberFormatException exception) {
                super.setNewValue(null);
            }
            listener.run();
        };
        entryFieldX.setOnKeyTyped(event);
        entryFieldY.setOnKeyTyped(event);
    }
}
