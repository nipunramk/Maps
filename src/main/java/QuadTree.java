import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;


/**
 * Created by nipun.ramk on 4/10/2016.
 */
public class QuadTree {

    private Node root;
    private Node traversalPointer;
    private ArrayDeque<Integer> nodeNumbers;
    private ArrayDeque<Node> nodes;


    public class Node implements Comparable<Node> {
        private Node upperLeft;
        private Node upperRight;
        private Node lowerLeft;
        private Node lowerRight;
        private Node parentNode;
        private double upperLeftLon;
        private double upperLeftLat;
        private double lowerRightLon;
        private double lowerRightLat;
        private String file;

        private Node(double uLeftLon, double uLeftLat, double lRightLon,
                     double lRightLat, String f) {
            upperLeftLon = uLeftLon;
            upperLeftLat = uLeftLat;
            lowerRightLon = lRightLon;
            lowerRightLat = lRightLat;
            file = f;
            upperLeft = null;
            upperRight = null;
            lowerLeft = null;
            lowerRight = null;
            parentNode = null;
        }

        @Override
        public int compareTo(Node other) {
            if (this.upperLeftLat > other.upperLeftLat) {
                return -1;
            } else if (this.upperLeftLat < other.upperLeftLat) {
                return 1;
            } else if (this.upperLeftLon < other.upperLeftLon) {
                return -1;
            } else if (this.upperLeftLon > other.upperLeftLon) {
                return 1;
            }

            return 0;
        }
    }

    public QuadTree(double uLLon, double uLLat, double lRlon, double lRlat) {

        root = new Node(uLLon, uLLat, lRlon, lRlat, "root");
        traversalPointer = root;
        nodeNumbers = new ArrayDeque<Integer>();
        nodes = new ArrayDeque<Node>();
        nodeNumbers.add(0);
        nodes.add(root);
        setAllChildren(root);


    }

    public Node getRoot() {
        return root;
    }

    public boolean hasIntersection(Node currentRoot, double queryUllon, double queryUllat,
                                   double queryLrlon, double queryLrlat) {

        return (getUllon(currentRoot) < queryLrlon && getUllat(currentRoot) > queryLrlat
                && getLrlon(currentRoot) > queryUllon && getLrlat(currentRoot) < queryUllat);
    }

    public boolean satisfiesDepth(Node currentRoot, double queryDepth) {
        return calcDppTile(currentRoot.upperLeftLon, currentRoot.lowerRightLon) <= queryDepth;
    }

    public double calcDppTile(double ullon, double lrlon) {
        return (lrlon - ullon) / 256;
    }

    public double calcDppNode(Node currentRoot) {
        return (currentRoot.lowerRightLon - currentRoot.upperLeftLon) / 256;
    }

    public ArrayList getChildren(Node currentRoot) {
        ArrayList<Node> children = new ArrayList<>(4);
        children.add(currentRoot.upperLeft);
        children.add(currentRoot.upperRight);
        children.add(currentRoot.lowerLeft);
        children.add(currentRoot.lowerRight);
        return children;
    }

    public double getUllon(Node currentRoot) {
        return currentRoot.upperLeftLon;
    }

    public double getUllat(Node currentRoot) {
        return currentRoot.upperLeftLat;
    }

    public double getLrlon(Node currentRoot) {
        return currentRoot.lowerRightLon;
    }

    public double getLrlat(Node currentRoot) {
        return currentRoot.lowerRightLat;
    }

    public int getDepth(Node currentRoot) {
        if (currentRoot.file.equals("root")) {
            return 1;
        } else {
            return currentRoot.file.length();
        }
    }

    public String getFileName(Node currentRoot) {
        return currentRoot.file;
    }

    private void setAllChildren(Node currentRoot) {


        while (nodeNumbers.getLast() < 4444444) {
            setChildren(nodes.removeFirst(), nodeNumbers.removeFirst());
        }

//        setAllChildren(currentRoot.upperLeft, nodeNumbers.removeFirst());
//        setAllChildren(currentRoot.upperRight, nodeNumbers.removeFirst());
//        setAllChildren(currentRoot.lowerLeft, nodeNumbers.removeFirst());
//        setAllChildren(currentRoot.lowerLeft, nodeNumbers.removeFirst());

    }

    //
    private void setChildren(Node currentRoot, int parent) {
        int fileInt1 = parent * 10 + 1;
        nodeNumbers.add(fileInt1);
        String fileName1 = Integer.toString(fileInt1);
        currentRoot.upperLeft = createUpperLeftNode(currentRoot, fileName1);
        nodes.add(currentRoot.upperLeft);
        int fileInt2 = parent * 10 + 2;
        nodeNumbers.add(fileInt2);
        String fileName2 = Integer.toString(fileInt2);
        currentRoot.upperRight = createUpperRightNode(currentRoot, fileName2);
        nodes.add(currentRoot.upperRight);
        int fileInt3 = parent * 10 + 3;
        nodeNumbers.add(fileInt3);
        String fileName3 = Integer.toString(fileInt3);
        currentRoot.lowerLeft = createLowerLeftNode(currentRoot, fileName3);
        nodes.add(currentRoot.lowerLeft);
        int fileInt4 = parent * 10 + 4;
        nodeNumbers.add(fileInt4);
        String fileName4 = Integer.toString(fileInt4);
        currentRoot.lowerRight = createLowerRightNode(currentRoot, fileName4);
        nodes.add(currentRoot.lowerRight);
    }

//    private static ArrayList splitIntoDigits(int fileNumber) {
//        int n = fileNumber;
//        ArrayList<Integer> split = new ArrayList<Integer>();
//
//        while (n > 0) {
//            split.add(0, n % 10);
//            n = n / 10;
//        }
//
//        return split;
//    }

    private static File convertStringToFile(String fileName) {

        String filename = "img/" + fileName + ".png";
        File f = new File(filename);
        return f;

    }
//    private void setAllChildren(Node currentRoot, File[] files, int index) {
//
//        try {
//
////            if (index == 2390) {
////                int x = 10;
////            }
//
//            File fileName = files[index];
//            String fileString = getFileString(fileName);
//            String currentFileString = getFileString(currentRoot.file); // currentRoot file
//            if (fileString.equals("root")) {
//                return;
//            }
//
//            int fileNumber = convertFileToInt(fileString);
//
//            if (currentFileString.equals("root")) {
//                if (fileNumber == 1) {
//                    currentRoot.upperLeft = createUpperLeftNode(currentRoot, fileName);
//                    setAllChildren(currentRoot.upperLeft, files, index + 1);
//                } else if (fileNumber == 2) {
//                    currentRoot.upperRight = createUpperRightNode(currentRoot, fileName);
//                    setAllChildren(currentRoot.upperRight, files, index + 1);
//                } else if (fileNumber == 3) {
//                    currentRoot.lowerLeft = createLowerLeftNode(currentRoot, fileName);
//                    setAllChildren(currentRoot.lowerLeft, files, index + 1);
//                } else if (fileNumber == 4) {
//                    currentRoot.lowerRight = createLowerRightNode(currentRoot, fileName);
//                    setAllChildren(currentRoot.lowerRight, files, index + 1);
//                }
//                return;
//
//            }
//
//            int currentFileNumber = convertFileToInt(currentFileString);
//
//            if (fileNumber < 10 * currentFileNumber) {
//                setAllChildren(currentRoot.parentNode, files, index);
//            }
//
//            if (fileNumber % 5 == 1 && fileNumber == currentFileNumber * 10 + 1) {
//                currentRoot.upperLeft = createUpperLeftNode(currentRoot, fileName);
//                setAllChildren(currentRoot.upperLeft, files, index + 1);
//            } else if (fileNumber % 5 == 2 && fileNumber == currentFileNumber * 10 + 2) {
//                currentRoot.upperRight = createUpperRightNode(currentRoot, fileName);
//                setAllChildren(currentRoot.upperRight, files, index + 1);
//
//            } else if (fileNumber % 5 == 3 && fileNumber == currentFileNumber * 10 + 3) {
//                currentRoot.lowerLeft = createLowerLeftNode(currentRoot, fileName);
//                setAllChildren(currentRoot.lowerLeft, files, index + 1);
//            } else if (fileNumber % 5 == 4 && fileNumber == currentFileNumber * 10 + 4) {
//                currentRoot.lowerRight = createLowerRightNode(currentRoot, fileName);
//                setAllChildren(currentRoot.lowerRight, files, index + 1);
//            }
//
//        } catch (StackOverflowError t) {
//            System.out.println(index);
//            t.printStackTrace();
//        }
//
//
//
//    }

    private Node createUpperLeftNode(Node currentRoot, String fileName) {
        double centerLon = (currentRoot.upperLeftLon + currentRoot.lowerRightLon) / 2;
        double centerLat = (currentRoot.upperLeftLat + currentRoot.lowerRightLat) / 2;
        Node upperLeftNode = new Node(currentRoot.upperLeftLon,
                currentRoot.upperLeftLat, centerLon, centerLat, fileName);
        upperLeftNode.parentNode = currentRoot;
        return upperLeftNode;
    }

    private Node createUpperRightNode(Node currentRoot, String fileName) {
        double centerLon = (currentRoot.upperLeftLon + currentRoot.lowerRightLon) / 2;
        double centerLat = (currentRoot.upperLeftLat + currentRoot.lowerRightLat) / 2;
        Node upperRightNode = new Node(centerLon, currentRoot.upperLeftLat,
                currentRoot.lowerRightLon, centerLat, fileName);
        upperRightNode.parentNode = currentRoot;
        return upperRightNode;

    }

    private Node createLowerLeftNode(Node currentRoot, String fileName) {
        double centerLon = (currentRoot.upperLeftLon + currentRoot.lowerRightLon) / 2;
        double centerLat = (currentRoot.upperLeftLat + currentRoot.lowerRightLat) / 2;
        Node lowerLeftNode = new Node(currentRoot.upperLeftLon, centerLat,
                centerLon, currentRoot.lowerRightLat, fileName);
        lowerLeftNode.parentNode = currentRoot;
        return lowerLeftNode;
    }

    private Node createLowerRightNode(Node currentRoot, String fileName) {
        double centerLon = (currentRoot.upperLeftLon + currentRoot.lowerRightLon) / 2;
        double centerLat = (currentRoot.upperLeftLat + currentRoot.lowerRightLat) / 2;
        Node lowerRightNode = new Node(centerLon, centerLat,
                currentRoot.lowerRightLon, currentRoot.lowerRightLat, fileName);
        lowerRightNode.parentNode = currentRoot;
        return lowerRightNode;
    }

    public Node makeTestNode(double ullon, double ullat, double lrlon, double lrlat) {
        return new Node(ullon, ullat, lrlon, lrlat, "");
    }


//    private String getFileString(File f) {
//        String fileName = f.getName();
//        String fileString = fileName.substring(0, fileName.length() - 4);
//        return fileString;
//
//    }
//
//    private int convertFileToInt(String fileName) {
//        return Integer.parseInt(fileName);
//    }

    public static void main(String[] args) {
        File file = new File("img/");
        File[] files = file.listFiles();

        System.out.println(files[files.length - 2]);
//
//        System.out.println(convertIntToFile(1234332));

        QuadTree q = new QuadTree(MapServer.ROOT_ULLON, MapServer.ROOT_ULLAT,
                MapServer.ROOT_LRLON, MapServer.ROOT_LRLAT);

//        Node testTile = q.makeTestNode(-3.0, 4.0, 0.0, 0.0);
//        System.out.println(q.hasIntersection(testTile, -10.0, 1.0, -5.0, -3.0));


//        ArrayList test = splitIntoDigits();

//        System.out.println(test);


//
//        for (int i= 2400; i < 2700; i++) {
//            System.out.println(files[i]);
//        }
////
//        System.out.println(files[2434]);
//
//        String filename = files[files.length - 3].getName();
//        String fileString = filename.substring(0, filename.length() - 4);
//        System.out.println(fileString);
//
//        int fileNumber = Integer.parseInt(fileString);
//        System.out.println(fileNumber);
    }


}
