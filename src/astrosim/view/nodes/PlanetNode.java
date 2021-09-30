package astrosim.view.nodes;

import astrosim.model.managers.SimulatorGUIManager;
import astrosim.model.math.Vector2D;
import astrosim.model.simulation.Planet;
import astrosim.view.helpers.ThemeColors;
import astrosim.view.nodes.inspector.*;
import javafx.animation.Animation;
import javafx.animation.StrokeTransition;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
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
        super.getChildren().add(planetBody);
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

    public void updatePlanet(List<Vector2D> newTrail, double length) {
        planetBody.setCenterX(planet.getPosition().getX());
        planetBody.setCenterY(planet.getPosition().getY());
        planetBody.setRadius(planet.getRadius());
        planetBody.setFill(Color.web(planet.getColor()));
        if (!planet.isStatic()) drawLine(newTrail, length);
    }

    private void drawLine(List<Vector2D> newTrail, double trailLength) {
        super.getChildren().removeIf(Line.class::isInstance);
        if (trailLength == 0) return;
        double currTrailLength = 0;
        Vector2D last = newTrail.get(0);
        for (int i = 1; i < newTrail.size(); i++) {
            Vector2D last2 = newTrail.get(i);
            Line line = new Line(last.getX(), last.getY(), last2.getX(), last2.getY());
            line.setStrokeLineCap(StrokeLineCap.BUTT);
            line.setStrokeWidth(planet.getRadius() * 0.5);
            line.setStroke(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.web(planet.getTrailColor()).deriveColor(1, 1, 1, getTransparency(currTrailLength / trailLength))),
                    new Stop(1, Color.web(planet.getTrailColor()).deriveColor(1, 1, 1, getTransparency((currTrailLength + last2.sub(last).magnitude()) / trailLength)))
            ));
            super.getChildren().add(0, line);
            currTrailLength += last2.sub(last).magnitude();
            last = last2;
        }
    }

    private double getTransparency(double interpolate) {
        return interpolate * interpolate;
    }

    public void updatePlanet() {
        updatePlanet(planet.getPath().getTrail(), planet.getPath().getTrailLength());
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
        settings.add(new BooleanInspectorSetting("Static", planet.isStatic(), planet::setStatic));
        return settings;
    }

    @Override
    public void onClose() {
        isSelected = false;
        planetBody.setStroke(Color.TRANSPARENT);
    }
}
