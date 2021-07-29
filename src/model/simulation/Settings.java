package model.simulation;

import model.xml.XMLNodeInfo;
import model.xml.XMLParseException;
import model.xml.XMLParser;

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
    public boolean modifiable;

    // Data class: stores settings from settings.xml (i.e. global settings)
    // Constructor is designed to be run again and again until it works
    @SuppressWarnings("unchecked")
    public Settings(boolean modifiable) throws XMLParseException {
        this.modifiable = modifiable;
        if (!modifiable) return;
        try {
            HashMap<String, XMLNodeInfo> settings;
            this.settingsXML = new XMLParser(filepath);
            settings = (HashMap<String, XMLNodeInfo>) this.settingsXML.getContent(new String[]{"settings"}).get("settings").getData();
            if (settings == null) {
                fixCorruption();
                throw new XMLParseException(XMLParseException.XML_ERROR);
            }
            XMLNodeInfo val = settings.get("accuracy");
            if (val == null || val.getNodeType() == XMLNodeInfo.HAS_CHILDREN) {
                fixCorruption();
                throw new XMLParseException(XMLParseException.XML_ERROR);
            }
            accuracy = Short.parseShort((String) val.getData());
            val = settings.get("lastSave");
            if (val == null || val.getNodeType() == XMLNodeInfo.HAS_CHILDREN) {
                fixCorruption();
                throw new XMLParseException(XMLParseException.XML_ERROR);
            }
            lastSave = (String)(val.getData());
        } catch (XMLParseException | NumberFormatException | ClassCastException e) {
            fixCorruption();
            throw new XMLParseException(XMLParseException.XML_ERROR);
        }
    }

    public Settings() throws XMLParseException {
        this(true);
    }

    public void setAccuracy(short accuracy) {
        this.accuracy = accuracy;
        try {
            settingsXML.writeContent(new String[]{"settings", "accuracy"}, new XMLNodeInfo(String.valueOf(accuracy)));
        } catch (XMLParseException e) {
            // ???
        }
    }

    public short getAccuracy() {
        return accuracy;
    }

    public String getLastSave() {
        return lastSave.equals("null") ? null : lastSave;
    }

    public void restoreDefaults() {
        setLastSave("null");
        setAccuracy((short) 5);
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

    private void fixCorruption() {
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
