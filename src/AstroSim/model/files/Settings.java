package AstroSim.model.files;

import AstroSim.model.xml.XMLNodeInfo;
import AstroSim.model.xml.XMLParseException;
import AstroSim.model.xml.XMLParser;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Settings {
    private static final Path filepath = Paths.get(System.getProperty("user.dir"), "data", "settings.xml");
    private static XMLParser settingsXML;
    private static short accuracy = 5; // global simulation accuracy (a number between 1 - 10) -> determines time step
    private static String lastSave = "null"; // file path to last save; provides smoothness
    private static short speed = 1; // The speed of the simulation
    private static int burstAmount = 50; // The amount of steps to calculate at a time
    private static int maxPointsInTrail = 150;
    private static int maxBufferInTrail = 150;

    // Data class: stores settings from settings.xml (i.e. global settings)

    static {
        try {
            Settings.settingsXML = new XMLParser(filepath);
            fromXML(Settings.settingsXML.getContent(new String[]{"settings"}).get("settings"));
        } catch (XMLParseException e) {
            e.printStackTrace();
        }
    }

    public static void setMaxBufferInTrail(int maxBufferInTrail) {
        Settings.maxBufferInTrail = maxBufferInTrail;
        try {
            settingsXML.writeContent(new String[]{"settings", "bufferLen"}, new XMLNodeInfo(maxBufferInTrail));
        } catch (XMLParseException e) {
            // ???
        }
    }

    public static void setMaxPointsInTrail(int maxPointsInTrail) {
        Settings.maxPointsInTrail = maxPointsInTrail;
        try {
            settingsXML.writeContent(new String[]{"settings", "trailLen"}, new XMLNodeInfo(maxPointsInTrail));
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

    public static void setBurstAmount(short burstAmount) {
        Settings.burstAmount = burstAmount;
        try {
            settingsXML.writeContent(new String[]{"settings", "burstAmount"}, new XMLNodeInfo(burstAmount));
        } catch (XMLParseException e) {
            // ???
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

    public static int getBurstAmount() {
        return burstAmount;
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
            accuracy = Short.parseShort(settings.get("accuracy").getValue());
            lastSave = settings.get("lastSave").getValue();
            speed = Short.parseShort(settings.get("speed").getValue());
            burstAmount = Integer.parseInt(settings.get("burstAmount").getValue());
            maxBufferInTrail = Integer.parseInt(settings.get("bufferLen").getValue());
            maxPointsInTrail = Integer.parseInt(settings.get("trailLen").getValue());
        } catch (XMLParseException | NumberFormatException | ClassCastException | NullPointerException e) {
            restoreDefaults();
            throw new XMLParseException(XMLParseException.XML_ERROR);
        }
    }
}
