import java.util.ArrayList;
import java.util.Random;

/**
 * Creates various nodes with various random edges between them
 * Uses this to test the Synch GHS algorithm
 */

public class Main {
    public static void main(String[] args) {

        int numberOfNodes = 20;
        int maxEdgesPerNode = 5; //must be at least 2
        ArrayList<Node> nodes = new ArrayList<>();
        Random rand = new Random();

        //create nodes
        for (int i = 0; i < numberOfNodes; i++) {
            nodes.add(new Node(i, nodes));
        }

        //create random array of weights
        ArrayList<ArrayList<Node>> pow = possibleEdges(nodes);
        int[] weights = new int[pow.size()];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = i;
        }
        int temp;
        int toSwap;
        for (int i = weights.length; i > 1; i--) {
            toSwap = rand.nextInt(i);
            temp = weights[toSwap];
            weights[toSwap] = weights[i-1];
            weights[i-1] = temp;
        }

        //randomly link nodes
        //must be connected graph
        int k = 0;//reprsents which of the random weights to use
        //start by creating a path that connects all nodes
        for (int i = 1; i < nodes.size(); i++) {//order in the ArrayList is arbitrary, this does not hurt randomness
            nodes.get(i).addEdge(new Edge(nodes.get(i-1), weights[k]));
            nodes.get(i-1).addEdge(new Edge(nodes.get(i), weights[k]));
            k++;
        }
        //randomly include or not include each other possible edge as long as it does not pass maximum edges per node
        for (ArrayList<Node> pair: pow) {
            //randomly pick if it's an edge that is not yet used, and does not exceed either nodes maximum edges
            if (rand.nextBoolean() && Math.abs(pair.get(0).getID()-pair.get(1).getID()) > 1 && pair.get(0).getEdges().size() < maxEdgesPerNode && pair.get(1).getEdges().size() < maxEdgesPerNode) {
                pair.get(0).addEdge(new Edge(pair.get(1), weights[k]));
                pair.get(1).addEdge(new Edge(pair.get(0), weights[k]));
                k++;
            }
        }

        printGraph(nodes);
        System.out.println();

        //create threads for the nodes, and start them
        Thread t;
        for (Node n: nodes) {
            t = new Thread(n);
            t.start();
        }

    }

    /**
     *
     * @param set an arraylist containing every object to be paired
     * @return every possible pair (order does not matter) in the set given
     * @param <E> The generic class for the set
     */
    public static <E> ArrayList<ArrayList<E>> possibleEdges (ArrayList<E> set) {
        ArrayList<ArrayList<E>> pow = new ArrayList<ArrayList<E>>();
        ArrayList<E> pair;
        for (int i = 0; i < set.size(); i++) {
            for (int j = i+1; j < set.size(); j++) {
                pair = new ArrayList<E>();
                pair.add(set.get(i));
                pair.add(set.get(j));
                pow.add(pair);
            }
        }
        return pow;
    }

    /**
     * Prints out each node in the graph,
     * and for each node all of it's in use edges
     *
     * @param nodes
     */
    public static void printGraph (ArrayList<Node> nodes) {
        ArrayList<Edge> edges;
        for (Node n: nodes) {
            System.out.println(n);
            edges = n.getEdges();
            for (Edge e: edges) {
                if (e.isInUse()) {
                    System.out.print(e + " ");
                }
            }
            System.out.println();
        }
    }

    /**
     *
     * @param list
     * @param element
     * @return if the given element is in the list
     * @param <E> the generic class of the element and list
     */
    public static <E> boolean in (ArrayList<E> list, E element) {
        for (E el: list) {
            if (el == element) {
                return true;
            }
        }
        return false;
    }

}
