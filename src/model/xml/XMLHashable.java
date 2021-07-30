package model.xml;

public interface XMLHashable {
    XMLNodeInfo hashed();
    void fromXML(XMLNodeInfo info) throws XMLParseException;
}
