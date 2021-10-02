package astrosim.model.managers;

import astrosim.Main;
import astrosim.model.simulation.Scenario;
import astrosim.model.xml.XMLParseException;
import astrosim.model.xml.XMLParser;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ScenarioManager {
    // deals with loading and saving scenarios
    private static Scenario scenario;
    private static XMLParser parser;

    // Prevent normal construction
    private ScenarioManager() {}

    static {
        String lastSave = SettingsManager.getGlobalSettings().getLastSave();
        if (lastSave != null && lastSave.matches(".+\\.xml")) {
            try {
                parser = new XMLParser(ResourceManager.getPath("saves/" + lastSave));
                scenario = Scenario.fromXML(parser.getContent().get("scenario"));
            } catch (XMLParseException e) {
                scenario = null;
            }
        }
        try {
            Thread.sleep(2000);
        } catch (@SuppressWarnings("java:S2142") InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Scenario getScenario() {
        return scenario;
    }

    public static void deleteScenario(String name) {
        try {
            Files.delete(ResourceManager.getPath("saves/" + name));
            SettingsManager.getGlobalSettings().setLastSave(null);
            scenario = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void makeScenario() {
        scenario = new Scenario();
        SettingsManager.getGlobalSettings().setLastSave(null);
    }

    public static void loadScenario(String name) {
        try {
            SettingsManager.getGlobalSettings().setLastSave(name);
            parser = new XMLParser(ResourceManager.getPath("saves/" + name));
            scenario = Scenario.fromXML(parser.getContent().get("scenario"));
        } catch (XMLParseException e) {
            scenario = null;
        }
    }

    public static void save(String fileName) {
        try {
            Path result = ResourceManager.guaranteeExists("saves/" + fileName, "/defaultSave.xml");
            parser = new XMLParser(result);
            parser.writeContent(new String[]{"scenario"}, scenario.hashed());
            SettingsManager.getGlobalSettings().setLastSave(fileName);
            new Thread(() -> {
                try {
                    parser.saveXML();
                } catch (XMLParseException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (XMLParseException e) {
            e.printStackTrace();
        }
    }

    public static void renderScenario(Stage toHide) {
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("/view/fxml/simulator.fxml")));
            root.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/view/css/" + (SettingsManager.getGlobalSettings().isDarkMode() ? "dark.css" : "light.css"))).toExternalForm());
            root.requestFocus();
            Scene scene = new Scene(root);
            stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("/images/icon.png"))));
            stage.setScene(scene);
            stage.setTitle("AstroSim");
            Platform.runLater(toHide::hide);
            SimulatorGUIManager.getInspector().hidePane();
            stage.showAndWait();
            SimulatorGUIManager.getController().setSpeed(0);
            SimulatorGUIManager.scaleProperty().set(1);
            scenario.stopThreadNow();
            scenario = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean waitUntilInit() {
        return scenario != null;
    }
}
