package AstroSim.model.math;

import AstroSim.model.xml.XMLHashable;
import AstroSim.model.xml.XMLNodeInfo;
import AstroSim.model.xml.XMLParseException;

import java.util.HashMap;

public class Vector implements XMLHashable {
    private double x;
    private double y;

    public Vector() {
        this.x = 0;
        this.y = 0;
    }

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector add(Vector v2) {
        return new Vector(x + v2.x, y + v2.y);
    }

    public Vector sub(Vector v2) {
        return new Vector(x - v2.x, y - v2.y);
    }

    public Vector multiply(Vector v2) {
        return new Vector(x * v2.x, y * v2.y);
    }

    public Vector multiply(double c) {
        return new Vector(x * c, y * c);
    }

    public double dot(Vector v2) {
        return (x * v2.x + y * v2.y);
    }

    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector normalised() {
        return new Vector(x / magnitude(), y / magnitude());
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
        hashed.put("x", new XMLNodeInfo(x));
        hashed.put("y", new XMLNodeInfo(y));
        return new XMLNodeInfo(hashed);
    }

    @Override
    public void fromXML(XMLNodeInfo info) throws XMLParseException {
        try {
            HashMap<String, XMLNodeInfo> data = info.getDataTable();
            x = Double.parseDouble(data.get("x").getValue());
            y = Double.parseDouble(data.get("y").getValue());
        } catch (XMLParseException | NullPointerException | NumberFormatException e) {
            throw new XMLParseException(XMLParseException.XML_ERROR);
        }
    }
}
