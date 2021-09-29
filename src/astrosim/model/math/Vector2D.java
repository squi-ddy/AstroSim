package astrosim.model.math;

import astrosim.model.xml.XMLHashable;
import astrosim.model.xml.XMLNodeInfo;
import astrosim.model.xml.XMLParseException;
import javafx.geometry.Point2D;

import java.util.HashMap;
import java.util.Map;

public class Vector2D implements XMLHashable {
    private final double x;
    private final double y;

    public static final Vector2D EMPTY = new Vector2D();

    public Vector2D() {
        this(0, 0);
    }

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D add(Vector2D v2) {
        return new Vector2D(x + v2.x, y + v2.y);
    }

    public Vector2D sub(Vector2D v2) {
        return new Vector2D(x - v2.x, y - v2.y);
    }

    public Vector2D multiply(double c) {
        return new Vector2D(x * c, y * c);
    }

    public double dot(Vector2D v2) {
        return (x * v2.x + y * v2.y);
    }

    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector2D normalise() {
        return new Vector2D(x / magnitude(), y / magnitude());
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Point2D toPoint2D() {
        return new Point2D(x, y);
    }

    @Override
    public XMLNodeInfo hashed() {
        HashMap<String, XMLNodeInfo> hashed = new HashMap<>();
        hashed.put("x", new XMLNodeInfo(String.valueOf(x)));
        hashed.put("y", new XMLNodeInfo(String.valueOf(y)));
        return new XMLNodeInfo(hashed);
    }

    public static Vector2D fromXML(XMLNodeInfo info) throws XMLParseException {
        try {
            Map<String, XMLNodeInfo> data = info.getDataTable();
            double x = Double.parseDouble(data.get("x").getValue());
            double y = Double.parseDouble(data.get("y").getValue());
            return new Vector2D(x, y);
        } catch (XMLParseException | NullPointerException | NumberFormatException e) {
            throw new XMLParseException(XMLParseException.Type.XML_ERROR);
        }
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Vector2D other) {
            return other.getX() == this.x && other.getY() == this.y;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (int) Math.round(x * y);
    }
}
