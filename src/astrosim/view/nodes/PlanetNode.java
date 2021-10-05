package astrosim.view.nodes;

import astrosim.model.managers.SimulatorGUIManager;
import astrosim.model.simulation.Planet;
import astrosim.view.helpers.ThemeColors;
import astrosim.view.nodes.inspector.*;
import javafx.animation.Animation;
import javafx.animation.StrokeTransition;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("java:S110")
public class PlanetNode extends Group implements Inspectable {
    private final Planet planet;
    private final Circle planetBody;
    private final StrokeTransition onHover;
    private boolean isSelected;
    private static final SimpleDoubleProperty selectedWidth = new SimpleDoubleProperty(2.5);

    public PlanetNode(Planet planet) {
        super();
        this.planet = planet;
        this.onHover = new StrokeTransition(Duration.millis(500));
        this.isSelected = false;
        Color themeColor = ThemeColors.getThemeColor("-theme-contrast-color-1");
        if (themeColor == null) throw new IllegalStateException();
        planetBody = new Circle();
        planetBody.setStroke(Color.TRANSPARENT);
        planetBody.strokeWidthProperty().bind(selectedWidth.divide(SimulatorGUIManager.scaleProperty()));
        super.getChildren().addAll(planet.getPath().getTrail(), planetBody);
        updatePlanet();
        onHover.setFromValue(themeColor);
        onHover.setToValue(themeColor.deriveColor(1, 1, 1, 0.3));
        onHover.setCycleCount(Animation.INDEFINITE);
        onHover.setAutoReverse(true);
        onHover.setShape(planetBody);
        planetBody.setOnMouseEntered(e -> {
            if (isSelected) return;
            onHover.play();
        });
        planetBody.setOnMouseExited(e -> {
            if (isSelected) return;
            onHover.pause();
            onHover.jumpTo(Duration.ZERO);
            planetBody.setStroke(Color.TRANSPARENT);
        });
        planetBody.setOnMouseClicked(e -> {
            planetBody.setStroke(themeColor);
            onHover.pause();
            onHover.jumpTo(Duration.ZERO);
            doOnSelect();
        });
        planet.setUpdateListener(this::updatePlanet);
    }

    public Planet getPlanet() {
        return planet;
    }

    public void updatePlanet() {
        planetBody.setCenterX(planet.getPosition().getX());
        planetBody.setCenterY(planet.getPosition().getY());
        planetBody.setRadius(planet.getRadius());
        planetBody.setFill(Color.web(planet.getColor()));
    }

    private void doOnSelect() {
        if (!isSelected) {
            SimulatorGUIManager.getInspector("Properties").loadSettings(this);
            isSelected = true;
        }
        else {
            SimulatorGUIManager.getInspector().hidePane();
            onHover.play();
        }
    }

    @Override
    public List<InspectorSetting<?>> getSettings() {
        List<InspectorSetting<?>> settings = new ArrayList<>();
        settings.add(new StringInspectorSetting("Name", planet.getName(), planet::setName, s -> true));
        settings.add(new ColorInspectorSetting("Fill", planet.getColor(), planet::setColor));
        settings.add(new ColorInspectorSetting("Trail", planet.getTrailColor(), planet::setTrailColor));
        settings.add(new DoubleInspectorSetting("Mass", planet.getMass(), planet::setMass, s -> s != null && s >= 0));
        settings.add(new DoubleInspectorSetting("Radius", planet.getRadius(), planet::setRadius, s -> s != null && s > 0));
        settings.add(new Vector2DInspectorSetting("Position", planet.getPosition(), v -> planet.setPosition(v, planet.getVelocity()), Objects::nonNull));
        settings.add(new Vector2DInspectorSetting("Velocity", planet.getVelocity(), v -> planet.setPosition(planet.getPosition(), v), Objects::nonNull));
        settings.add(new BooleanInspectorSetting("Static", planet.isStatic(), planet::setStatic));
        settings.add(new BooleanInspectorSetting("Show Trail", planet.getPath().getTrail().isShowing(), planet.getPath().getTrail()::setShowing));
        return settings;
    }

    @Override
    public void onClose() {
        isSelected = false;
        planetBody.setStroke(Color.TRANSPARENT);
    }
}
