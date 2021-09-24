package astrosim.model;

import astrosim.model.files.ScenarioManager;
import astrosim.model.files.Settings;
import astrosim.model.math.Vector2D;
import astrosim.model.simulation.Planet;
import astrosim.model.xml.XMLParseException;

import java.nio.file.Path;

public class ModelTester {
    public static void main(String[] args) throws XMLParseException, InterruptedException {
        ScenarioManager.makeScenario();
        ScenarioManager.getScenario().getPlanets().add(new Planet(new Vector2D(), new Vector2D(1, 0)));
        ScenarioManager.getScenario().startThread();
        Thread.sleep(1000);
        System.out.println(ScenarioManager.getScenario().getPlanets().get(0).getPath().getPositionBuffer().toString());
        ScenarioManager.softSave(Path.of(System.getProperty("user.dir"), "save.xml"));
        ScenarioManager.save();
        Settings.save();
    }
}
