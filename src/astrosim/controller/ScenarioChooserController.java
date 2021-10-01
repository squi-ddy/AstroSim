package astrosim.controller;

import astrosim.Main;
import astrosim.model.managers.ResourceManager;
import astrosim.model.managers.ScenarioManager;
import astrosim.model.managers.SettingsManager;
import astrosim.model.xml.XMLParseException;
import astrosim.model.xml.XMLParser;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class ScenarioChooserController implements Initializable {
    @FXML
    private BorderPane root;
    @FXML
    private VBox scenarioLister;
    @FXML
    private ImageView logoImage;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private Parent createScenarioTile(String fileName, String scenarioName) {
        VBox tileRoot = new VBox();
        tileRoot.setAlignment(Pos.TOP_CENTER);
        tileRoot.getStyleClass().add("save-chooser-elem");
        Label topLabel = new Label(scenarioName);
        topLabel.setFont(new Font(18));
        topLabel.setStyle("-fx-text-fill: -theme-text-color-2;");
        Label bottomLabel = new Label(fileName);
        bottomLabel.setFont(new Font(10));
        bottomLabel.setStyle("-fx-text-fill: -theme-text-color-2;");
        HBox bottomHBox = new HBox(bottomLabel);
        bottomHBox.setAlignment(Pos.CENTER);
        tileRoot.getChildren().addAll(topLabel, bottomLabel);
        tileRoot.setOnMouseEntered(e -> {
            topLabel.setStyle("-fx-text-fill: -theme-text-color-1;");
            bottomLabel.setStyle("-fx-text-fill: -theme-text-color-1;");
            SVGPath xSymbolShape = new SVGPath();
            xSymbolShape.setContent("M 5 5 L -5 -5 M -5 5 L 5 -5");
            xSymbolShape.setStyle("-fx-stroke-width: 2.5; -fx-stroke-line-cap: round;");
            Button xButton = new Button();
            Group xButtonGroup = new Group();
            xButton.setGraphic(xSymbolShape);
            xButtonGroup.setManaged(false);
            xButtonGroup.getChildren().add(xButton);
            xButton.getStyleClass().add("themed-button");
            tileRoot.getChildren().add(xButtonGroup);
            xButtonGroup.layoutXProperty().bind(tileRoot.widthProperty().subtract(20).subtract(15));
            xButtonGroup.layoutYProperty().bind(tileRoot.heightProperty().divide(2).subtract(15));
            xButton.setOnAction(action -> {
                new Thread(() -> ScenarioManager.deleteScenario(fileName)).start(); // this is a time-consuming action, do in a thread.
                scenarioLister.getChildren().removeIf(node -> node == tileRoot);
                doIfEmpty();
            });
        });
        tileRoot.setOnMouseExited(e -> {
            topLabel.setStyle("-fx-text-fill: -theme-text-color-2;");
            bottomLabel.setStyle("-fx-text-fill: -theme-text-color-2;");
            tileRoot.getChildren().removeIf(Group.class::isInstance);
        });
        tileRoot.setOnMouseClicked(e -> {
            if (e.getClickCount() >= 2 && e.getButton() == MouseButton.PRIMARY) {
                ScenarioManager.loadScenario(fileName);
                renderScenario();
            }
        });
        return tileRoot;
    }

    @FXML
    private void createScenario() {
        ScenarioManager.makeScenario();
        renderScenario();
    }

    public void renderScenario() {
        ScenarioManager.renderScenario(stage);
        loadFiles();
        root.getStylesheets().removeIf(s -> s.contains("dark") || s.contains("light"));
        root.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/view/css/" + (SettingsManager.getGlobalSettings().isDarkMode() ? "dark.css" : "light.css"))).toExternalForm());
        new Thread(() -> {
            try {
                SettingsManager.save();
            } catch (XMLParseException e) {
                e.printStackTrace();
            }
        }).start();
        ScenarioManager.getScenario().stopThreadNow();
        stage.showAndWait();
    }

    private void doIfEmpty() {
        if (scenarioLister.getChildren().isEmpty()) {
            Label noSaves = new Label("No Saves!");
            noSaves.setFont(new Font("System Italic", 13));
            noSaves.setStyle("-fx-text-fill: -theme-text-color-1;");
            scenarioLister.getChildren().add(noSaves);
        }
    }

    private void loadFiles() {
        scenarioLister.getChildren().clear();
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logoImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/logo.png"))));
        loadFiles();
    }
 }
