package astrosim.model.simulation;

import astrosim.model.math.Vec2;
import astrosim.model.xml.XMLHashable;
import astrosim.model.xml.XMLNodeInfo;
import astrosim.model.xml.XMLParseException;
import astrosim.view.nodes.Trail;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class OrbitalPath implements XMLHashable {
    // Stores tracers.
    private final Deque<Vec2> positionBuffer;
    private final Deque<Vec2> velocityBuffer;
    private Pair<Vec2, Vec2> lastPosVel;
    private static int maxLength = 0;
    private static int maxBufferLength = 0;
    private static int positionGap = 0;
    private static int pending = 0;
    private int untilNextAdd = positionGap;
    private final Trail trail;

    public static void setMaxLength(int maxLength) {
        OrbitalPath.maxLength = maxLength;
    }

    public static void setMaxBufferLength(int maxBufferLength) {
        OrbitalPath.maxBufferLength = maxBufferLength;
    }

    public static void setPositionGap(int positionGap) {
        OrbitalPath.positionGap = positionGap;
    }

    public static void addPending(int steps) {pending += steps;}

    public static void removePending(int steps) {pending -= steps;}

    public static int getMaxBufferLength() {
        return maxBufferLength;
    }

    public void setPlanet(Planet planet) {
        trail.setColor(Color.web(planet.getTrailColor()));
    }

    public OrbitalPath(Vec2 position, Vec2 velocity, boolean showing) {
        this.positionBuffer = new ArrayDeque<>();
        this.velocityBuffer = new ArrayDeque<>();
        this.lastPosVel = new Pair<>(position, velocity);
        this.trail = new Trail(position, this, showing);
    }

    public OrbitalPath() {this(new Vec2(), new Vec2(), true);}

    public void addPosition(Vec2 pos, Vec2 vel) {
        if (positionBuffer.size() < maxBufferLength + pending) {
            positionBuffer.add(pos);
            velocityBuffer.add(vel);
        }
    }

    public boolean isBufferFull() {
        return positionBuffer.size() >= maxBufferLength + pending;
    }

    public void clearBuffer() {
        positionBuffer.clear();
        velocityBuffer.clear();
        untilNextAdd = 0;
    }

    public Trail getTrail() {
        return trail;
    }

    public Vec2 getLatestVelocity() {
        if (!velocityBuffer.isEmpty()) return velocityBuffer.getLast();
        return getVelocity();
    }

    public Vec2 getLatestPosition() {
        if (!positionBuffer.isEmpty()) return positionBuffer.getLast();
        return getPosition();
    }

    public Vec2 getPosition() {
        return lastPosVel.getKey();
    }

    public Vec2 getVelocity() {
        return lastPosVel.getValue();
    }

    public void setPosition(Vec2 position, Vec2 velocity) {
        positionBuffer.clear();
        velocityBuffer.clear();
        lastPosVel = new Pair<>(position, velocity);
        untilNextAdd = 0;
        trail.clearTrail();
        trail.clearPermanentTrail();
    }

    public void addToTrail(int steps) {
        if (steps > positionBuffer.size()) System.getLogger("Logger").log(System.Logger.Level.WARNING,"Buffer empty!");
        for (int i = 0; i < steps; i++) {
            if (untilNextAdd == 0 && !positionBuffer.isEmpty()) {
                trail.addPointToTrail(positionBuffer.getFirst());
                untilNextAdd = positionGap + 1;
            }
            lastPosVel = new Pair<>(positionBuffer.removeFirst(), velocityBuffer.removeFirst());
            untilNextAdd--;
        }
        trail.changePoint(lastPosVel.getKey());
        while (trail.getNumPoints() > maxLength + 1) {
            trail.deletePointFromTrail();
        }
    }

    @Override
    public XMLNodeInfo hashed() {
        HashMap<String, XMLNodeInfo> hashed = new HashMap<>();
        hashed.put("position", getPosition().hashed());
        hashed.put("velocity", getVelocity().hashed());
        hashed.put("show", new XMLNodeInfo(String.valueOf(trail.isShowing())));
        return new XMLNodeInfo(hashed);
    }

    public static OrbitalPath fromXML(XMLNodeInfo info) throws XMLParseException {
        try {
            Map<String, XMLNodeInfo> hashed = info.getDataTable();
            Vec2 position = Vec2.fromXML(hashed.get("position"));
            Vec2 velocity = Vec2.fromXML(hashed.get("velocity"));
            boolean showing = Boolean.parseBoolean(hashed.get("show").getValue());
            return new OrbitalPath(position, velocity, showing);
        } catch (XMLParseException | NullPointerException | NumberFormatException e) {
            throw new XMLParseException(XMLParseException.Type.XML_ERROR);
        }
    }
}
