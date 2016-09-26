import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Wraps the parsing functionality of the MapDBHandler as an example.
 * You may choose to add to the functionality of this class if you wish.
 *
 * @author Alan Yao
 */
public class GraphDB {
    /**
     * Example constructor shows how to create and start an XML parser.
     *
     * @param db_path Path to the XML file to be parsed.
     */

    private HashMap<String, GraphNode> nodes = new HashMap<>();
    private HashSet<String> nodeIds = new HashSet<>();
    private Trie trie = new Trie();

    public GraphDB(String dbPath) {
        try {
            File inputFile = new File(dbPath);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            MapDBHandler maphandler = new MapDBHandler(this);
            saxParser.parse(inputFile, maphandler);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     *
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     * Remove nodes with no connections from the graph.
     * While this does not guarantee that any two nodes in the remaining graph are connected,
     * we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        for (String id : nodeIds) {
            if (nodes.get(id).isDisconnected()) {
                nodes.remove(id);
            }

        }
    }

    public void setNodes(HashMap<String, GraphNode> mapNodes) {
        nodes = mapNodes;
    }

    public HashMap<String, GraphNode> getNodes() {
        return nodes;
    }

    public HashSet<String> getNodeIds() {
        return nodeIds;
    }

    public void addName(String name) {
        trie.addName(name, cleanString(name));

    }

    public ArrayList<String> getSearchResults(String prefix) {
        return trie.getSearchResults(prefix);
    }




}
