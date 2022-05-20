package astrosim.model.simulation;

import astrosim.model.managers.SettingsManager;
import astrosim.model.math.Vec2;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public record Simulator(int steps) implements Runnable {
    private static List<Planet> planets;
    private static double valG;

    public static void setPlanets(List<Planet> planets) {
        Simulator.planets = planets;
    }

    public static void setValG(double valG) {
        Simulator.valG = valG;
    }

    @Override
    public void run() {
        double tStep = 0.00005 * (101 - SettingsManager.getGlobalSettings().getAccuracy());
        for (int i = 0; i < steps && !Thread.interrupted(); i++) {
            List<Pair<Planet, Pair<Vec2, Vec2>>> newPosVel = new ArrayList<>();
            for (Planet p1 : planets) {
                if (p1.isStatic()) continue;
                if (p1.getPath().isBufferFull()) return;
                newPosVel.add(new Pair<>(p1, updatePath(p1, tStep)));
            }
            newPosVel.forEach(p -> p.getKey().getPath().addPosition(p.getValue().getKey(), p.getValue().getValue()));
        }
    }

    private Vec2 accelG(Planet p2, Vec2 r) {
        // Returns acceleration due to gravity between planet p1 to p2.
        return r.mul(-valG * p2.getMass() / r.magnitude() / r.magnitude() / r.magnitude());
    }

    private Pair<Vec2, Vec2> handleIntersects(Planet p1, List<Planet> intersect, Vec2 velocity, Vec2 accel) {
        if (intersect.isEmpty()) return new Pair<>(velocity, accel);
        for (Planet p2 : intersect) {
            Vec2 r = p2.getPath().getLatestPosition().sub(p1.getPath().getLatestPosition());
            if (r.magnitude() == 0) {
                double angle = Math.random() * 2 * Math.PI;
                r = new Vec2(Math.sin(angle), Math.cos(angle)).mul(0.01);
            }
            accel = accel.sub(r.normalise().mul(accel.dot(r.normalise()) + 10 * (p1.getRadius() + p2.getRadius() - r.magnitude())));
            velocity = velocity.sub(r.normalise().mul(velocity.dot(r.normalise()) + 10 * (p1.getRadius() + p2.getRadius() - r.magnitude())));
        }
        return new Pair<>(velocity, accel);
    }

    private Pair<Vec2, Vec2> updatePath(Planet p1, double tStep) {
        Vec2 accel = new Vec2();
        List<Planet> intersect = new ArrayList<>();
        for (Planet p2 : planets) {
            if (p1 == p2) continue;
            Vec2 r = p1.getPath().getLatestPosition().sub(p2.getPath().getLatestPosition());
            double dist = r.magnitude();
            if (dist - p1.getRadius() - p2.getRadius() <= 0) {
                intersect.add(p2);
            } else {
                accel = accel.add(accelG(p2, r));
            }
        }

        Vec2 velocity = p1.getPath().getLatestVelocity();
        Pair<Vec2, Vec2> vectors = handleIntersects(p1, intersect, velocity, accel);
        velocity = vectors.getKey();
        accel = vectors.getValue();
        Vec2 newVelocity = velocity.add(accel.mul(tStep));
        OrbitalPath path = p1.getPath();
        Vec2 position = path.getLatestPosition().add(velocity.mul(tStep)).add(accel.div(2).mul(tStep * tStep));

        return new Pair<>(position, newVelocity);
    }
}
