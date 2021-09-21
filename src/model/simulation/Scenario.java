package model.simulation;

import model.xml.XMLHashable;
import model.xml.XMLNodeInfo;
import model.xml.XMLParseException;

import java.nio.file.Path;
import java.util.List;

public class Scenario implements XMLHashable {
    // Loads the simulation field.
    // To be implemented
    private double valG;
    private Path filepath;
    private List<Planet> planets;

    public Scenario() {
        valG = 6.67e-11;
        filepath = null;
        planets = new List<>();
    }

    @Override
    public XMLNodeInfo hashed() {
        return null;
    }

    @Override
    public void fromXML(XMLNodeInfo info) throws XMLParseException {

    }
}
