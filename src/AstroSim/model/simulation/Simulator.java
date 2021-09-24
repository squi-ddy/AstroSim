package AstroSim.model.simulation;

import AstroSim.model.math.Vector;
import AstroSim.model.xml.XMLList;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class Simulator implements Runnable {
    private final List<Planet> planets;
    private final double valG;
    private final double valRes;
    private final double tStep;
    private int steps = 0;

    public void addSteps(int steps) {
        this.steps += steps;
    }

    public Simulator(List<Planet> planets, double valG, double valRes, double tStep) {
        this.planets = planets;
        this.valRes = valRes;
        this.valG = valG;
        this.tStep = tStep;
    }

    @Override
    public void run() {
        while (steps > 0) {
            for (Planet p1 : planets) {
                if (p1.isStatic()) continue;
                Vector accel = new Vector();
                for (Planet p2 : planets) {
                    // intersection resolver
                    Vector segment = p2.getPosition().sub(p1.getPosition());
                    double dist = segment.magnitude();
                    dist -= p2.getRadius();
                    dist -= p1.getRadius();
                    dist = Math.min(0, dist);
                    accel.add(segment.normalise().multiply(dist * valRes / p1.getMass()));
                    accel.add(accelG(p1, p2));
                }
                OrbitalPath path = p1.getPath();
                Vector position = path.getLatestPosition().add(path.getLatestVelocity().multiply(tStep)).add(accel.multiply(0.5 * tStep * tStep));
                Vector velocity = path.getLatestVelocity().add(accel.multiply(tStep));
                path.addPosition(position, velocity);
            }
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
            steps--;
        }
    }

    private Vector accelG(Planet p1, Planet p2) {
        // Returns acceleration due to gravity between planet p1 to p2.
        Vector r = p1.getPosition().sub(p2.getPosition());
        double distR = r.magnitude();
        return r.multiply(-valG * p1.getMass() / distR / distR / distR);
    }
}
