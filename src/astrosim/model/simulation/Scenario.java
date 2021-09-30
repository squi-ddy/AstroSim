package astrosim.model.simulation;

import astrosim.model.managers.SettingsManager;
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
import java.util.concurrent.TimeUnit;

public class Scenario implements XMLHashable {
    private double valG;
    private final List<Planet> planets;
    private String name;
    private ExecutorService runner;

    public Scenario() {
        this(6.67e-11, new ArrayList<>(), "Unnamed Scenario");
    }

    public Scenario(double valG, List<Planet> planets, String name) {
        this.valG = valG;
        this.planets = planets;
        this.name = name;
        Simulator.setPlanets(planets); // due to referencing, this updates automatically
        Simulator.setValG(valG);
    }

    public List<Planet> getPlanets() {
        return planets;
    }

    public void stopThread() {
        if (runner != null) {
            runner.shutdownNow();
            waitForThreadEnd();
        }
    }

    public void startThread() {
        stopThread();
        planets.forEach(p -> p.getPath().clearBuffer());
        runner = Executors.newSingleThreadExecutor();
        Simulator simulator = new Simulator(SettingsManager.getMaxBufferInTrail());
        runner.submit(simulator);
    }

    public void simulateSteps(int steps) {
        Simulator simulator = new Simulator(steps);
        runner.submit(simulator);
    }

    public void waitForThreadEnd() {
        try {
            boolean result = runner.awaitTermination(1000, TimeUnit.SECONDS);
            if (!result) throw new IllegalStateException();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
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
            String name = hashed.get("name").getValue();
            List<XMLNodeInfo> planetsHashed = XMLList.fromXML(hashed.get("planets"));
            List<Planet> planets = new ArrayList<>();
            for (XMLNodeInfo planetInfo : planetsHashed) {
                planets.add(Planet.fromXML(planetInfo));
            }
            return new Scenario(valG, planets, name);
        } catch (XMLParseException | NumberFormatException e) {
            throw new XMLParseException(XMLParseException.Type.XML_ERROR);
        }
    }
}
