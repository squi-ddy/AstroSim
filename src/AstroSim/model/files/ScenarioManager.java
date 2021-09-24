package AstroSim.model.files;

import AstroSim.model.simulation.Scenario;

public class ScenarioManager {
    // deals with loading and saving scenarios
    private static Scenario scenario;

    public static Scenario getScenario() {
        return scenario;
    }
}
