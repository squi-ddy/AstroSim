package model;

import javafx.application.Application;
import javafx.stage.Stage;
import model.simulation.Settings;
import model.xml.XMLParseException;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        System.out.println(Settings.getSpeed());
        System.out.println(Settings.getAccuracy());
        System.out.println(Settings.getLastSave());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
