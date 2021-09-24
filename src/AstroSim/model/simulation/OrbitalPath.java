package AstroSim.model.simulation;

import javafx.scene.shape.Polyline;
import AstroSim.model.math.Vector;
import AstroSim.model.xml.XMLHashable;
import AstroSim.model.xml.XMLNodeInfo;
import AstroSim.model.xml.XMLParseException;

import java.util.*;

public class OrbitalPath implements XMLHashable {
    // Stores tracers.
    private final Deque<Vector> positionBuffer;
    private final Deque<Vector> velocityBuffer;
    private final Deque<Vector> positionTrail;
    private final Deque<Vector> velocityTrail;
    private static int maxLength = 0;
    private static int maxBufferLength = 0;

    public static void setMaxLength(int maxLength) {
        OrbitalPath.maxLength = maxLength;
    }

    public static void setMaxBufferLength(int maxBufferLength) {
        OrbitalPath.maxBufferLength = maxBufferLength;
    }

    public OrbitalPath(Vector position, Vector velocity) {
        this.positionTrail = new ArrayDeque<>(List.of(position));
        this.velocityTrail = new ArrayDeque<>(List.of(velocity));
        this.positionBuffer = new ArrayDeque<>();
        this.velocityBuffer = new ArrayDeque<>();
    }

    public OrbitalPath() {
        this(new Vector(), new Vector());
    }

    public void addPosition(Vector pos, Vector vel) {
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

    public Vector getLatestVelocity() {
        if (velocityBuffer.size() > 0) return velocityBuffer.getLast();
        return getVelocity();
    }

    public Vector getLatestPosition() {
        if (positionBuffer.size() > 0) return positionBuffer.getLast();
        return getPosition();
    }

    public Polyline getTrail() {
        List<Double> pts = new ArrayList<>();
        for (Vector pos : positionTrail) {
            pts.add(pos.getX());
            pts.add(pos.getY());
        }
        Polyline pl = new Polyline();
        pl.getPoints().addAll(pts);
        return pl;
    }

    public Vector getPosition() {
        return positionTrail.getLast();
    }

    public Vector getVelocity() {
        return velocityTrail.getLast();
    }

    public void clearTrail() {
        velocityTrail.clear();
        positionTrail.clear();
    }

    public void setPosition(Vector position, Vector velocity) {
        positionTrail.clear();
        positionBuffer.clear();
        velocityTrail.clear();
        velocityBuffer.clear();
        positionTrail.add(position);
        velocityTrail.add(velocity);
    }

    public void addToTrail() {
        positionTrail.add(positionBuffer.getFirst());
        velocityTrail.add(velocityBuffer.getFirst());
        positionBuffer.removeFirst();
        velocityBuffer.removeFirst();
        if (positionTrail.size() > maxLength) {
            positionTrail.removeFirst();
            velocityTrail.removeFirst();
        }
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
            HashMap<String, XMLNodeInfo> hashed = info.getDataTable();
            Vector position = Vector.fromXML(hashed.get("position"));
            Vector velocity = Vector.fromXML(hashed.get("velocity"));
            return new OrbitalPath(position, velocity);
        } catch (XMLParseException | NullPointerException | NumberFormatException e) {
            throw new XMLParseException(XMLParseException.XML_ERROR);
        }
    }
}
