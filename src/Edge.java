public class Edge {

    private Node node;
    private int weight;
    private boolean inUse;

    public Edge (Node node, int weight) {
        this.node = node;
        this.weight = weight;
        inUse = true;
    }

    public Node getNode () {
        return node;
    }
    public int getWeight () {
        return weight;
    }
    public boolean isInUse () {
        return inUse;
    }
    public void setInUse (boolean bool) {
        inUse = bool;
    }

    @Override
    public String toString () {
        return weight + "(" + node + ")";
    }

}
