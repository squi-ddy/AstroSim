package astrosim.controller;

import astrosim.Main;
import astrosim.model.managers.ResourceManager;
import astrosim.model.managers.ScenarioManager;
import astrosim.model.managers.Settings;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class SplashController implements Initializable {
    @FXML
    private Label statusLabel;
    @FXML
    private ImageView logoImage;

    private FadeTransition statusFlash;
    private Stage rootStage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logoImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/logo.png"))));
        statusLabel.setText("Loading settings...");
        statusFlash = new FadeTransition(Duration.millis(200), statusLabel);
        statusFlash.setCycleCount(Animation.INDEFINITE);
        statusFlash.setAutoReverse(true);
        statusFlash.setFromValue(0.8);
        statusFlash.setToValue(0.4);
        loadSettings();
    }

    public void setStage(Stage stage) {
        this.rootStage = stage;
    }

    private void loadSettings() {
        statusFlash.play();
        new Thread(() -> {
            Settings.waitUntilInit();
            Platform.runLater(this::loadFile);
        }).start();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void loadFile() {
        if (Settings.getLastSave() != null && !Settings.getLastSave().equals("firstTime")) {
            statusLabel.setText("Loading save...");
            new Thread(() -> {
                if (ScenarioManager.waitUntilInit()) {
                    Platform.runLater(() -> {
                        statusFlash.stop();
                        ScenarioManager.renderScenario(rootStage);
                    });
                }
                else {
                    statusFlash.stop();
                    Settings.setLastSave(null);
                    loadFile();
                }
            }).start();
        } else {
            statusLabel.setText("Setting up...");
            new Thread(() -> {
                if (Objects.equals(Settings.getLastSave(), "firstTime")) {
                    ResourceManager.copyFromResourceDirectory("/defaultSaves/", "saves/");
                }
                ScenarioManager.waitUntilInit();
                Platform.runLater(this::loadScenarioChooser);
            }).start();
        }
    }

    private void loadScenarioChooser() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(Main.class.getResource("view/fxml/scenarioChooser.fxml")));
            Parent root = loader.load();
            root.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("view/css/" + (Settings.isDarkMode() ? "dark.css" : "light.css"))).toExternalForm());
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            loader.<ScenarioChooserController>getController().setStage(stage);
            stage.setTitle("AstroSim");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/icon.png"))));
            statusFlash.stop();
            rootStage.close();
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}