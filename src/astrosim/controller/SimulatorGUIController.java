package astrosim.controller;

import astrosim.model.managers.ScenarioManager;
import astrosim.model.managers.Settings;
import astrosim.model.math.Functions;
import astrosim.model.simulation.Planet;
import astrosim.model.simulation.Scenario;
import astrosim.view.helpers.MenuItem;
import astrosim.view.helpers.MenuRenderer;
import astrosim.view.nodes.PlanetNode;
import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SimulatorGUIController implements Initializable {
    @FXML
    private Label zoomLabel;
    @FXML
    private Pane simulationInnerPane;
    @FXML
    private Pane simulationPane;
    @FXML
    private Label xLabel;
    @FXML
    private Label yLabel;
    @FXML
    private BorderPane simulatorRoot;
    @FXML
    private StackPane root;
    @FXML
    private Label objectMenuItem;
    @FXML
    private Label fileMenuItem;
    @FXML
    private ToggleButton pauseButton;
    @FXML
    private ToggleButton speed1Button;
    @FXML
    private ToggleButton speed2Button;
    @FXML
    private ToggleButton speed3Button;

    private ToggleButton[] speedButtons;
    private Double lastX = null;
    private Double lastY = null;
    private Scale scale = new Scale();
    private double lastScale = 1;
    private double currentScale = 1;
    private static final double SCALE_CONSTANT = 5e4;
    private final List<PlanetNode> planetNodes = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        speedButtons = new ToggleButton[]{pauseButton, speed1Button, speed2Button, speed3Button};
        simulatorRoot.prefWidthProperty().bind(root.widthProperty());
        simulatorRoot.prefHeightProperty().bind(root.heightProperty());
        syncSpeedButtons();
        List<MenuItem> fileMenu = new ArrayList<>();
        List<MenuItem> objectMenu = new ArrayList<>();
        fileMenu.add(new MenuItem("Save", () -> doSave(false)));
        fileMenu.add(new MenuItem("Save As", () -> doSave(true)));
        fileMenu.add(new MenuItem("Delete", this::doDelete));
        objectMenu.add(new MenuItem("Create new Planet", this::newPlanet));
        objectMenu.add(MenuItem.SPACING);
        objectMenu.add(new MenuItem("Planet Inspector", this::openInspector));
        MenuRenderer.renderMenu(fileMenu, fileMenuItem, root);
        MenuRenderer.renderMenu(objectMenu, objectMenuItem, root);
        setUpSimulator();
    }

    private void doSave(boolean saveAs) {

    }

    private void doDelete() {

    }

    private void newPlanet() {

    }

    private void openInspector() {

    }

    private void syncSpeedButtons() {
        for (int i = 0; i < speedButtons.length; i++) {
            speedButtons[i].setSelected(Settings.getSpeed() == i);
        }
    }

    private void setUpSimulator() {
        simulationInnerPane.getTransforms().add(new Scale());
        simulationPane.setOnMousePressed(e -> simulationPane.getScene().setCursor(Cursor.MOVE));
        simulationPane.setOnMouseDragged(e -> {
            if (lastX != null) {
                simulationInnerPane.setLayoutX(simulationInnerPane.getLayoutX() + e.getX() - lastX);
                simulationInnerPane.setLayoutY(simulationInnerPane.getLayoutY() + e.getY() - lastY);
            }
            lastX = e.getX();
            lastY = e.getY();
        });
        simulationPane.setOnMouseReleased(e -> {
            lastX = null;
            lastY = null;
            simulationPane.getScene().setCursor(Cursor.DEFAULT);
        });
        simulationPane.setOnMouseMoved(e -> {
            if (simulationInnerPane.getTransforms().size() > 1) combineTransforms();
            try {
                Point2D trueMouseCoordinates = simulationInnerPane.getTransforms().get(0).inverseTransform(e.getX() - simulationInnerPane.getLayoutX(), e.getY() - simulationInnerPane.getLayoutY());
                xLabel.setText(String.valueOf((int) (trueMouseCoordinates.getX())));
                yLabel.setText(String.valueOf((int) (trueMouseCoordinates.getY())));
            } catch (NonInvertibleTransformException ex) {
                ex.printStackTrace();
            }
        });
        simulationPane.setOnScrollStarted(e -> simulationInnerPane.getTransforms().add(scale));
        simulationPane.setOnScroll(e -> {
            if (!simulationInnerPane.getTransforms().contains(scale)) simulationInnerPane.getTransforms().add(scale);
            double direction = e.getDeltaX();
            if (direction == 0) direction = e.getDeltaY();
            Point2D trueMouseCoordinates = new Point2D(e.getX() - simulationInnerPane.getLayoutX(), e.getY() - simulationInnerPane.getLayoutY());
            for (Transform t : simulationInnerPane.getTransforms()) {
                try {
                    trueMouseCoordinates = t.inverseTransform(trueMouseCoordinates);
                } catch (NonInvertibleTransformException ex) {
                    ex.printStackTrace();
                }
            }
            scaleScenario(direction * Settings.getSensitivity() / 100000, trueMouseCoordinates.getX(), trueMouseCoordinates.getY());
        });
        simulationPane.setOnScrollFinished(e -> combineTransforms());
        doSimulatorClip();
        // add some speckles (stars!)
        addSpeckles();
        renderScenario();
    }

    private void scaleScenario(double deltaPercent, double scaleFromX, double scaleFromY) {
        double scaleBy = (lastScale - deltaPercent) / currentScale;
        lastScale = Math.max(0.05, currentScale * scaleBy);
        zoomLabel.setText(String.valueOf(Math.round(lastScale * 100)));
        scale.setPivotX(scaleFromX);
        scale.setPivotY(scaleFromY);
        scale.setX(scaleBy);
        scale.setY(scaleBy);
    }

    private void renderScenario() {
        Scenario scenario = ScenarioManager.getScenario();
        scenario.getPlanets().forEach(this::placePlanet);
    }

    private void placePlanet(Planet planet) {
        PlanetNode node = new PlanetNode(planet, SCALE_CONSTANT);
        planetNodes.add(node);
        simulationInnerPane.getChildren().add(node);
    }

    private void doSimulatorClip() {
        final Rectangle clipPane = new Rectangle();
        simulationPane.setClip(clipPane);

        simulationPane.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
            clipPane.setWidth(newValue.getWidth());
            clipPane.setHeight(newValue.getHeight());
        });
    }

    private void addSpeckles() {
        Group speckleGroup = new Group();
        speckleGroup.setManaged(false);
        for (int i = 0; i < 100; i++) {
            Rectangle speckle = new Rectangle(2, 2);
            speckle.setFill(Color.WHITE);
            double xPos = Math.random();
            double yPos = Math.random();
            ChangeListener<Number> listenerX = (observable, oldValue, newValue) -> speckle.setX(Functions.modulo(simulationPane.getWidth() * xPos + simulationInnerPane.getLayoutX(), simulationPane.getWidth()));
            ChangeListener<Number> listenerY = (observable, oldValue, newValue) -> speckle.setY(Functions.modulo(simulationPane.getHeight() * yPos + simulationInnerPane.getLayoutY(), simulationPane.getHeight()));
            simulationInnerPane.layoutXProperty().addListener(listenerX);
            simulationInnerPane.layoutYProperty().addListener(listenerY);
            simulationPane.widthProperty().addListener(listenerX);
            simulationPane.heightProperty().addListener(listenerY);
            FillTransition transition = new FillTransition(Duration.seconds(5 * Math.random() + 5));
            transition.setFromValue(Color.WHITE);
            transition.setToValue(Color.rgb(50, 50, 50));
            transition.setShape(speckle);
            transition.setAutoReverse(true);
            transition.setCycleCount(Animation.INDEFINITE);
            transition.playFrom(Duration.seconds(5 * Math.random()));
            speckleGroup.getChildren().add(speckle);
        }
        simulationPane.getChildren().add(0, speckleGroup);
    }

    private void syncTransforms() {

    }

    private void combineTransforms() {
        Transform transform = null;
        for (Transform t : simulationInnerPane.getTransforms()) {
            if (transform == null) {
                transform = t;
            }
            else {
                transform = transform.createConcatenation(t);
            }
        }
        simulationInnerPane.getTransforms().clear();
        simulationInnerPane.getTransforms().add(transform);
        syncTransforms();
        currentScale *= scale.getX();
        scale = new Scale();
    }
}
