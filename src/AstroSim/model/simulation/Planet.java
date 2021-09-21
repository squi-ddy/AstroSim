package AstroSim.model.simulation;

import AstroSim.model.math.Vector;
import AstroSim.model.xml.XMLHashable;
import AstroSim.model.xml.XMLNodeInfo;
import AstroSim.model.xml.XMLParseException;

import java.util.HashMap;

public class Planet implements XMLHashable {
    // A Planet. Constructed by Scenario.
    private String name;
    private boolean isStatic;
    private Vector velocity;
    private Vector position;
    private double mass;
    private double radius;
    private OrbitalPath path;

    public Planet(Vector position, double mass, double radius, Vector velocity, boolean isStatic, String name) {
        this.position = position;
        this.mass = mass;
        this.radius = radius;
        this.velocity = velocity;
        this.isStatic = isStatic;
        this.name = name;
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
        return position;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public OrbitalPath getPath() {return path;}

    public boolean isStatic() {
        return isStatic;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public void setPosition(Vector position) {
        this.position = position;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(OrbitalPath path) {this.path = path;}

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
        hashed.put("position", position.hashed());
        hashed.put("velocity", velocity.hashed());
        return new XMLNodeInfo(hashed);
    }

    @Override
    public void fromXML(XMLNodeInfo info) throws XMLParseException {
        try {
            HashMap<String, XMLNodeInfo> data = info.getDataTable();
            mass = Double.parseDouble(data.get("mass").getValue());
            isStatic = Boolean.parseBoolean(data.get("static").getValue());
            radius = Double.parseDouble(data.get("radius").getValue());
            name = data.get("name").getValue();
            position = new Vector();
            position.fromXML(new XMLNodeInfo(data.get("position").getDataTable()));
            velocity = new Vector();
            velocity.fromXML(new XMLNodeInfo(data.get("velocity").getDataTable()));
        } catch (XMLParseException | NullPointerException | NumberFormatException e) {
            throw new XMLParseException(XMLParseException.XML_ERROR);
        }
    }

}
