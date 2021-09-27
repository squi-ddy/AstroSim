package astrosim.view.nodes;

import astrosim.model.simulation.Planet;
import astrosim.view.helpers.ThemeColors;
import javafx.animation.Animation;
import javafx.animation.StrokeTransition;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.util.Duration;

public class PlanetNode extends Group {
    private final Planet planet;
    private final Polyline trail;
    private final Circle planetBody;
    private final double scale;
    private final StrokeTransition onHover;
    private final boolean isSelected;

    public PlanetNode(Planet planet, double scale) {
        super();
        this.planet = planet;
        this.scale = scale;
        this.onHover = new StrokeTransition(Duration.millis(500));
        this.isSelected = false;
        trail = new Polyline();
        Color themeColor = ThemeColors.getThemeColor("-theme-contrast-color-1");
        if (themeColor == null) throw new IllegalStateException();
        trail.setStroke(themeColor);
        planetBody = new Circle();
        planetBody.setStroke(themeColor);
        super.getChildren().addAll(planetBody, trail);
        updatePlanet();
        updateTrail();
        onHover.setToValue(themeColor.deriveColor(1, 1, 1, 0.5));
        onHover.setCycleCount(Animation.INDEFINITE);
        onHover.setAutoReverse(true);
        onHover.setShape(planetBody);
        planetBody.setOnMouseEntered(e -> {
            onHover.play();
        });
        planetBody.setOnMouseExited(e -> {
            onHover.pause();
            onHover.jumpTo(Duration.ZERO);
        });
        planetBody.setOnMouseClicked(e -> {
            doOnSelect();
            e.consume();
        });
    }

    public Planet getPlanet() {
        return planet;
    }

    public void updateTrail() {
        trail.getPoints().setAll(planet.getPath().getTrail().stream().map(e -> e / scale).toList());
    }

    public void updatePlanet() {
        planetBody.setCenterX(planet.getPosition().getX() / scale);
        planetBody.setCenterY(planet.getPosition().getY() / scale);
        planetBody.setRadius(planet.getRadius() / scale);
        planetBody.setFill(Color.web(planet.getColor()));
    }

    private void doOnSelect() {
        if (!isSelected) {
            planetBody.getStyleClass().add("planet-selected");
            // todo: open inspector
        }
        else {
            planetBody.getStyleClass().removeIf(e -> e.equals("planet-selected"));
        }
    }
}
