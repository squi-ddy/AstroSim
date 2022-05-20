package astrosim.model.math;

import astrosim.model.xml.XMLHashable;
import astrosim.model.xml.XMLNodeInfo;
import astrosim.model.xml.XMLParseException;
import javafx.geometry.Point2D;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Vec2 implements XMLHashable {
    private final double x;
    private final double y;
    private Double magnitude = null;

    public Vec2() {
        this(0, 0);
    }

    public Vec2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vec2 add(@NotNull Vec2 v2) {
        return new Vec2(x + v2.x, y + v2.y);
    }

    public Vec2 sub(@NotNull Vec2 v2) {
        return new Vec2(x - v2.x, y - v2.y);
    }

    public Vec2 mul(double c) {
        return new Vec2(x * c, y * c);
    }

    public Vec2 div(double c) { return new Vec2(x / c, y / c); }

    public double dot(Vec2 v2) {
        return (x * v2.x + y * v2.y);
    }

    public double magnitude() {
        if (magnitude == null) {
            magnitude = Math.sqrt(x * x + y * y);
        }
        return magnitude;
    }

    public Vec2 normalise() {
        return new Vec2(x / magnitude(), y / magnitude());
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

    public static Vec2 fromXML(XMLNodeInfo info) throws XMLParseException {
        try {
            Map<String, XMLNodeInfo> data = info.getDataTable();
            double x = Double.parseDouble(data.get("x").getValue());
            double y = Double.parseDouble(data.get("y").getValue());
            return new Vec2(x, y);
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
        if (o instanceof Vec2 other) {
            return other.getX() == this.x && other.getY() == this.y;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (int) Math.round(x * y);
    }
}
