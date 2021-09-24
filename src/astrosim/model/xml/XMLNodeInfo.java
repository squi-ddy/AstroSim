package astrosim.model.xml;

import java.util.Map;

public class XMLNodeInfo {
    public static final short HAS_CHILDREN = 0;
    public static final short HAS_VALUE = 1;
    private Map<String, XMLNodeInfo> dataTable;
    private String value;
    private final short nodeType;

    public XMLNodeInfo(Map<String, XMLNodeInfo> info) {
        this.nodeType = HAS_CHILDREN;
        this.dataTable = info;
    }

    public XMLNodeInfo(String info) {
        this.nodeType = HAS_VALUE;
        this.value = info;
    }

    public short getNodeType() {
        return nodeType;
    }

    public Map<String, XMLNodeInfo> getDataTable() throws XMLParseException {
        if (nodeType == HAS_VALUE) throw new XMLParseException(XMLParseException.WRONG_NODE_TYPE);
        return dataTable;
    }

    public String getValue() throws XMLParseException {
        if (nodeType == HAS_CHILDREN) throw new XMLParseException(XMLParseException.WRONG_NODE_TYPE);
        return value; // Should be cast by anyone who uses this data
    }
}
