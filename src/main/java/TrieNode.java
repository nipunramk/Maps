import java.util.ArrayList;

/**
 * Created by nipun.ramk on 4/18/2016.
 */
public class TrieNode {
    private String locationName;
    private String locationCleanedName;
    private ArrayList<TrieNode> branches = new ArrayList<>();
    private ArrayList<String> searchResults = new ArrayList<>();

    public TrieNode(String name, String cleanedName) {
        locationName = name;
        locationCleanedName = cleanedName;
    }

    public void insert(String name, String cleanedName) {
        insertAllSubStrings(name, cleanedName);

    }

    private void insertAllSubStrings(String name, String cleanedName) {
        ArrayList<TrieNode> children = branches;
        for (int i = 1; i <= cleanedName.length(); i++) {
            TrieNode node = insertNode(name, cleanedName.substring(0, i),
                    children);
            children = node.branches;
        }

    }

    private TrieNode insertNode(String name, String substr, ArrayList<TrieNode> children) {
        TrieNode currentTrieNode = findTrieNode(substr, children);

        if (currentTrieNode == null) {
            currentTrieNode = new TrieNode(name, substr);
            children.add(currentTrieNode);
        }

        currentTrieNode.searchResults.add(name);
        return currentTrieNode;
    }

//    private void insertNode(String name, TrieNode toInsert, int depth) {
//        if ((depth == 1) && (!branches.contains(toInsert.cleanedName)) {
//            branches.add(toInsert);
//            toInsert.searchResults.add(name);
//        }
//
//        ArrayList<TrieNode> children = branches;
//
//        int count = 1;
//        while (count < depth) {
//            TrieNode nextNode = findTrieNode(toInsert, count, children);
//            count += 1;
//            children = nextNode.branches;
//
//        }
//
//    }

    public TrieNode findTrieNode(String currentString,  ArrayList<TrieNode> children) {
        for (TrieNode child : children) {
            if (child.locationCleanedName.equals(currentString)) {
                return child;
            }
        }

        return null;

    }

//    private boolean containsString(String s, ArrayList<TrieNode> children) {
//        for (TrieNode node : children) {
//            if (node.cleanedName.equals(s)) {
//                return true;
//            }
//        }
//
//        return false;
//    }

    public ArrayList<String> getSearchResults(String cleanedName) {
        ArrayList<TrieNode> children = branches;
        TrieNode node = new TrieNode("", "");
        for (int i = 1; i <= cleanedName.length(); i++) {
            node = findTrieNode(cleanedName.substring(0, i), children);
            if (node == null) {
                return new ArrayList<>();
            }
            children = node.branches;
        }

        return node.searchResults;


    }

//    public static void main(String[] args) {
//        String test = "hello";
//
//        TrieNode test1 = new TrieNode("", "");
//        test1.insert("hello", "hello");
//        test1.insert("sam", "sam");
//        test1.insert("halil", "halil");
//        test1.insert("Same", "same");
//        test1.insert("StarBuck's", "starbucks");
//
//        System.out.println(test1.getSearchResults("sa"));
//
//
//    }
}
