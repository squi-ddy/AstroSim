package astrosim.controller;

import astrosim.model.managers.ResourceManager;
import astrosim.model.managers.ScenarioManager;
import astrosim.model.managers.Settings;
import astrosim.model.xml.XMLParseException;
import astrosim.model.xml.XMLParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class ScenarioChooserController implements Initializable {
    @FXML
    private VBox scenarioLister;
    @FXML
    private ImageView logoImage;

    private Parent createScenarioTile(String fileName, String scenarioName) {
        VBox root = new VBox();
        root.setAlignment(Pos.TOP_CENTER);
        root.getStyleClass().add("scroll-pane-elem");
        Label topLabel = new Label(scenarioName);
        topLabel.setFont(new Font(18));
        topLabel.setStyle("-fx-text-fill: -theme-text-color-2;");
        Label bottomLabel = new Label(fileName);
        bottomLabel.setFont(new Font(10));
        bottomLabel.setStyle("-fx-text-fill: -theme-text-color-2;");
        HBox bottomHBox = new HBox(bottomLabel);
        bottomHBox.setAlignment(Pos.CENTER);
        root.getChildren().addAll(topLabel, bottomLabel);
        root.setOnMouseEntered(e -> {
            topLabel.setStyle("-fx-text-fill: -theme-text-color-1;");
            bottomLabel.setStyle("-fx-text-fill: -theme-text-color-1;");
            SVGPath xSymbolShape = new SVGPath();
            xSymbolShape.setContent("M 5 5 L -5 -5 M -5 5 L 5 -5");
            xSymbolShape.setStyle("");
            Button xButton = new Button();
            Group xButtonGroup = new Group();
            xButton.setGraphic(xSymbolShape);
            xButtonGroup.setManaged(false);
            xButtonGroup.getChildren().add(xButton);
            xButton.getStyleClass().add("themed-button");
            root.getChildren().add(xButtonGroup);
            xButtonGroup.layoutXProperty().bind(root.widthProperty().subtract(20).subtract(15));
            xButtonGroup.layoutYProperty().bind(root.heightProperty().divide(2).subtract(15));
            xButton.setOnAction(action -> {
                new Thread(() -> ScenarioManager.deleteScenario(fileName)).start(); // this is a time-consuming action, do in a thread.
                scenarioLister.getChildren().removeIf(node -> node == root);
                doIfEmpty();
            });
        });
        root.setOnMouseExited(e -> {
            topLabel.setStyle("-fx-text-fill: -theme-text-color-2;");
            bottomLabel.setStyle("-fx-text-fill: -theme-text-color-2;");
            root.getChildren().removeIf(Group.class::isInstance);
        });
        root.setOnMouseClicked(e -> {
            if (e.getClickCount() >= 2 && e.getButton() == MouseButton.PRIMARY) {
                ScenarioManager.loadScenario(fileName);
                ScenarioManager.renderScenario();
            }
        });
        return root;
    }

    @FXML
    private void createScenario(ActionEvent e) {
        ScenarioManager.makeScenario();
    }

    private void doIfEmpty() {
        if (scenarioLister.getChildren().isEmpty()) {
            Label noSaves = new Label("No Saves!");
            noSaves.setFont(new Font("System Italic", 13));
            noSaves.setStyle("-fx-text-fill: -theme-text-color-1;");
            scenarioLister.getChildren().add(noSaves);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logoImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/logo.png"))));
        Settings.setLastSave(null);
        ResourceManager.getFilesInDirectory("saves/").forEach(p -> {
            try {
                XMLParser parser = new XMLParser(p);
                scenarioLister.getChildren().add(createScenarioTile(p.getFileName().toString(), parser.getContent(new String[] {"scenario", "name"}).get("name").getValue()));
            } catch (XMLParseException e) {
                e.printStackTrace();
            }
        });
        doIfEmpty();
    }
 }
