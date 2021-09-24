package AstroSim.model.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class XMLList {
    public static XMLNodeInfo hashed(List<XMLNodeInfo> list) {
        HashMap<String, XMLNodeInfo> hashed = new HashMap<>();
        hashed.put("size", new XMLNodeInfo(list.size()));
        for (int i = 0; i < list.size(); i++) {
            hashed.put(String.valueOf(i), list.get(i));
        }
        return new XMLNodeInfo(hashed);
    }

    public static List<XMLNodeInfo> fromXML(XMLNodeInfo hashed) throws XMLParseException {
        try {
            List<XMLNodeInfo> list = new ArrayList<>();
            HashMap<String, XMLNodeInfo> data = hashed.getDataTable();
            int size = Integer.parseInt(data.get("size").getValue());
            for (int i = 0; i < size; i++) {
                list.add(data.get(String.valueOf(i)));
            }
            return list;
        } catch (XMLParseException | NumberFormatException e) {
            throw new XMLParseException(XMLParseException.XML_ERROR);
        }
    }
}
