package astrosim.model.xml;

public class XMLParseException extends Exception {
    private final Type type;

    public XMLParseException(Type type) {
        this(type, "");
    }

    public XMLParseException(Type type, String message) {
        super(message);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        XML_ERROR,
        TAG_NOT_FOUND,
        IO_EXCEPTION,
        WRONG_NODE_TYPE,
        READ_ONLY
    }
}
