package astrosim.view.nodes.inspector;

import astrosim.model.managers.SimulatorGUIManager;
import astrosim.view.nodes.PlanetNode;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;

public class PlanetInspectorSetting extends InspectorSetting<PlanetNode> {
    private final Group toDisplay;

    public PlanetInspectorSetting(String name, PlanetNode setting) {
        super(name, setting, p -> {}, p -> true);
        Button zoomButton = new Button();
        SVGPath zoomPath = new SVGPath();
        zoomPath.setContent("M -7 0 A 1 1 0 0 0 7 0 A 1 1 0 0 0 -7 0 M 1 0 A 1 1 0 0 0 -1 0 A 1 1 0 0 0 1 0");
        zoomPath.setStyle("-fx-stroke-width: 2.5; -fx-stroke-line-cap: round; -fx-fill: transparent;");
        zoomButton.getStyleClass().add("themed-button");
        zoomButton.setGraphic(zoomPath);
        zoomButton.setOnAction(e -> SimulatorGUIManager.getController().moveTo(setting.getPlanet()));
        Button delPlanet = new Button();
        SVGPath xSymbolShape = new SVGPath();
        HBox wrapper = new HBox(zoomButton, delPlanet);
        wrapper.setSpacing(5);
        this.toDisplay = new Group(wrapper);
        xSymbolShape.setContent("M 5 5 L -5 -5 M -5 5 L 5 -5");
        xSymbolShape.setStyle("-fx-stroke-width: 2.5; -fx-stroke-line-cap: round;");
        delPlanet.setGraphic(xSymbolShape);
        delPlanet.getStyleClass().add("themed-button");
        delPlanet.setOnAction(e -> {
            SimulatorGUIManager.getController().deletePlanet(setting);
            SimulatorGUIManager.getOpenInspector().removeSetting(this);
        });
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
