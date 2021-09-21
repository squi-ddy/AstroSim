package model.simulation;

import model.math.Vector;

import java.util.List;

public class Simulator implements Runnable {
    private final List<Planet> planets;
    private final double valG;
    private final double tStep;
    private int steps;

    public Simulator(List<Planet> planets, double valG, double tStep, double simFor) {
        this.planets = planets;
        this.valG = valG;
        this.tStep = tStep;
        this.steps = (int) Math.round(simFor / tStep);
    }

    @Override
    public void run() {
        while (steps > 0) {
            for (Planet p1 : planets) {
                Vector accelG = new Vector();
                for (Planet p2 : planets) {
                    accelG.add(accelG(p1, p2));
                }
                OrbitalPath positions = p1.getPath();
                positions.add(positions.get(positions.size() - 1).add(planet.getVelocity().multiply(tStep)).add(accelG.multiply(0.5 * tStep * tStep)));
                steps -= 1;
            }
        }
    }

    private Vector accelG(Planet p1, Planet p2) {
        // Returns acceleration due to gravity between planet p1 to p2.
        Vector r = p1.getPosition().sub(p2.getPosition());
        double distR = r.magnitude();
        return r.multiply(-valG * p1.getMass() / distR / distR / distR);
    }
}
