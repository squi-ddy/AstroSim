package model.xml;

import java.util.HashMap;

public class XMLNodeInfo {
    public static final short HAS_CHILDREN = 0;
    public static final short HAS_VALUE = 1;
    private HashMap<String, XMLNodeInfo> dataTable;
    private String value;
    private final short nodeType;

    public XMLNodeInfo(HashMap<String, XMLNodeInfo> info) {
        this.nodeType = HAS_CHILDREN;
        this.dataTable = info;
    }

    public XMLNodeInfo(Object info) {
        this.nodeType = HAS_VALUE;
        this.value = String.valueOf(info);
    }

    public short getNodeType() {
        return nodeType;
    }

    public HashMap<String, XMLNodeInfo> getDataTable() throws XMLParseException {
        if (nodeType == HAS_VALUE) throw new XMLParseException(XMLParseException.WRONG_NODE_TYPE);
        return dataTable;
    }

    public String getValue() throws XMLParseException {
        if (nodeType == HAS_CHILDREN) throw new XMLParseException(XMLParseException.WRONG_NODE_TYPE);
        return value; // Should be casted by anyone who uses this data
    }

    public XMLNodeInfo getKey(String key) throws XMLParseException {
        if (nodeType == HAS_VALUE) throw new XMLParseException(XMLParseException.WRONG_NODE_TYPE);
        return dataTable.get(key);
    }
}
