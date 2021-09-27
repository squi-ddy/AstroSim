package astrosim.model.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLList {
    private XMLList() {}

    public static XMLNodeInfo hashed(List<XMLNodeInfo> list) {
        HashMap<String, XMLNodeInfo> hashed = new HashMap<>();
        hashed.put("size", new XMLNodeInfo(String.valueOf(list.size())));
        for (int i = 0; i < list.size(); i++) {
            hashed.put("elem" + i, list.get(i));
        }
        return new XMLNodeInfo(hashed);
    }

    public static List<XMLNodeInfo> fromXML(XMLNodeInfo hashed) throws XMLParseException {
        try {
            List<XMLNodeInfo> list = new ArrayList<>();
            Map<String, XMLNodeInfo> data = hashed.getDataTable();
            int size = Integer.parseInt(data.get("size").getValue());
            for (int i = 0; i < size; i++) {
                list.add(data.get("elem" + i));
            }
            return list;
        } catch (XMLParseException | NumberFormatException e) {
            throw new XMLParseException(XMLParseException.Type.XML_ERROR);
        }
    }
}
