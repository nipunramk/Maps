/**
 * Created by nipun.ramk on 4/17/2016.
 */
public class AStar implements Comparable<AStar> {

    private GraphNode currentNode;
    private GraphNode endNode;
    private AStar parentNode;
    private double priority;
    private double g;


    public AStar(GraphNode currentNode, GraphNode endNode,
                 AStar parentNode, double g, double priority) {
        this.currentNode = currentNode;
        this.endNode = endNode;
        this.parentNode = parentNode;
        this.g = g;
        this.priority = priority;
    }

    public int compareTo(AStar other) {
        if (this.priority < other.priority) {
            return -1;
        } else if (this.priority > other.priority) {
            return 1;
        } else {
            return 0;
        }
    }

    public double getPriority() {
        return priority;
    }

    public double getG() {
        return g;
    }

    public AStar getParent() {
        return parentNode;
    }

    public GraphNode getCurrentNode() {
        return currentNode;
    }
}
