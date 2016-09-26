import java.util.ArrayList;


/**
 * Created by nipun.ramk on 4/18/2016.
 */
public class Trie {
    private TrieNode root;



    public Trie() {
        root = new TrieNode("", "");


    }

    public void addName(String name, String cleanedName) {
        root.insert(name, cleanedName);
    }

    public ArrayList<String> getSearchResults(String cleanedName) {
        return root.getSearchResults(cleanedName);
    }

}
