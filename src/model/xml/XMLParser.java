package model.xml;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class XMLParser {
    private final Path filepath;
    private final HashMap<String, XMLNodeInfo> contentCache;

    public XMLParser(Path filepath) throws XMLParseException {
        if (!Files.exists(filepath)) {
            throw new XMLParseException(XMLParseException.CANNOT_FIND_FILE);
        }
        this.filepath = filepath.toAbsolutePath();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document XMLFile = db.parse(new File(this.filepath.toString()));
            XMLFile.getDocumentElement().normalize();
            this.contentCache = recursiveReader(XMLFile.getDocumentElement());
        } catch (ParserConfigurationException | SAXException e) {
            throw new XMLParseException(XMLParseException.XML_ERROR);
        } catch (IOException e) {
            throw new XMLParseException(XMLParseException.IO_EXCEPTION);
        }
    }

    public HashMap<String, XMLNodeInfo> getContent() {
        return contentCache;
    }

    public HashMap<String, XMLNodeInfo> getContent(String[] path) throws XMLParseException {
        HashMap<String, XMLNodeInfo> result = new HashMap<>();
        XMLNodeInfo node = getNodeByPath(path);
        if (node == null) throw new XMLParseException(XMLParseException.TAG_NOT_FOUND);
        result.put(path[path.length - 1], node);
        return result;
    }

    @SuppressWarnings("unchecked")
    public void writeContent(String[] path, XMLNodeInfo info) throws XMLParseException {
        // Writes to cache, not file (fast)
        // File only written upon save()
        XMLNodeInfo parent = getNodeByPath(Arrays.copyOf(path, path.length - 1));
        if (parent == null || parent.getNodeType() != XMLNodeInfo.HAS_CHILDREN) throw new XMLParseException(XMLParseException.TAG_NOT_FOUND);
        HashMap<String, XMLNodeInfo> oldInfo = (HashMap<String, XMLNodeInfo>) parent.getData();
        oldInfo.put(path[path.length - 1], info);
    }

    public void saveXML() throws XMLParseException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.newDocument();

            writer(document, new XMLNodeInfo(contentCache));

            TransformerFactory tFactory =
                    TransformerFactory.newInstance();
            Transformer transformer =
                    tFactory.newTransformer();

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(filepath.toString()));
            transformer.transform(source, result);
        } catch (ParserConfigurationException | TransformerException e) {
            throw new XMLParseException(XMLParseException.XML_ERROR);
        }
    }

    @SuppressWarnings("unchecked")
    private void writer(Node node, XMLNodeInfo nodeStuff) {
        if (nodeStuff.getNodeType() == XMLNodeInfo.HAS_VALUE) node.setNodeValue((String) nodeStuff.getData());
        HashMap<String, XMLNodeInfo> children = (HashMap<String, XMLNodeInfo>) nodeStuff.getData();
        for (String child : children.keySet()) {
            Node childNode = node.getOwnerDocument().createElement(child);
            writer(childNode, children.get(child));
            node.appendChild(childNode);
        }
    }

    private HashMap<String, XMLNodeInfo> recursiveReader(Node node) {
        HashMap<String, XMLNodeInfo> nodes = new HashMap<>();
        NodeList children = (NodeList) node;
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n.hasChildNodes()) {
                nodes.put(n.getNodeName(), new XMLNodeInfo(recursiveReader(n)));
            } else {
                nodes.put(n.getNodeName(), new XMLNodeInfo(n.getNodeValue()));
            }
        }
        return nodes;
    }

    // Java believes that the data cannot be casted to HashMap
    // But the implementation says that it will always be HashMap if its type is HAS_CHILDREN
    @SuppressWarnings("unchecked")
    private XMLNodeInfo getNodeByPath(String[] path) {
        HashMap<String, XMLNodeInfo> layer = contentCache;
        for (int i = 0; i < path.length - 1; i++) {
            String child = path[i];
            XMLNodeInfo childNode = layer.get(child);
            if (childNode == null) return null;
            if (childNode.getNodeType() != XMLNodeInfo.HAS_CHILDREN) return null;
            layer = (HashMap<String, XMLNodeInfo>) childNode.getData();
        }
        return layer.get(path[path.length - 1]);
    }
}
