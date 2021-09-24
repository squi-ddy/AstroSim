package astrosim.model.simulation;

import astrosim.model.files.Settings;
import astrosim.model.xml.XMLHashable;
import astrosim.model.xml.XMLList;
import astrosim.model.xml.XMLNodeInfo;
import astrosim.model.xml.XMLParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Scenario implements XMLHashable {
    // Loads the simulation field.
    // To be implemented
    private double valG;
    private double valRes;
    private final List<Planet> planets;
    private ExecutorService runner;

    public Scenario() {
        this(6.67e-11, 1e10, new ArrayList<>());
    }

    public Scenario(double valG, double valRes, List<Planet> planets) {
        this.valG = valG;
        this.valRes = valRes;
        this.planets = planets;
        Simulator.setPlanets(planets); // due to referencing, this updates automatically
        Simulator.setValG(valG);
        Simulator.setValRes(valRes);
    }

    public List<Planet> getPlanets() {
        return planets;
    }

    public void stopThread() {
        runner.shutdownNow();
    }

    public void startThread() {
        runner = Executors.newSingleThreadExecutor();
        Simulator simulator = new Simulator(Settings.getMaxBufferInTrail());
        runner.submit(simulator);
    }

    public void nextStep(int steps) {
        planets.forEach(p -> p.getPath().addToTrail(steps));
        Simulator simulator = new Simulator(steps);
        runner.submit(simulator);
    }

    public void setValRes(double valRes) {
        this.valRes = valRes;
        Simulator.setValRes(valRes);
    }

    public void setValG(double valG) {
        this.valG = valG;
        Simulator.setValG(valG);
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
            Map<String, XMLNodeInfo> hashed = info.getDataTable();
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
