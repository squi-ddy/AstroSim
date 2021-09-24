package astrosim.model.files;

import astrosim.model.simulation.Scenario;
import astrosim.model.xml.XMLParseException;
import astrosim.model.xml.XMLParser;

import java.nio.file.Path;

public class ScenarioManager {
    // deals with loading and saving scenarios
    private static Scenario scenario;
    private static XMLParser parser;

    // Prevent normal construction
    private ScenarioManager() {}

    static {
        Path lastSave = Settings.getLastSave();
        if (lastSave != null) {
            try {
                parser = new XMLParser(lastSave);
                scenario = Scenario.fromXML(parser.getContent().get("scenario"));
            } catch (XMLParseException e) {
                e.printStackTrace();
            }
        }
    }

    public static Scenario getScenario() {
        return scenario;
    }

    public static void makeScenario() {
        ScenarioManager.scenario = new Scenario();
    }

    public static void softSave(Path filePath) throws XMLParseException {
        ResourceManager.guaranteeExists(filePath, "/defaultSave.xml");
        parser = new XMLParser(filePath);
        parser.writeContent(new String[]{"scenario"}, scenario.hashed());
        Settings.setLastSave(filePath);
    }

    public static void save() throws XMLParseException {parser.saveXML();}

}
