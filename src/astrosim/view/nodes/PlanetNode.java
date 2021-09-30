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
import javafx.scene.shape.Polyline;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("java:S110")
public class PlanetNode extends Group implements Inspectable {
    private final Planet planet;
    private final Polyline trail;
    private final Circle planetBody;
    private final StrokeTransition onHover;
    private boolean isSelected;

    public PlanetNode(Planet planet) {
        super();
        this.planet = planet;
        this.onHover = new StrokeTransition(Duration.millis(500));
        this.isSelected = false;
        trail = new Polyline();
        Color themeColor = ThemeColors.getThemeColor("-theme-contrast-color-1");
        if (themeColor == null) throw new IllegalStateException();
        trail.strokeWidthProperty().bind(new SimpleDoubleProperty(2.5).divide(SimulatorGUIManager.scaleProperty()));
        planetBody = new Circle();
        planetBody.setStroke(Color.TRANSPARENT);
        planetBody.strokeWidthProperty().bind(new SimpleDoubleProperty(2.5).divide(SimulatorGUIManager.scaleProperty()));
        super.getChildren().addAll(trail, planetBody);
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

    public void updatePlanet(List<Double> newTrail) {
        planetBody.setCenterX(planet.getPosition().getX());
        planetBody.setCenterY(planet.getPosition().getY());
        planetBody.setRadius(planet.getRadius());
        planetBody.setFill(Color.web(planet.getColor()));
        trail.setStroke(Color.web(planet.getTrailColor()));
        trail.getPoints().setAll(newTrail);
    }

    public void updatePlanet() {
        updatePlanet(planet.getPath().getTrail());
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
        settings.add(new StringInspectorSetting("Fill", planet.getColor(), planet::setColor, s -> s.matches("#[0-9a-fA-F]{6}")));
        settings.add(new StringInspectorSetting("Trail", planet.getTrailColor(), planet::setTrailColor, s -> s.matches("#[0-9a-fA-F]{6}")));
        settings.add(new DoubleInspectorSetting("Mass", planet.getMass(), planet::setMass, s -> s != null && s >= 0));
        settings.add(new DoubleInspectorSetting("Radius", planet.getRadius(), planet::setRadius, s -> s != null && s > 0));
        settings.add(new Vector2DInspectorSetting("Position", planet.getPosition(), v -> planet.setPosition(v, planet.getVelocity()), Objects::nonNull));
        settings.add(new Vector2DInspectorSetting("Velocity", planet.getVelocity(), v -> planet.setPosition(planet.getPosition(), v), Objects::nonNull));
        return settings;
    }

    @Override
    public void onClose() {
        isSelected = false;
        planetBody.setStroke(Color.TRANSPARENT);
    }
}
