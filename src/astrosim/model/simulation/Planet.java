package astrosim.model.simulation;

import astrosim.model.math.Vector2D;
import astrosim.model.xml.XMLHashable;
import astrosim.model.xml.XMLNodeInfo;
import astrosim.model.xml.XMLParseException;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Planet implements XMLHashable {
    // A Planet. Constructed by Scenario.
    private String name;
    private boolean isStatic;
    private double mass;
    private double radius;
    private final OrbitalPath path;
    private String color;
    private String trailColor;
    private Runnable updateListener;

    private static final char[] converter = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    @SuppressWarnings("java:S2119")
    private static String getRandomColor() {
        StringBuilder result = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < 6; i++) {
            result.append(converter[r.nextInt(16)]);
        }
        return "#" + result;
    }

    @SuppressWarnings("java:S107")
    public Planet(OrbitalPath path, double mass, double radius, boolean isStatic, String name, String color, String trailColor) {
        this.mass = mass;
        this.radius = radius;
        this.isStatic = isStatic;
        this.name = name;
        this.path = path;
        this.color = color;
        this.trailColor = trailColor;
        this.path.setPlanet(this);
    }

    public Planet() {
        this(new OrbitalPath(), 1e18, 20, false, "Planet", getRandomColor(), getRandomColor());
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

    public String getColor() {
        return color;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public String getTrailColor() {
        return trailColor;
    }

    public void setTrailColor(String trailColor) {
        this.trailColor = trailColor;
        updateListener.run();
    }

    public void setColor(String color) {
        this.color = color;
        updateListener.run();
    }

    public void setMass(double mass) {
        this.mass = mass;
        updateListener.run();
    }

    public void setPosition(Vector2D position, Vector2D velocity) {
        path.setPosition(position, velocity);
        updateListener.run();
    }

    public void setRadius(double radius) {
        this.radius = radius;
        updateListener.run();
    }

    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
        path.getTrail().setShowing(!isStatic);
        updateListener.run();
    }

    public void setName(String name) {
        this.name = name;
        updateListener.run();
    }

    public void setUpdateListener(Runnable updateListener) {
        this.updateListener = updateListener;
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
        hashed.put("color", new XMLNodeInfo(color));
        hashed.put("trailColor", new XMLNodeInfo(trailColor));
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
            String color = data.get("color").getValue();
            String trailColor = data.get("trailColor").getValue();
            return new Planet(path, mass, radius, isStatic, name, color, trailColor);
        } catch (XMLParseException | NullPointerException | NumberFormatException e) {
            throw new XMLParseException(XMLParseException.Type.XML_ERROR);
        }
    }
}
