package astrosim.model.managers;

import astrosim.model.simulation.Settings;
import astrosim.model.xml.XMLParseException;
import astrosim.model.xml.XMLParser;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SettingsManager {
    private SettingsManager() {}

    private static final Path filepath = Paths.get(System.getProperty("user.dir"), "settings.xml");
    private static XMLParser settingsXML;
    private static Settings settings;

    static {
        try {
            ResourceManager.guaranteeExists(filepath, "/settings.xml");
            settingsXML = new XMLParser(filepath);
            settings = Settings.fromXML(settingsXML.getContent(new String[]{"settings"}).get("settings"));
            // A sleep for the splash screen
            Thread.sleep(2000);
        } catch (@SuppressWarnings("java:S2142") XMLParseException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void waitUntilInit() { /* Empty method to wait for construct */ }

    public static void save() throws XMLParseException {
        settingsXML.writeContent(new String[]{"settings"}, settings.hashed());
        settingsXML.saveXML();
    }

    public static Settings getGlobalSettings() {
        return settings;
    }
}
