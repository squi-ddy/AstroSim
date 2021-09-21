package AstroSim.model.simulation;

import AstroSim.model.files.ResourceManager;
import AstroSim.model.xml.XMLNodeInfo;
import AstroSim.model.xml.XMLParseException;
import AstroSim.model.xml.XMLParser;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Settings {
    private static final Path filepath = Paths.get(System.getProperty("user.dir"), "data", "settings.xml");
    private static XMLParser settingsXML;
    private static short accuracy = 5; // global simulation accuracy (a number between 1 - 10) -> determines distance step
    private static String lastSave = "null"; // file path to last save; provides smoothness
    private static short speed = 1; // The speed of the simulation

    // Data class: stores settings from settings.xml (i.e. global settings)

    static {
        try {
            Settings.settingsXML = new XMLParser(filepath);
            fromXML(Settings.settingsXML.getContent(new String[]{"settings"}).get("settings"));
        } catch (XMLParseException e) {
            e.printStackTrace();
        }
    }

    public static void setAccuracy(short accuracy) {
        Settings.accuracy = accuracy;
        try {
            settingsXML.writeContent(new String[]{"settings", "accuracy"}, new XMLNodeInfo(accuracy));
        } catch (XMLParseException e) {
            // ???
        }
    }

    public static void setSpeed(short speed) {
        Settings.speed = speed;
        try {
            settingsXML.writeContent(new String[]{"settings", "speed"}, new XMLNodeInfo(speed));
        } catch (XMLParseException e) {
            // ???
        }
    }

    public static short getSpeed() {
        return speed;
    }

    public static short getAccuracy() {
        return accuracy;
    }

    public static String getLastSave() {
        return lastSave.equals("null") ? null : lastSave;
    }

    public static void setLastSave(String lastSave) {
        Settings.lastSave = lastSave;
        try {
            settingsXML.writeContent(new String[]{"settings", "lastSave"}, new XMLNodeInfo(String.valueOf(lastSave)));
        } catch (XMLParseException e) {
            // ???
        }
    }

    public static void saveSettings() throws XMLParseException {
        settingsXML.saveXML();
    }

    private static void restoreDefaults() {
        ResourceManager.restoreDefault(filepath);
    }

    private static void fromXML(XMLNodeInfo info) throws XMLParseException {
        try {
            var settings = info.getDataTable();
            XMLNodeInfo val = settings.get("accuracy");
            accuracy = Short.parseShort(val.getValue());
            val = settings.get("lastSave");
            lastSave = val.getValue();
            val = settings.get("speed");
            speed = Short.parseShort(val.getValue());
        } catch (XMLParseException | NumberFormatException | ClassCastException | NullPointerException e) {
            restoreDefaults();
            throw new XMLParseException(XMLParseException.XML_ERROR);
        }
    }
}
