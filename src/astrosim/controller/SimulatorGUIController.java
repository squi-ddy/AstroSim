package astrosim.controller;

import astrosim.Main;
import astrosim.model.managers.ScenarioManager;
import astrosim.model.managers.SettingsManager;
import astrosim.model.managers.SimulatorGUIManager;
import astrosim.model.math.Functions;
import astrosim.model.simulation.OrbitalPath;
import astrosim.model.simulation.Planet;
import astrosim.model.simulation.Scenario;
import astrosim.view.helpers.InspectablePlanetList;
import astrosim.view.nodes.PlanetNode;
import astrosim.view.nodes.inspector.Inspectable;
import astrosim.view.nodes.inspector.InspectorPane;
import astrosim.view.nodes.inspector.InspectorSetting;
import astrosim.view.nodes.inspector.StringInspectorSetting;
import astrosim.view.nodes.menu.Menu;
import astrosim.view.nodes.menu.MenuItem;
import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimulatorGUIController implements Initializable {
    @FXML
    private AnchorPane inspectorParentPane;
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
    private ToggleButton pauseButton;
    @FXML
    private ToggleButton speed1Button;
    @FXML
    private ToggleButton speed2Button;
    @FXML
    private ToggleButton speed3Button;
    @FXML
    private HBox menuBar;

    private ToggleButton[] speedButtons;
    private Double lastX = null;
    private Double lastY = null;
    private Scale scale = new Scale();
    private double currentScale = 1;
    private final InspectablePlanetList planetNodes = new InspectablePlanetList();
    private ScheduledExecutorService simulator = Executors.newSingleThreadScheduledExecutor();
    private int burstSteps = (int) Math.ceil(OrbitalPath.getMaxBufferLength() / SettingsManager.getGlobalSettings().getBurstFactor());
    private final int[] speedValues = new int[]{0, 1, 3, 9};
    private boolean taskRunning = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        speedButtons = new ToggleButton[]{pauseButton, speed1Button, speed2Button, speed3Button};
        for (int i = 0; i < 4; i++) {
            int finalI = i;
            speedButtons[i].setOnAction(e -> setSpeed(speedValues[finalI]));
        }
        simulatorRoot.prefWidthProperty().bind(root.widthProperty());
        simulatorRoot.prefHeightProperty().bind(root.heightProperty());
        syncSpeedButtons();
        List<MenuItem> fileMenu = new ArrayList<>();
        List<MenuItem> objectMenu = new ArrayList<>();
        List<MenuItem> settingsMenu = new ArrayList<>();
        fileMenu.add(new MenuItem("Save", () -> doSave(false)));
        fileMenu.add(new MenuItem("Save As", () -> doSave(true)));
        fileMenu.add(new MenuItem("Delete", this::doDelete));
        objectMenu.add(new MenuItem("Create new Planet", this::newPlanet));
        objectMenu.add(MenuItem.SPACING);
        objectMenu.add(new MenuItem("See all Planets", this::zoomOnPlanetPane));
        settingsMenu.add(new MenuItem("Global", this::globalSettings));
        settingsMenu.add(new MenuItem("Scenario", this::simulationSettings));
        menuBar.getChildren().addAll(
                new Menu("Files", fileMenu, root),
                new Menu("Objects", objectMenu, root),
                new Menu("Settings", settingsMenu, root)
        );
        setUpSimulator();
        SimulatorGUIManager.setController(this);
        InspectorPane inspector = SimulatorGUIManager.getInspector();
        AnchorPane.setTopAnchor(inspector, 0.);
        AnchorPane.setRightAnchor(inspector, 0.);
        AnchorPane.setBottomAnchor(inspector, 0.);
        inspectorParentPane.getChildren().add(inspector);
    }

    private void simulationSettings() {
        SimulatorGUIManager.getInspector("Properties").loadSettings(ScenarioManager.getScenario());
    }

    private void globalSettings() {
        SimulatorGUIManager.getInspector("Properties").loadSettings(SettingsManager.getGlobalSettings());
    }

    private void doSave(boolean saveAs) {
        if (!saveAs && SettingsManager.getGlobalSettings().getLastSave() != null && SettingsManager.getGlobalSettings().getLastSave().contains("xml")) ScenarioManager.save(SettingsManager.getGlobalSettings().getLastSave());
        else {
            SimulatorGUIManager.getInspector("Save").loadSettings(new Inspectable() {
                private final String fileName = SettingsManager.getGlobalSettings().getLastSave();

                private void saveFile(String name) {
                    ScenarioManager.save(name);
                }

                @Override
                public List<InspectorSetting<?>> getSettings() {
                    List<InspectorSetting<?>> settings = new ArrayList<>();
                    settings.add(new StringInspectorSetting("File Name", fileName != null && fileName.contains("xml") ? fileName : "", this::saveFile, s -> s.matches("[a-zA-Z][a-zA-Z0-9-_]*\\.xml")));
                    return settings;
                }

                @Override
                public void onClose() {
                    // nothing
                }
            });
        }
    }

    private void doDelete() {
        if (SettingsManager.getGlobalSettings().getLastSave().contains("xml")) {
            new Thread(() -> ScenarioManager.deleteScenario(SettingsManager.getGlobalSettings().getLastSave())).start();
            root.getScene().getWindow().hide();
        }
    }

    private void newPlanet() {
        Planet planet = new Planet();
        setSpeed(0);
        ScenarioManager.getScenario().stopThreadNow();
        ScenarioManager.getScenario().getPlanets().add(planet);
        placePlanet(planet);
        ScenarioManager.getScenario().startThread();
    }

    public void deletePlanet(PlanetNode node) {
        setSpeed(0);
        ScenarioManager.getScenario().stopThreadNow();
        ScenarioManager.getScenario().getPlanets().remove(node.getPlanet());
        simulationInnerPane.getChildren().remove(node);
        planetNodes.getPlanetList().remove(node);
        ScenarioManager.getScenario().startThread();
    }

    private void runSimulationGUI() {
        if (taskRunning) return;
        taskRunning = true;
        ScenarioManager.getScenario().stopThread();
        Platform.runLater(() -> {
            planetNodes.getPlanetList().forEach(p -> {
                if (!p.getPlanet().isStatic()) {
                    Platform.runLater(() -> {
                        p.getPlanet().getPath().addToTrail(burstSteps);
                        p.updatePlanet();
                    });
                }
            });
            ScenarioManager.getScenario().simulateSteps(burstSteps + 10);
            taskRunning = false;
        });
    }

    public void setSpeed(int speedLevel) {
        SettingsManager.getGlobalSettings().setSpeed(speedLevel);
        if (speedLevel != 0) SimulatorGUIManager.getInspector().hidePane();
        simulator.shutdownNow();
        try {
            boolean status = simulator.awaitTermination(1000, TimeUnit.SECONDS);
            if (!status) throw new IllegalStateException();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (speedLevel != 0) {
            simulator = Executors.newSingleThreadScheduledExecutor();
            simulator.scheduleAtFixedRate(this::runSimulationGUI, 0, (long) (101 - SettingsManager.getGlobalSettings().getAccuracy()) *  30 / speedLevel * burstSteps, TimeUnit.MICROSECONDS);
        }
        syncSpeedButtons();
    }

    private void zoomOnPlanetPane() {
        InspectorPane inspector = SimulatorGUIManager.getInspector("Planets");
        inspector.loadSettings(planetNodes);
        inspector.showPane();
    }

    public void syncSpeedButtons() {
        for (int i = 0; i < speedButtons.length; i++) {
            speedButtons[i].setSelected(SettingsManager.getGlobalSettings().getSpeed() == speedValues[i]);
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
            Point2D trueMouseCoordinates = invertTransforms(new Point2D(e.getX() - simulationInnerPane.getLayoutX(), e.getY() - simulationInnerPane.getLayoutY()));
            xLabel.setText(String.valueOf((int) (trueMouseCoordinates.getX())));
            yLabel.setText(String.valueOf((int) (trueMouseCoordinates.getY())));
        });
        simulationPane.setOnScrollStarted(e -> simulationInnerPane.getTransforms().add(scale));
        simulationPane.setOnScroll(e -> {
            if (!simulationInnerPane.getTransforms().contains(scale)) simulationInnerPane.getTransforms().add(scale);
            double direction = e.getDeltaX() * e.getMultiplierX();
            if (direction == 0) direction = -e.getDeltaY() * e.getMultiplierY();
            Point2D trueMouseCoordinates = invertTransforms(new Point2D(e.getX() - simulationInnerPane.getLayoutX(), e.getY() - simulationInnerPane.getLayoutY()));
            scaleScenario(direction * SettingsManager.getGlobalSettings().getSensitivity() / 2500000, trueMouseCoordinates.getX(), trueMouseCoordinates.getY());
        });
        simulationPane.setOnScrollFinished(e -> combineTransforms());
        doSimulatorClip();
        // add some speckles (stars!)
        addSpeckles();
        renderScenario();
    }

    private void scaleScenario(double deltaPercent, double scaleFromX, double scaleFromY) {
        if (scaleFromX != scale.getPivotX() || scaleFromY != scale.getPivotY()) {
            combineTransforms();
            simulationInnerPane.getTransforms().add(scale);
        }
        double scaleBy = Math.max(currentScale * scale.getX() - deltaPercent, 0.01) / currentScale;
        SimulatorGUIManager.scaleProperty().set(currentScale * scaleBy);
        long zoom = Math.round(currentScale * scaleBy * 1000);
        zoomLabel.setText(String.valueOf(zoom / 10) + '.' + zoom % 10);
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
        PlanetNode node = new PlanetNode(planet);
        planetNodes.getPlanetList().add(node);
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

    private void combineTransforms() {
        currentScale *= scale.getX();
        scale = new Scale();
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
    }

    public void moveTo(Planet planet) {
        Point2D coordinates = applyTransforms(planet.getPosition().toPoint2D());
        simulationInnerPane.setLayoutX(simulationPane.getWidth() / 2 - coordinates.getX());
        simulationInnerPane.setLayoutY(simulationPane.getHeight() / 2 - coordinates.getY());
        // Scale for the new planet to fit exactly 10% of scenario space
        double zoom = 0.05 * Math.min(simulationPane.getWidth(), simulationPane.getHeight()) / planet.getRadius();
        combineTransforms();
        scale.setPivotX(planet.getPosition().getX() + 1);
        scale.setPivotY(planet.getPosition().getY() + 1);
        scaleScenario(scale.getX() * currentScale - zoom, planet.getPosition().getX(), planet.getPosition().getY());
    }

    public Point2D invertTransforms(Point2D coordinates) {
        for (Transform t : simulationInnerPane.getTransforms()) {
            try {
                coordinates = t.inverseTransform(coordinates);
            } catch (NonInvertibleTransformException ex) {
                ex.printStackTrace();
            }
        }
        return coordinates;
    }

    public Point2D applyTransforms(Point2D coordinates) {
        for (Transform t : simulationInnerPane.getTransforms()) {
            coordinates = t.transform(coordinates);
        }
        return coordinates;
    }

    public void setBurstFactor(double burstFactor) {
        this.burstSteps = (int) Math.ceil(OrbitalPath.getMaxBufferLength() / burstFactor);
        this.planetNodes.getPlanetList().forEach(p -> p.getPlanet().getPath().clearBuffer());
        ScenarioManager.getScenario().startThread();
    }

    public void setDarkMode(boolean darkMode) {
        if (darkMode) {
            root.getStylesheets().removeIf(s -> s.contains("light"));
        } else {
            root.getStylesheets().removeIf(s -> s.contains("dark"));
        }
        root.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/view/css/" + (darkMode ? "dark.css" : "light.css"))).toExternalForm());

    }
}
