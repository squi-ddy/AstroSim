package model.xml;

import java.util.HashMap;

public class XMLNodeInfo {
    public static final short HAS_CHILDREN = 0;
    public static final short HAS_VALUE = 1;
    private Object data;
    private short nodeType;

    public XMLNodeInfo(HashMap<String, XMLNodeInfo> info) {
        this.nodeType = HAS_CHILDREN;
        this.data = info;
    }

    public XMLNodeInfo(String info) {
        this.nodeType = HAS_VALUE;
        this.data = info;
    }

    public short getNodeType() {
        return nodeType;
    }

    public Object getData() {
        return data; // Should be casted by anyone who uses this data
    }
}
