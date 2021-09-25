package astrosim.model.managers;

import astrosim.model.simulation.OrbitalPath;
import astrosim.model.xml.XMLNodeInfo;
import astrosim.model.xml.XMLParseException;
import astrosim.model.xml.XMLParser;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Settings {
    private Settings() {}

    private static final Path filepath = Paths.get(System.getProperty("user.dir"), "settings.xml");
    private static XMLParser settingsXML;
    private static short accuracy = 5; // global simulation accuracy (a number between 1 - 10) -> determines time step
    private static String lastSave = null; // file path to last save; provides smoothness
    private static short speed = 1; // The speed of the simulation
    private static int maxPointsInTrail = 150;
    private static int maxBufferInTrail = 150;
    private static boolean darkMode = true;

    // Data class: stores settings from settings.xml (i.e. global settings)

    static {
        try {
            ResourceManager.guaranteeExists(filepath, "/settings.xml");
            Settings.settingsXML = new XMLParser(filepath);
            fromXML(Settings.settingsXML.getContent(new String[]{"settings"}).get("settings"));
            // A sleep for the splash screen
            Thread.sleep(2000);
        } catch (XMLParseException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void waitUntilInit() { /* Empty method to wait for construct */ }

    public static void setDarkMode(boolean darkMode) {
        Settings.darkMode = darkMode;
        try {
            settingsXML.writeContent(new String[]{"settings", "dark"}, new XMLNodeInfo(String.valueOf(darkMode)));
        } catch (XMLParseException e) {
            // ???
        }
    }

    public static boolean isDarkMode() {
        return darkMode;
    }

    public static void setMaxBufferInTrail(int maxBufferInTrail) {
        Settings.maxBufferInTrail = maxBufferInTrail;
        try {
            settingsXML.writeContent(new String[]{"settings", "bufferLen"}, new XMLNodeInfo(String.valueOf(maxBufferInTrail)));
        } catch (XMLParseException e) {
            // ???
        }
    }

    public static void setMaxPointsInTrail(int maxPointsInTrail) {
        Settings.maxPointsInTrail = maxPointsInTrail;
        try {
            settingsXML.writeContent(new String[]{"settings", "trailLen"}, new XMLNodeInfo(String.valueOf(maxPointsInTrail)));
        } catch (XMLParseException e) {
            // ???
        }
    }

    public static int getMaxBufferInTrail() {
        return maxBufferInTrail;
    }

    public static int getMaxPointsInTrail() {
        return maxPointsInTrail;
    }

    public static void setAccuracy(short accuracy) {
        Settings.accuracy = accuracy;
        try {
            settingsXML.writeContent(new String[]{"settings", "accuracy"}, new XMLNodeInfo(String.valueOf(accuracy)));
        } catch (XMLParseException e) {
            // ???
        }
    }

    public static void setSpeed(short speed) {
        Settings.speed = speed;
        try {
            settingsXML.writeContent(new String[]{"settings", "speed"}, new XMLNodeInfo(String.valueOf(speed)));
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
        return lastSave;
    }

    public static void setLastSave(String lastSave) {
        Settings.lastSave = lastSave;
        try {
            settingsXML.writeContent(new String[]{"settings", "lastSave"}, new XMLNodeInfo((lastSave != null) ? lastSave : "null"));
        } catch (XMLParseException e) {
            // ???
        }
    }

    public static void save() throws XMLParseException {
        settingsXML.saveXML();
    }

    private static void fromXML(XMLNodeInfo info) throws XMLParseException {
        try {
            var settings = info.getDataTable();
            accuracy = Short.parseShort(settings.get("accuracy").getValue());
            lastSave = (settings.get("lastSave").getValue().equals("null") ? null : settings.get("lastSave").getValue());
            speed = Short.parseShort(settings.get("speed").getValue());
            maxBufferInTrail = Integer.parseInt(settings.get("bufferLen").getValue());
            OrbitalPath.setMaxBufferLength(maxBufferInTrail);
            maxPointsInTrail = Integer.parseInt(settings.get("trailLen").getValue());
            OrbitalPath.setMaxLength(maxPointsInTrail);
            darkMode = Boolean.parseBoolean(settings.get("dark").getValue());
        } catch (XMLParseException | NumberFormatException | NullPointerException e) {
            throw new XMLParseException(XMLParseException.XML_ERROR);
        }
    }
}
