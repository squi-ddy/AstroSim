package astrosim.model.simulation;

import astrosim.model.math.Vector2D;
import astrosim.model.xml.XMLHashable;
import astrosim.model.xml.XMLNodeInfo;
import astrosim.model.xml.XMLParseException;

import java.util.HashMap;
import java.util.Map;

public class Planet implements XMLHashable {
    // A Planet. Constructed by Scenario.
    private String name;
    private boolean isStatic;
    private double mass;
    private double radius;
    private final OrbitalPath path;

    public Planet(Vector2D position, Vector2D velocity, double mass, double radius, boolean isStatic, String name) {
        this.mass = mass;
        this.radius = radius;
        this.isStatic = isStatic;
        this.name = name;
        this.path = new OrbitalPath(position, velocity);
    }

    public Planet(Vector2D position, Vector2D velocity, double mass, double radius, boolean isStatic) {
        this(position, velocity, mass, radius, isStatic, "Planet");
    }

    public Planet(Vector2D position, Vector2D velocity, double mass, double radius) {
        this(position, velocity, mass, radius, false);
    }

    public Planet(Vector2D position, Vector2D velocity, double mass) {
        this(position, velocity, mass, 1e6);
    }

    public Planet(Vector2D position, Vector2D velocity) {
        this(position, velocity, 1e20);
    }

    public Planet() {
        this(new Vector2D(), new Vector2D());
    }

    public double getMass() {
        return mass;
    }

    public double getRadius() {
        return radius;
    }

    public Vector2D getPosition() {
        return path.getPosition();
    }

    public Vector2D getVelocity() {
        return path.getVelocity();
    }

    public OrbitalPath getPath() {return path;}

    public boolean isStatic() {
        return isStatic;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public void setPosition(Vector2D position, Vector2D velocity) {
        path.setPosition(position, velocity);
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public XMLNodeInfo hashed() {
        HashMap<String, XMLNodeInfo> hashed = new HashMap<>();
        hashed.put("mass", new XMLNodeInfo(String.valueOf(mass)));
        hashed.put("static", new XMLNodeInfo(String.valueOf(isStatic)));
        hashed.put("radius", new XMLNodeInfo(String.valueOf(radius)));
        hashed.put("name", new XMLNodeInfo(String.valueOf(name)));
        hashed.put("path", path.hashed());
        return new XMLNodeInfo(hashed);
    }

    public static Planet fromXML(XMLNodeInfo info) throws XMLParseException {
        try {
            Map<String, XMLNodeInfo> data = info.getDataTable();
            double mass = Double.parseDouble(data.get("mass").getValue());
            boolean isStatic = Boolean.parseBoolean(data.get("static").getValue());
            double radius = Double.parseDouble(data.get("radius").getValue());
            String name = data.get("name").getValue();
            OrbitalPath path = OrbitalPath.fromXML(data.get("path"));
            return new Planet(path.getPosition(), path.getVelocity(), mass, radius, isStatic, name);
        } catch (XMLParseException | NullPointerException | NumberFormatException e) {
            throw new XMLParseException(XMLParseException.XML_ERROR);
        }
    }

}
