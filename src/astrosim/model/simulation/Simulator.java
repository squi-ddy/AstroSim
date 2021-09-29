package astrosim.model.simulation;

import astrosim.model.managers.Settings;
import astrosim.model.math.Vector2D;

import java.util.List;

public class Simulator implements Runnable {
    private static List<Planet> planets;
    private static double valG;
    private final int steps;

    public static void setPlanets(List<Planet> planets) {
        Simulator.planets = planets;
    }

    public static void setValG(double valG) {
        Simulator.valG = valG;
    }

    public Simulator(int steps) {
        this.steps = steps;
    }

    @Override
    public void run() {
        double tStep = 0.001 * (11 - Settings.getAccuracy()) / 10;
        for (int i = 0; i < steps && !Thread.interrupted(); i++) {
            for (Planet p1 : planets) {
                if (p1.isStatic()) continue;
                Vector2D accel = new Vector2D();
                Vector2D velocity = p1.getPath().getLatestVelocity();
                for (Planet p2 : planets) {
                    if (p1 == p2) continue;
                    // intersection resolver
                    Vector2D r = p1.getPath().getLatestPosition().sub(p2.getPath().getLatestPosition());
                    double dist = r.magnitude();
                    accel = accel.add(accelG(p2, r, dist));
                    if (dist - p1.getRadius() - p2.getRadius() <= 0) {
                        accel = accel.sub(r.normalise().multiply(accel.dot(r.normalise()) + 0.5));
                        velocity = velocity.sub(r.normalise().multiply(velocity.dot(r.normalise()) + 0.5));
                    }
                }
                OrbitalPath path = p1.getPath();
                Vector2D position = path.getLatestPosition().add(path.getLatestVelocity().multiply(tStep)).add(accel.multiply(0.5 * tStep * tStep));
                velocity = velocity.add(accel.multiply(tStep));
                path.addPosition(position, velocity);
            }
        }
    }

    private Vector2D accelG(Planet p2, Vector2D r, double distR) {
        // Returns acceleration due to gravity between planet p1 to p2.
        return r.multiply(-valG * p2.getMass() / distR / distR / distR);
    }
}
