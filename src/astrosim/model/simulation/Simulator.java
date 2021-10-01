package astrosim.model.simulation;

import astrosim.model.managers.SettingsManager;
import astrosim.model.math.Vector2D;
import javafx.util.Pair;

import java.util.ArrayList;
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
        double tStep = 0.0001 * (101 - SettingsManager.getGlobalSettings().getAccuracy());
        for (int i = 0; i < steps && !Thread.interrupted(); i++) {
            for (Planet p1 : planets) {
                if (p1.isStatic()) continue;
                updatePath(p1, tStep);
            }
        }
    }

    private Vector2D accelG(Planet p2, Vector2D r) {
        // Returns acceleration due to gravity between planet p1 to p2.
        return r.multiply(-valG * p2.getMass() / r.magnitude() / r.magnitude() / r.magnitude());
    }

    private Pair<Vector2D, Vector2D> handleIntersects(Planet p1, List<Planet> intersect, Vector2D velocity, Vector2D accel) {
        for (Planet p2 : intersect) {
            Vector2D r = p2.getPath().getLatestPosition().sub(p1.getPath().getLatestPosition());
            if (r.magnitude() == 0) {
                double angle = Math.random() * 2 * Math.PI;
                r = new Vector2D(Math.sin(angle), Math.cos(angle)).multiply(0.01);
            }
            accel = accel.sub(r.normalise().multiply(accel.dot(r.normalise()) + 5 * (p1.getRadius() + p2.getRadius() - r.magnitude())));
            velocity = velocity.sub(r.normalise().multiply(velocity.dot(r.normalise()) + 5 * (p1.getRadius() + p2.getRadius() - r.magnitude())));
        }
        return new Pair<>(velocity, accel);
    }

    private void updatePath(Planet p1, double tStep) {
        Vector2D accel = new Vector2D();
        List<Planet> intersect = new ArrayList<>();
        for (Planet p2 : planets) {
            if (p1 == p2) continue;
            Vector2D r = p1.getPath().getLatestPosition().sub(p2.getPath().getLatestPosition());
            double dist = r.magnitude();
            if (dist - p1.getRadius() - p2.getRadius() <= 0) {
                intersect.add(p2);
            } else {
                accel = accel.add(accelG(p2, r));
            }
        }
        Vector2D velocity = p1.getPath().getLatestVelocity();
        Pair<Vector2D, Vector2D> vectors = handleIntersects(p1, intersect, velocity, accel);
        velocity = vectors.getKey();
        accel = vectors.getValue();
        OrbitalPath path = p1.getPath();
        Vector2D position = path.getLatestPosition().add(path.getLatestVelocity().multiply(tStep)).add(accel.multiply(0.5 * tStep * tStep));
        velocity = velocity.add(accel.multiply(tStep));
        path.addPosition(position, velocity);
    }
}
