package AstroSim.model.xml;

public class XMLParseException extends Throwable {
    public static final short XML_ERROR = 0;
    public static final short TAG_NOT_FOUND = 1;
    public static final short IO_EXCEPTION = 2;
    public static final short CANNOT_FIND_FILE = 3;
    public static final short WRONG_NODE_TYPE = 4;
    private final short type;

    public XMLParseException(short type) {
        this(type, "");
    }

    public XMLParseException(short type, String message) {
        super(message);
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
