package AstroSim.model;

import javafx.application.Application;
import javafx.stage.Stage;
import AstroSim.model.simulation.Settings;
import AstroSim.model.xml.XMLParseException;

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
