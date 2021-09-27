package astrosim.model.simulation;

import astrosim.model.managers.Settings;
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
    private double valG;
    private double valRes;
    private final List<Planet> planets;
    private String name;
    private ExecutorService runner;

    public Scenario() {
        this(6.67e-11, 1e10, new ArrayList<>(), "Unnamed Scenario");
    }

    public Scenario(double valG, double valRes, List<Planet> planets, String name) {
        this.valG = valG;
        this.valRes = valRes;
        this.planets = planets;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public XMLNodeInfo hashed() {
        HashMap<String, XMLNodeInfo> hashed = new HashMap<>();
        hashed.put("valG", new XMLNodeInfo(String.valueOf(valG)));
        hashed.put("valRes", new XMLNodeInfo(String.valueOf(valRes)));
        hashed.put("name", new XMLNodeInfo(name));
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
            String name = hashed.get("name").getValue();
            List<XMLNodeInfo> planetsHashed = XMLList.fromXML(hashed.get("planets"));
            List<Planet> planets = new ArrayList<>();
            for (XMLNodeInfo planetInfo : planetsHashed) {
                planets.add(Planet.fromXML(planetInfo));
            }
            return new Scenario(valG, valRes, planets, name);
        } catch (XMLParseException | NumberFormatException e) {
            throw new XMLParseException(XMLParseException.Type.XML_ERROR);
        }
    }
}
