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
        String lastSave = Settings.getLastSave();
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Scenario getScenario() {
        return scenario;
    }

    public static void deleteScenario(String name) {
        try {
            Files.delete(ResourceManager.getPath("saves/" + name));
            Settings.setLastSave(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void makeScenario() {
        ScenarioManager.scenario = new Scenario();
    }

    public static void loadScenario(String name) {
        try {
            Settings.setLastSave(name);
            parser = new XMLParser(ResourceManager.getPath("saves/" + name));
            scenario = Scenario.fromXML(parser.getContent().get("scenario"));
        } catch (XMLParseException e) {
            scenario = null;
        }
    }

    public static void softSave(String fileName) throws XMLParseException {
        Path result = ResourceManager.guaranteeExists("saves/" + fileName, "/defaultSave.xml");
        parser = new XMLParser(result);
        parser.writeContent(new String[]{"scenario"}, scenario.hashed());
        Settings.setLastSave(fileName);
    }

    public static void renderScenario(Stage toHide) {
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("view/fxml/simulator.fxml")));
            root.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("view/css/" + (Settings.isDarkMode() ? "dark.css" : "light.css"))).toExternalForm());
            Scene scene = new Scene(root);
            stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("/images/icon.png"))));
            stage.setScene(scene);
            stage.setTitle("AstroSim");
            Platform.runLater(toHide::hide);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() throws XMLParseException {parser.saveXML();}

    public static boolean waitUntilInit() {
        return scenario != null;
    }
}
