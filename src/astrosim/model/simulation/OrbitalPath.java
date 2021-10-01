package astrosim.model.simulation;

import astrosim.model.math.Vector2D;
import astrosim.model.xml.XMLHashable;
import astrosim.model.xml.XMLNodeInfo;
import astrosim.model.xml.XMLParseException;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Pair;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class OrbitalPath implements XMLHashable {
    // Stores tracers.
    private final Deque<Vector2D> positionBuffer;
    private final Deque<Vector2D> velocityBuffer;
    private Pair<Vector2D, Vector2D> lastPosVel;
    private static int maxLength = 0;
    private static int maxBufferLength = 0;
    private static int positionGap = 0;
    private int untilNextAdd = positionGap;
    private double trailLength = 0;
    private final Group trail;
    private Planet planet;

    public static void setMaxLength(int maxLength) {
        OrbitalPath.maxLength = maxLength;
    }

    public static void setMaxBufferLength(int maxBufferLength) {
        OrbitalPath.maxBufferLength = maxBufferLength;
    }

    public static void setPositionGap(int positionGap) {
        OrbitalPath.positionGap = positionGap;
    }

    public static int getMaxBufferLength() {
        return maxBufferLength;
    }

    public void setPlanet(Planet planet) {
        this.planet = planet;
    }

    public OrbitalPath(Vector2D position, Vector2D velocity) {
        this.trail = new Group(new Line(position.getX(), position.getY(), position.getX(), position.getY()));
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

    public Group getLine() {
        return trail;
    }

    private void changePoint(Vector2D end) {
        Line lastLine = (Line) trail.getChildren().get(trail.getChildren().size() - 1);
        lastLine.setEndX(end.getX());
        lastLine.setEndY(end.getY());
    }

    private void addPointToTrail(Vector2D end) {
        Line lastLine = (Line) trail.getChildren().get(trail.getChildren().size() - 1);
        lastLine.setEndX(end.getX());
        lastLine.setEndY(end.getY());
        trailLength += new Vector2D(lastLine.getStartX(), lastLine.getStartY()).sub(new Vector2D(lastLine.getEndX(), lastLine.getEndY())).magnitude();
        Line line = new Line(end.getX(), end.getY(), end.getX(), end.getY());
        line.setStrokeLineCap(StrokeLineCap.BUTT);
        line.setStrokeWidth(planet.getRadius() * 0.5);
        line.setStroke(Color.web(planet.getTrailColor()));
        trail.getChildren().add(line);
        double currTrailLength = 0;
        for (Node node : trail.getChildren()) {
            Line currLine = (Line) node;
            double extraLength = new Vector2D(currLine.getStartX(), currLine.getStartY()).sub(new Vector2D(currLine.getEndX(), currLine.getEndY())).magnitude();
            /*currLine.setStroke(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.web(planet.getTrailColor()).deriveColor(1, 1, 1, getTransparency(currTrailLength / trailLength))),
                    new Stop(1, Color.web(planet.getTrailColor()).deriveColor(1, 1, 1, getTransparency((currTrailLength + extraLength) / trailLength)))
            ));*/
            currLine.setStroke(Color.web(planet.getTrailColor()).deriveColor(1, 1, 1, getTransparency(currTrailLength / trailLength)));
            currTrailLength += extraLength;
        }
    }

    private void deletePointFromTrail() {
        Line line = (Line) trail.getChildren().get(0);
        trailLength -= new Vector2D(line.getStartX(), line.getStartY()).sub(new Vector2D(line.getEndX(), line.getEndY())).magnitude();
        trail.getChildren().remove(0);
    }

    private double getTransparency(double interpolate) {
        return interpolate * interpolate;
    }

    public Vector2D getLatestVelocity() {
        if (!velocityBuffer.isEmpty()) return velocityBuffer.getLast();
        return getVelocity();
    }

    public Vector2D getLatestPosition() {
        if (!positionBuffer.isEmpty()) return positionBuffer.getLast();
        return getPosition();
    }

    public Vector2D getPosition() {
        return lastPosVel.getKey();
    }

    public Vector2D getVelocity() {
        return lastPosVel.getValue();
    }

    public void setPosition(Vector2D position, Vector2D velocity) {
        trail.getChildren().clear();
        trail.getChildren().add(new Line(position.getX(), position.getY(), position.getX(), position.getY()));
        positionBuffer.clear();
        velocityBuffer.clear();
        lastPosVel = new Pair<>(position, velocity);
        untilNextAdd = 0;
        trailLength = 0;
    }

    public void addToTrail(int steps) {
        if (steps > positionBuffer.size()) throw new IllegalArgumentException();
        for (int i = 0; i < steps; i++) {
            if (untilNextAdd == 0) {
                addPointToTrail(positionBuffer.getFirst());
                untilNextAdd = positionGap + 1;
            }
            lastPosVel = new Pair<>(positionBuffer.getFirst(), velocityBuffer.getFirst());
            changePoint(positionBuffer.removeFirst());
            velocityBuffer.removeFirst();
            untilNextAdd--;
        }
        while (trail.getChildren().size() > maxLength + 1) {
            deletePointFromTrail();
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
            Map<String, XMLNodeInfo> hashed = info.getDataTable();
            Vector2D position = Vector2D.fromXML(hashed.get("position"));
            Vector2D velocity = Vector2D.fromXML(hashed.get("velocity"));
            return new OrbitalPath(position, velocity);
        } catch (XMLParseException | NullPointerException | NumberFormatException e) {
            throw new XMLParseException(XMLParseException.Type.XML_ERROR);
        }
    }
}
