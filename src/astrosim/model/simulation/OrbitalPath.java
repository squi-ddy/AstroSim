package astrosim.model.simulation;

import astrosim.model.math.Vector2D;
import astrosim.model.xml.XMLHashable;
import astrosim.model.xml.XMLNodeInfo;
import astrosim.model.xml.XMLParseException;
import javafx.util.Pair;

import java.util.*;

public class OrbitalPath implements XMLHashable {
    // Stores tracers.
    private final Deque<Vector2D> positionBuffer;
    private final Deque<Vector2D> velocityBuffer;
    private final Deque<Vector2D> positionTrail;
    private Pair<Vector2D, Vector2D> lastPosVel;
    private static int maxLength = 0;
    private static int maxBufferLength = 0;
    private static int positionGap = 0;
    private int untilNextAdd = 0;
    private double trailLength = 0;

    public static void setMaxLength(int maxLength) {
        OrbitalPath.maxLength = maxLength;
    }

    public static void setMaxBufferLength(int maxBufferLength) {
        OrbitalPath.maxBufferLength = maxBufferLength;
    }

    public static void setPositionGap(int positionGap) { OrbitalPath.positionGap = positionGap;}

    public OrbitalPath(Vector2D position, Vector2D velocity) {
        this.positionTrail = new ArrayDeque<>(List.of(position));
        this.positionBuffer = new ArrayDeque<>();
        this.velocityBuffer = new ArrayDeque<>();
        this.lastPosVel = new Pair<>(position, velocity);
    }

    public void addPosition(Vector2D pos, Vector2D vel) {
        if (positionBuffer.size() < maxBufferLength) {
            positionBuffer.add(pos);
            velocityBuffer.add(vel);
        }
    }

    public void clearBuffer() {
        positionBuffer.clear();
        velocityBuffer.clear();
        untilNextAdd = 0;
    }

    public Vector2D getLatestVelocity() {
        if (!velocityBuffer.isEmpty()) return velocityBuffer.getLast();
        return getVelocity();
    }

    public Vector2D getLatestPosition() {
        if (!positionBuffer.isEmpty()) return positionBuffer.getLast();
        return getPosition();
    }

    public List<Vector2D> getTrail() {
        List<Vector2D> ptsVector;
        ptsVector = new ArrayList<>(positionTrail);
        ptsVector.add(getPosition());
        return ptsVector;
    }

    public Vector2D getPosition() {
        return lastPosVel.getKey();
    }

    public Vector2D getVelocity() {
        return lastPosVel.getValue();
    }

    public void setPosition(Vector2D position, Vector2D velocity) {
        positionTrail.clear();
        positionBuffer.clear();
        velocityBuffer.clear();
        positionTrail.add(position);
        lastPosVel = new Pair<>(position, velocity);
        untilNextAdd = 0;
        trailLength = 0;
    }

    public void addToTrail(int steps) {
        if (steps > positionBuffer.size()) throw new IllegalArgumentException();
        for (int i = 0; i < steps; i++) {
            if (untilNextAdd == 0) {
                trailLength += positionBuffer.getFirst().sub(positionTrail.getLast()).magnitude();
                positionTrail.add(positionBuffer.getFirst());
                untilNextAdd = positionGap + 1;
            }
            lastPosVel = new Pair<>(positionBuffer.getFirst(), velocityBuffer.getFirst());
            positionBuffer.removeFirst();
            velocityBuffer.removeFirst();
            untilNextAdd--;
        }
        while (positionTrail.size() > maxLength) {
            Vector2D last = positionTrail.removeFirst();
            trailLength -= last.sub(positionTrail.getFirst()).magnitude();
        }
    }

    public double getTrailLength() {
        return trailLength + getPosition().sub(positionTrail.getLast()).magnitude();
    }

    @Override
    public XMLNodeInfo hashed() {
        HashMap<String, XMLNodeInfo> hashed = new HashMap<>();
        hashed.put("position", getPosition().hashed());
        hashed.put("velocity", getVelocity().hashed());
        return new XMLNodeInfo(hashed);
    }

    public static OrbitalPath fromXML(XMLNodeInfo info) throws XMLParseException {
        try {
            Map<String, XMLNodeInfo> hashed = info.getDataTable();
            Vector2D position = Vector2D.fromXML(hashed.get("position"));
            Vector2D velocity = Vector2D.fromXML(hashed.get("velocity"));
            return new OrbitalPath(position, velocity);
        } catch (XMLParseException | NullPointerException | NumberFormatException e) {
            throw new XMLParseException(XMLParseException.Type.XML_ERROR);
        }
    }
}
