import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;


/**
 * Created by nipun.ramk on 4/15/2016.
 */
public class GraphNode implements Comparable<GraphNode> {
    private long id;
    private double latitude;
    private double longitude;
    private String name;
    private double prioirity;
    private boolean marked;
    private GraphNode parent;
    private double g;
    private double heuristic;

    private HashSet<GraphNode> connectedNodes;


    public GraphNode(long id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        connectedNodes = new HashSet<>();
        marked = false;
        parent = null;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setHeuristic(double h) {
        heuristic = h;
    }

    public double getHeuristic() {
        return heuristic;
    }

    public void setParent(GraphNode p) {
        parent = p;
    }

    public GraphNode getParent() {
        return parent;
    }

    public void setMarkedToTrue() {
        marked = true;
    }

    public void setMarkedToFalse() {
        marked = false;
    }

    public void setG(double gfunc) {
        g = gfunc;
    }

    public double getG() {
        return g;
    }


    public static void connectArrayList(ArrayList<GraphNode> nodes) {
        if (nodes.isEmpty()) {
            return;
        }

        for (int i = 0; i < nodes.size() - 1; i++) {
            connect(nodes.get(i), nodes.get(i + 1));
        }

    }

    public static void connect(GraphNode node1, GraphNode node2) {
        if (!node1.connectedNodes.contains(node2)) {
            node1.connectedNodes.add(node2);
        }

        if (!node2.connectedNodes.contains(node1)) {
            node2.connectedNodes.add(node1);
        }

    }

    public boolean isDisconnected() {
        return connectedNodes.isEmpty();
    }

    public HashSet getConnectedNodes() {
        return connectedNodes;
    }


    public void setName(String n) {
        name = n;
    }

    public void setPrioirity(double p) {
        prioirity = p;
    }

    public double getPrioirity() {
        return prioirity;
    }

    public static double calcDistance(GraphNode node1, GraphNode node2) {
        double latDistanceSquared = Math.pow(node1.latitude - node2.latitude, 2);
        double lonDistanceSquared = Math.pow(node1.longitude - node2.longitude, 2);
        return Math.sqrt(latDistanceSquared + lonDistanceSquared);
    }

    public static GraphNode getClosestGraphNode(HashMap<String, GraphNode> allNodes,
                                                double lat, double lon) {
        double minDistance = 1000000000000.0;
        GraphNode toCompare = new GraphNode(0, lat, lon);
        GraphNode result = toCompare;
        for (GraphNode node : allNodes.values()) {
            double distance = calcDistance(node, toCompare);
            if (distance < minDistance) {
                minDistance = distance;
                result = node;
            }
        }
        return result;


    }

    public long getId() {
        return id;
    }

    public boolean isMarked() {
        return marked;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GraphNode graphNode = (GraphNode) o;

        return id == graphNode.id;

    }

    @Override
    public int hashCode() {
        return (int) id;
    }

    @Override
    public String toString() {
        return "GraphNode{"
                + "id=" + id
                + ", latitude=" + latitude
                + ", longditude=" + longitude + '}';
    }

    @Override
    public int compareTo(GraphNode other) {
        if (this.prioirity < other.prioirity) {
            return -1;
        } else if (this.prioirity > other.prioirity) {
            return 1;
        } else {
            return 0;
        }
    }

//    public static void main(String[] args) {
////        ArrayList<GraphNode> testNodes = new ArrayList<>();
////
////        for (int i = 0; i < 10; i++) {
////            GraphNode testNode = new GraphNode((long) i, (double) i, (double) i);
////            testNodes.add(testNode);
////        }
////
////        connectArrayList(testNodes);
////
////
////
////        connect(new GraphNode(100, 100.0, 100.0), (testNodes.get(4)));
////
////        for (GraphNode node : testNodes) {
////            System.out.println(node.getConnectedNodes());
////        }
//
////        GraphNode g1 = new GraphNode(1, 2.0, 3.0);
////        g1.setPrioirity(3.0);
////        GraphNode g2 = new GraphNode(3, 5.0, 7.0);
////        g2.setPrioirity(5.0);
////
////        HashMap<String, GraphNode> testMap = new HashMap<>();
////        testMap.put("g1", g1);
////        testMap.put("g2", g2);
////
////
////        System.out.println(g2.compareTo(g2));
////        System.out.println(calcDistance(g1, g2));
////
////        System.out.println(getClosestGraphNode(testMap, 4.0, 4.0));
//
////        HashSet<Long> test = new HashSet<>();
////        LinkedList<Long> test1 = new LinkedList<>();
////        test.add((long) 53140452);
////        test.add((long) 206140568);
////        test.add((long) 956500319);
////        test.add((long) 53140454);
////
////        test1.add((long) 53140452);
////        test1.add((long) 206140568);
////        test1.add((long) 956500319);
////        test1.add((long) 53140454);
////
////        if (test.contains((long) 53140452) && test.contains((long) 206140568) &&
////                test.contains((long) 956500319) && test.contains((long) 53140454)) {
////            test1.remove((long) 53140452);
////        }
////
////        System.out.println(test1);
//        long id1 = 1;
//        long id2 = 2;
//        long id3 = 3;
//        long id4 = 4;
//        long id5 = 5;
//
//        GraphNode node1 = new GraphNode(id1, 0.0, 0.0);
//        GraphNode node2 = new GraphNode(id2, 4.0, 3.0);
//        GraphNode node3 = new GraphNode(id3, -4.0, 3.0);
//        GraphNode node4 = new GraphNode(id4, -4.0, 11.0);
//        GraphNode node5 = new GraphNode(id5, -1.0, 15.0);
//
//        ArrayList<GraphNode> connect1 = new ArrayList<>();
//
//        connect1.add(node1);
//        connect1.add(node2);
//        connect1.add(node5);
//        connect1.add(node4);
//        connect1.add(node3);
//
//        connectArrayList(connect1);
//
//        connect(node2, node3);
//        connect(node1, node3);
//
//        for (GraphNode node : connect1) {
//            System.out.println(node.getConnectedNodes());
//        }
//
//        testNodes = new HashMap<>();
//        testNodes.put("1", node1);
//        testNodes.put("2", node2);
//        testNodes.put("3", node3);
//        testNodes.put("4", node4);
//        testNodes.put("5", node5);
//
//        HashMap<String, Double> testParams = new HashMap<>();
//        testParams.put("start_lat", 0.0);
//        testParams.put("start_lon", 0.0);
//        testParams.put("end_lat", -1.0);
//        testParams.put("end_lon", 15.0);
//
//        LinkedList<Long> result = MapServer.findAndSetRoute(testParams);
//        System.out.println(result);

//        for (GraphNode node : connect1) {
//            System.out.println(node.getG());
//        }





}

