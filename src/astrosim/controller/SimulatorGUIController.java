package astrosim.controller;

import astrosim.model.managers.Settings;
import astrosim.model.math.Vector2D;
import astrosim.model.simulation.Planet;
import astrosim.view.guihelpers.MenuItem;
import astrosim.view.guihelpers.MenuRenderer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Scale;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SimulatorGUIController implements Initializable {
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
        simulationInnerPane.getTransforms().add(scale);
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
            try {
                Point2D trueMouseCoords = scale.inverseTransform(e.getX() - simulationInnerPane.getLayoutX(), e.getY() - simulationInnerPane.getLayoutY());
                xLabel.setText(String.valueOf((int) (trueMouseCoords.getX())));
                yLabel.setText(String.valueOf((int) (trueMouseCoords.getY())));
            } catch (NonInvertibleTransformException ex) {
                ex.printStackTrace();
            }
        });
        simulationPane.setOnScroll(e -> {
            scaleScenario(Math.max(0.5, scale.getX() - e.getDeltaY() / 1000), e.getX(), e.getY());
        });
    }

    private Vector2D convertCoordinates(Vector2D position) {
        return position.sub(new Vector2D(simulationPane.getWidth() / 2, simulationPane.getHeight() / 2));
    }

    private void scaleScenario(double scaleBy, double scaleFromX, double scaleFromY) {
        scale.setPivotX(scaleFromX);
        scale.setPivotY(scaleFromY);
        scale.setX(scaleBy);
        scale.setY(scaleBy);
    }

    private void placePlanet(Planet planet) {

    }
}
