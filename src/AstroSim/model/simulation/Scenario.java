package AstroSim.model.simulation;

import AstroSim.model.files.Settings;
import AstroSim.model.xml.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Scenario implements XMLHashable {
    // Loads the simulation field.
    // To be implemented
    private final double valG;
    private final double valRes;
    private final List<Planet> planets;
    private Thread simulation;
    private Simulator simulator;

    public Scenario() {
        this(6.67e-11, 1e10, new ArrayList<>());
    }

    public Scenario(double valG, double valRes, List<Planet> planets) {
        this.valG = valG;
        this.valRes = valRes;
        this.planets = planets;
    }

    public List<Planet> getPlanets() {
        return planets;
    }

    public void stopThread() {
        // TODO: stop thread in gui
    }

    public void startThread() {
        simulator = new Simulator(planets, valG, valRes, 0.01 * Settings.getAccuracy() / 10);
        simulation = new Thread(simulation);
        simulator.addSteps(Settings.getMaxBufferInTrail());
        simulation.start();
    }

    public void nextStep() {
        planets.forEach(p -> p.getPath().addToTrail());
        simulator.addSteps(1);
        simulation.notify();
    }

    @Override
    public XMLNodeInfo hashed() {
        HashMap<String, XMLNodeInfo> hashed = new HashMap<>();
        hashed.put("valG", new XMLNodeInfo(String.valueOf(valG)));
        hashed.put("valRes", new XMLNodeInfo(String.valueOf(valRes)));
        List<XMLNodeInfo> planetsHashed = new ArrayList<>();
        planets.forEach(e -> planetsHashed.add(e.hashed()));
        hashed.put("planets", XMLList.hashed(planetsHashed));
        return new XMLNodeInfo(hashed);
    }

    public static Scenario fromXML(XMLNodeInfo info) throws XMLParseException {
        try {
            HashMap<String, XMLNodeInfo> hashed = info.getDataTable();
            double valG = Double.parseDouble(hashed.get("valG").getValue());
            double valRes = Double.parseDouble(hashed.get("valRes").getValue());
            List<XMLNodeInfo> planetsHashed = XMLList.fromXML(hashed.get("planets"));
            List<Planet> planets = new ArrayList<>();
            for (XMLNodeInfo planetInfo : planetsHashed) {
                planets.add(Planet.fromXML(planetInfo));
            }
            return new Scenario(valG, valRes, planets);
        } catch (XMLParseException | NumberFormatException e) {
            throw new XMLParseException(XMLParseException.XML_ERROR);
        }
    }
}
