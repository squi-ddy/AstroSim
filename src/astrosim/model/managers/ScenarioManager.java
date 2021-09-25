package astrosim.model.managers;

import astrosim.model.simulation.Scenario;
import astrosim.model.xml.XMLParseException;
import astrosim.model.xml.XMLParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ScenarioManager {
    // deals with loading and saving scenarios
    private static Scenario scenario;
    private static XMLParser parser;

    // Prevent normal construction
    private ScenarioManager() {}

    static {
        String lastSave = Settings.getLastSave();
        if (lastSave != null) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void makeScenario() {
        ScenarioManager.scenario = new Scenario();
    }

    public static void loadScenario(String name) {
        try {
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

    public static void renderScenario() {
        // TODO: render scenario
    }

    public static void save() throws XMLParseException {parser.saveXML();}

    public static boolean waitUntilInit() {
        return scenario != null;
    }
}
