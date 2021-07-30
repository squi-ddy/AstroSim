package model.simulation;

import model.math.Vector;

import java.util.ArrayList;

public class Simulator implements Runnable {
    private ArrayList<Planet> planets;
    private final int planetIdx;
    private Planet planet;
    private final double valG;
    private final ArrayList<Vector> positions;
    private double tStep;
    private int steps;

    public Simulator(ArrayList<Planet> planets, int planetIdx, double valG, ArrayList<Vector> positions, double tStep, double simFor) {
        this.planetIdx = planetIdx;
        this.planets = planets;
        this.planet = planets.get(planetIdx);
        this.valG = valG;
        this.positions = positions;
        this.tStep = tStep;
        this.steps = (int) Math.round(simFor / tStep);
    }

    @Override
    public void run() {
        while (steps > 0) {
            Vector accelG = new Vector();
            for (Planet value : planets) {
                accelG.add(accelG(value, planet));
            }
            positions.add(positions.get(positions.size() - 1).add(planet.getVelocity().multiply(tStep)).add(accelG.multiply(0.5 * tStep * tStep)));
            steps -= 1;
        }
    }

    private Vector accelG(Planet p1, Planet p2) {
        // Returns acceleration due to gravity between planet p1 to p2.
        Vector r = p1.getPosition().sub(p2.getPosition());
        double distR = r.magnitude();
        return r.multiply(-valG * p1.getMass() / distR / distR / distR);
    }
}
