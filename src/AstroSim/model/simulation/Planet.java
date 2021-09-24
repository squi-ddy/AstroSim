package AstroSim.model.simulation;

import AstroSim.model.math.Vector;
import AstroSim.model.xml.XMLHashable;
import AstroSim.model.xml.XMLNodeInfo;
import AstroSim.model.xml.XMLParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Planet implements XMLHashable {
    // A Planet. Constructed by Scenario.
    private String name;
    private boolean isStatic;
    private double mass;
    private double radius;
    private final OrbitalPath path;

    public Planet(Vector position, double mass, double radius, Vector velocity, boolean isStatic, String name) {
        this.mass = mass;
        this.radius = radius;
        this.isStatic = isStatic;
        this.name = name;
        this.path = new OrbitalPath(position, velocity);
    }

    public Planet(Vector position, double mass, double radius, Vector velocity, boolean isStatic) {
        this(position, mass, radius, velocity, isStatic, "Planet");
    }

    public Planet(Vector position, double mass, double radius, Vector velocity) {
        this(position, mass, radius, velocity, false);
    }

    public Planet(Vector position, double mass, double radius) {
        this(position, mass, radius, new Vector());
    }

    public Planet(Vector position, double mass) {
        this(position, mass, 1e6);
    }

    public Planet(Vector position) {
        this(position, 1e20);
    }

    public Planet() {
        this(new Vector());
    }

    public double getMass() {
        return mass;
    }

    public double getRadius() {
        return radius;
    }

    public Vector getPosition() {
        return path.getPosition();
    }

    public Vector getVelocity() {
        return path.getVelocity();
    }

    public OrbitalPath getPath() {return path;}

    public boolean isStatic() {
        return isStatic;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public void setPosition(Vector position, Vector velocity) {
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
        hashed.put("mass", new XMLNodeInfo(mass));
        hashed.put("static", new XMLNodeInfo(isStatic));
        hashed.put("radius", new XMLNodeInfo(radius));
        hashed.put("name", new XMLNodeInfo(name));
        hashed.put("path", path.hashed());
        return new XMLNodeInfo(hashed);
    }

    public static Planet fromXML(XMLNodeInfo info) throws XMLParseException {
        try {
            HashMap<String, XMLNodeInfo> data = info.getDataTable();
            double mass = Double.parseDouble(data.get("mass").getValue());
            boolean isStatic = Boolean.parseBoolean(data.get("static").getValue());
            double radius = Double.parseDouble(data.get("radius").getValue());
            String name = data.get("name").getValue();
            OrbitalPath path = OrbitalPath.fromXML(data.get("path"));
            return new Planet(path.getPosition(), mass, radius, path.getVelocity(), isStatic, name);
        } catch (XMLParseException | NullPointerException | NumberFormatException e) {
            throw new XMLParseException(XMLParseException.XML_ERROR);
        }
    }

}
