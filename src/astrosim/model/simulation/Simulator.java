package astrosim.model.simulation;

import astrosim.model.managers.Settings;
import astrosim.model.math.Vector2D;

import java.util.List;

public class Simulator implements Runnable {
    private static List<Planet> planets;
    private static double valG;
    private static double valRes;
    private final int steps;

    public static void setPlanets(List<Planet> planets) {
        Simulator.planets = planets;
    }

    public static void setValG(double valG) {
        Simulator.valG = valG;
    }

    public static void setValRes(double valRes) {
        Simulator.valRes = valRes;
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
                for (Planet p2 : planets) {
                    if (p1 == p2) continue;
                    // intersection resolver
                    Vector2D r = p1.getPath().getLatestPosition().sub(p2.getPath().getLatestPosition());
                    double dist = r.magnitude();
                    dist -= p2.getRadius();
                    dist -= p1.getRadius();
                    dist = Math.min(0, dist);
                    accel.add(r.normalise().multiply(dist * valRes / p1.getMass()));
                    accel.add(accelG(p2, r, dist));
                }
                OrbitalPath path = p1.getPath();
                Vector2D position = path.getLatestPosition().add(path.getLatestVelocity().multiply(tStep)).add(accel.multiply(0.5 * tStep * tStep));
                Vector2D velocity = path.getLatestVelocity().add(accel.multiply(tStep));
                path.addPosition(position, velocity);
            }
        }
    }

    private Vector2D accelG(Planet p2, Vector2D r, double distR) {
        // Returns acceleration due to gravity between planet p1 to p2.
        return r.multiply(-valG * p2.getMass() / distR / distR / distR);
    }
}
