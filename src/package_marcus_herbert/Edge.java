package package_marcus_herbert;

/**
 * package_marcus_herbert.Edge classed used for implementing the Synch GHS MST algorithm
 * Contains a weight and a node it points to
 * Because it only points to one node, for each edge, each node object representing a node
 * adjacent to the edge will need it's own edge object
 */

public class Edge {

    private Node node;
    private int weight;
    private boolean inUse;

    /**
     * Creates an edge object
     *
     * @param node
     * @param weight
     */
    public Edge (Node node, int weight) {
        this.node = node;
        this.weight = weight;
        inUse = true;
    }

    /**
     *
     * @return the package_marcus_herbert.Node this edge points to
     */
    public Node getNode () {
        return node;
    }

    /**
     *
     * @return the weight of the edge
     */
    public int getWeight () {
        return weight;
    }

    /**
     *
     * @return if the edge is being used in the graph it is a part of
     */
    public boolean isInUse () {
        return inUse;
    }

    /**
     *
     * @param bool whether the edge will be used in the graph it is a part of
     */
    public void setInUse (boolean bool) {
        inUse = bool;
    }

    /**
     *
     * @return a string representation of the edge in the form:
     * weight(nodeID)
     */
    @Override
    public String toString () {
        return weight + "(" + node + ")";
    }

}
