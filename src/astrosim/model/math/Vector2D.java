package astrosim.model.math;

import astrosim.model.xml.XMLHashable;
import astrosim.model.xml.XMLNodeInfo;
import astrosim.model.xml.XMLParseException;

import java.util.HashMap;
import java.util.Map;

public class Vector2D implements XMLHashable {
    private final double x;
    private final double y;

    public Vector2D() {
        this.x = 0;
        this.y = 0;
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
}
