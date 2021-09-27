package astrosim.model.simulation;

import astrosim.model.math.Vector2D;
import astrosim.model.xml.XMLHashable;
import astrosim.model.xml.XMLNodeInfo;
import astrosim.model.xml.XMLParseException;

import java.util.*;

public class OrbitalPath implements XMLHashable {
    // Stores tracers.
    private final Deque<Vector2D> positionBuffer;
    private final Deque<Vector2D> velocityBuffer;
    private final Deque<Vector2D> positionTrail;
    private final Deque<Vector2D> velocityTrail;
    private static int maxLength = 0;
    private static int maxBufferLength = 0;

    public static void setMaxLength(int maxLength) {
        OrbitalPath.maxLength = maxLength;
    }

    public static void setMaxBufferLength(int maxBufferLength) {
        OrbitalPath.maxBufferLength = maxBufferLength;
    }

    public OrbitalPath(Vector2D position, Vector2D velocity) {
        this.positionTrail = new ArrayDeque<>(List.of(position));
        this.velocityTrail = new ArrayDeque<>(List.of(velocity));
        this.positionBuffer = new ArrayDeque<>();
        this.velocityBuffer = new ArrayDeque<>();
    }

    public void addPosition(Vector2D pos, Vector2D vel) {
        if (positionBuffer.size() < maxBufferLength) {
            positionBuffer.add(pos);
            velocityBuffer.add(vel);
        }
    }

    public boolean isBufferFull() {
        return positionBuffer.size() >= maxBufferLength;
    }

    public void clearBuffer() {
        positionBuffer.clear();
        velocityBuffer.clear();
    }

    public Vector2D getLatestVelocity() {
        if (!velocityBuffer.isEmpty()) return velocityBuffer.getLast();
        return getVelocity();
    }

    public Vector2D getLatestPosition() {
        if (!positionBuffer.isEmpty()) return positionBuffer.getLast();
        return getPosition();
    }

    public List<Double> getTrail() {
        List<Double> pts = new ArrayList<>();
        for (Vector2D pos : positionTrail) {
            pts.add(pos.getX());
            pts.add(pos.getY());
        }
        return pts;
    }

    public Vector2D getPosition() {
        return positionTrail.getLast();
    }

    public Vector2D getVelocity() {
        return velocityTrail.getLast();
    }

    public void clearTrail() {
        velocityTrail.clear();
        positionTrail.clear();
    }

    public void setPosition(Vector2D position, Vector2D velocity) {
        positionTrail.clear();
        positionBuffer.clear();
        velocityTrail.clear();
        velocityBuffer.clear();
        positionTrail.add(position);
        velocityTrail.add(velocity);
    }

    public void addToTrail(int steps) {
        if (steps > positionBuffer.size()) throw new IllegalArgumentException();
        for (int i = 0; i < steps; i++) {
            positionTrail.add(positionBuffer.getFirst());
            velocityTrail.add(velocityBuffer.getFirst());
            positionBuffer.removeFirst();
            velocityBuffer.removeFirst();
        }
        while (positionTrail.size() > maxLength) {
            positionTrail.removeFirst();
            velocityTrail.removeFirst();
        }
    }

    public Deque<Vector2D> getPositionBuffer() {
        return positionBuffer;
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
