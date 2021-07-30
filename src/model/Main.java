package model;

import javafx.application.Application;
import javafx.stage.Stage;
import model.simulation.Settings;
import model.xml.XMLParseException;

public class Main extends Application {
    public static final int MAX_RETRY = 10;
    public static Settings globalSettings;

    @Override
    public void start(Stage stage) {
        setGlobalSettings();
        System.out.println(globalSettings.getSpeed());
        System.out.println(globalSettings.getAccuracy());
        System.out.println(globalSettings.getLastSave());
    }

    private void setGlobalSettings() {
        for (int i = 1; i <= MAX_RETRY; i++){
            try {
                globalSettings = new Settings();
                return;
            } catch (XMLParseException e) {
                System.out.println("[WARN] Attempt to open global settings failed (" + i + "/" + MAX_RETRY + ")");
            }
        }
        try {
            globalSettings = new Settings(false);
        } catch (XMLParseException e) {
            // something has gone terribly wrong
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
