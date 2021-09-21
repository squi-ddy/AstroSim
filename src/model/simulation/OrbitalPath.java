package model.simulation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableArrayBase;
import javafx.collections.ObservableList;
import javafx.scene.shape.Polyline;
import model.math.Vector;
import model.xml.XMLHashable;
import model.xml.XMLNodeInfo;
import model.xml.XMLParseException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class OrbitalPath implements XMLHashable {
    // Stores tracers.
    private Deque<Vector> positionBuffer;
    private Deque<Vector> velocityBuffer;
    private Deque<Vector> positionTrail;
    private static int maxLength = 0;
    private static int maxBufferLength = 0;

    public static void setMaxLength(int maxLength) {
        OrbitalPath.maxLength = maxLength;
    }

    public static void setMaxBufferLength(int maxBufferLength) {
        OrbitalPath.maxBufferLength = maxBufferLength;
    }

    public OrbitalPath(List<Vector> position, List<Vector> velocity, Vector initialPos, Vector initialVel) {
        this.positionBuffer = new ArrayDeque<>(position);
        this.velocityBuffer = new ArrayDeque<>(velocity);
        positionBuffer.addFirst(initialPos);
        velocityBuffer.addFirst(initialVel);
    }

    public OrbitalPath() {
        this(new ArrayList<>(), new ArrayList<>(), new Vector(), new Vector());
    }

    public void addPoint(Vector pos, Vector vel) {
        if (positionBuffer.size() < maxBufferLength) {
            positionBuffer.add(pos);
            velocityBuffer.add(vel);
        }
    }

    public boolean isBufferFull() {
        return positionBuffer.size() >= maxBufferLength;
    }

    public void clearBuffer() {
        Vector pos = positionBuffer.getFirst();
        Vector vel = velocityBuffer.getFirst();
        positionBuffer.clear();
        velocityBuffer.clear();
        positionBuffer.add(pos);
        velocityBuffer.add(vel);
    }

    public Vector getLatestVelocity() {
        return velocityBuffer.getLast();
    }

    public Vector getLatestPosition() {
        return positionBuffer.getLast();
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

    @Override
    public XMLNodeInfo hashed() {
        return null;
    }

    @Override
    public void fromXML(XMLNodeInfo info) throws XMLParseException {

    }
}
