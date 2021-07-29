package model;

import model.xml.XMLNodeInfo;
import model.xml.XMLParseException;
import model.xml.XMLParser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class Settings {
    private static final Path filepath = Paths.get(System.getProperty("user.dir") , "settings.xml");
    private XMLParser settingsXML;
    // Data class: stores settings from XMLParser
    public Settings() {
        try {
            this.settingsXML = new XMLParser(filepath);
            XMLNodeInfo settings = Objects.requireNonNull(this.settingsXML.getContent().get("settings"));
        } catch (XMLParseException e) {
            if (e.getType() == XMLParseException.IO_EXCEPTION) {
                // technically, this is due to an IO Exception
                // try to generate the file again
                try {
                    Files.copy(Paths.get(Objects.requireNonNull(getClass().getResource("defaultSettings.xml")).toURI()), filepath, StandardCopyOption.REPLACE_EXISTING);
                } catch (URISyntaxException | IOException e2) {
                    // Settings cannot be loaded nor saved if this occurs
                    // Warn in console
                    System.out.println("[WARN] Could not load settings. Running with default settings. No settings changes will be saved.");
                }
            }
        } catch (NullPointerException e) {
            // Probably some sort of corruption; reset to default
            System.out.println("[WARN] Could not load settings, as it appears to be corrupted. Resetting to default...");
            try {
                Files.copy(Paths.get(Objects.requireNonNull(getClass().getResource("defaultSettings.xml")).toURI()), filepath, StandardCopyOption.REPLACE_EXISTING);
            } catch (URISyntaxException | IOException e2) {
                // Settings cannot be loaded nor saved if this occurs
                // Warn in console
                System.out.println("[WARN] Could not reset settings. Running with default settings. No settings changes will be saved.");
            }
        }
    }

    public static void main(String[] args) {
        new Settings();
    }
}
