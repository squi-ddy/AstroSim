package AstroSim.model.simulation;

import AstroSim.model.xml.XMLNodeInfo;
import AstroSim.model.xml.XMLParseException;
import AstroSim.model.xml.XMLParser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Objects;

public class Settings {
    private static final Path filepath = Paths.get(System.getProperty("user.dir"), "settings.xml");
    private XMLParser settingsXML;
    private short accuracy = 5; // global simulation accuracy (a number between 1 - 10) -> determines distance step
    private String lastSave = "null"; // file path to last save; provides smoothness
    private short speed = 1; // The speed of the simulation
    public boolean modifiable;

    // Data class: stores settings from settings.xml (i.e. global settings)
    // Constructor is designed to be run again and again until it works
    public Settings(boolean modifiable) throws XMLParseException {
        this.modifiable = modifiable;
        if (!modifiable) return;
        try {
            HashMap<String, XMLNodeInfo> settings;
            this.settingsXML = new XMLParser(filepath);
            settings = this.settingsXML.getContent(new String[]{"settings"}).get("settings").getDataTable();
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

    public Settings() throws XMLParseException {
        this(true);
    }

    public void setAccuracy(short accuracy) {
        this.accuracy = accuracy;
        try {
            settingsXML.writeContent(new String[]{"settings", "accuracy"}, new XMLNodeInfo(accuracy));
        } catch (XMLParseException e) {
            // ???
        }
    }

    public void setSpeed(short speed) {
        this.speed = speed;
        try {
            settingsXML.writeContent(new String[]{"settings", "speed"}, new XMLNodeInfo(speed));
        } catch (XMLParseException e) {
            // ???
        }
    }

    public short getSpeed() {
        return speed;
    }

    public short getAccuracy() {
        return accuracy;
    }

    public String getLastSave() {
        return lastSave.equals("null") ? null : lastSave;
    }

    public void setLastSave(String lastSave) {
        this.lastSave = lastSave;
        try {
            settingsXML.writeContent(new String[]{"settings", "lastSave"}, new XMLNodeInfo(String.valueOf(lastSave)));
        } catch (XMLParseException e) {
            // ???
        }
    }

    public void saveSettings() throws XMLParseException {
        settingsXML.saveXML();
    }

    private void restoreDefaults() {
        // Probably some sort of corruption; reset to default
        try {
            Files.copy(Paths.get(Objects.requireNonNull(getClass().getResource("/defaultSettings.xml")).toURI()), filepath, StandardCopyOption.REPLACE_EXISTING);
        } catch (URISyntaxException | IOException e2) {
            // Settings cannot be loaded nor saved if this occurs
            // Warn in console
            System.out.println("Could not restore defaults.");
        }
    }
}
