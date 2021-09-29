package astrosim.view.nodes.inspector;

import astrosim.model.managers.SimulatorGUIManager;
import astrosim.model.simulation.Planet;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.shape.SVGPath;

public class PlanetInspectorSetting extends InspectorSetting<Planet> {
    private final Group toDisplay;

    public PlanetInspectorSetting(String name, Planet setting) {
        super(name, setting, p -> {}, p -> true);
        Button zoomButton = new Button();
        SVGPath zoomPath = new SVGPath();
        zoomPath.setContent("M -7 0 A 1 1 0 0 0 7 0 A 1 1 0 0 0 -7 0 M 1 0 A 1 1 0 0 0 -1 0 A 1 1 0 0 0 1 0");
        zoomPath.setStyle("-fx-stroke-width: 2.5; -fx-stroke-line-cap: round; -fx-fill: transparent;");
        zoomButton.getStyleClass().add("themed-button");
        zoomButton.setGraphic(zoomPath);
        this.toDisplay = new Group(zoomButton);
        zoomButton.setOnAction(e -> SimulatorGUIManager.getController().moveTo(setting));
    }

    @Override
    public Group getToDisplay() {
        return toDisplay;
    }

    @Override
    public void addChangeListener(Runnable listener) {
        // no listener required
    }
}
