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

/*
Some sample output

0
55(1) 96(2) 177(4) 149(6) 142(10)
1
55(0) 84(2) 143(6) 104(7) 135(9)
2
84(1) 45(3) 96(0) 111(7) 126(8)
3
45(2) 156(4) 59(6) 189(9) 15(11)
4
156(3) 28(5) 177(0) 109(8) 71(9)
5
28(4) 122(6) 163(11) 178(16) 22(18)
6
122(5) 139(7) 149(0) 143(1) 59(3)
7
139(6) 162(8) 104(1) 111(2) 31(11)
8
162(7) 165(9) 126(2) 109(4) 147(10)
9
165(8) 161(10) 135(1) 189(3) 71(4)
10
161(9) 145(11) 142(0) 147(8) 141(12)
11
145(10) 152(12) 15(3) 163(5) 31(7)
12
152(11) 166(13) 141(10) 102(14) 159(16)
13
166(12) 110(14) 90(15) 25(19)
14
110(13) 95(15) 102(12) 73(16) 87(18)
15
95(14) 157(16) 90(13) 79(17) 108(18)
16
157(15) 148(17) 178(5) 159(12) 73(14)
17
148(16) 146(18) 79(15) 2(19)
18
146(17) 27(19) 22(5) 87(14) 108(15)
19
27(18) 25(13) 2(17)

Leader: 18, Added Edge: 22(5)
Leader: 18, connected to: [18, 5]
Leader: 18, Added Edge: 22(18)
Leader: 5, Added Edge: 28(4)
Leader: 5, connected to: [18, 5, 4]
Leader: 9, Added Edge: 71(9)
Leader: 1, Added Edge: 55(0)
Leader: 1, connected to: [1, 0]
Leader: 19, Added Edge: 2(19)
Leader: 1, Added Edge: 55(1)
Leader: 19, Added Edge: 2(17)
Leader: 19, connected to: [19, 17]
Leader: 19, Added Edge: 25(13)
Leader: 19, connected to: [19, 17, 13]
Leader: 19, Added Edge: 27(18)
Leader: 19, connected to: [19, 17, 13, 18, 5, 4]
Leader: 19, Added Edge: 27(18)
Leader: 19, connected to: [19, 17, 13, 18, 5, 4, 18, 5, 4]
Leader: 17, Added Edge: 79(15)
Leader: 17, connected to: [19, 17, 13, 18, 5, 4, 18, 5, 4, 15]
Leader: 8, Added Edge: 109(8)
Leader: 11, Added Edge: 15(11)
Leader: 16, Added Edge: 73(14)
Leader: 16, Added Edge: 73(16)
Leader: 11, Added Edge: 15(3)
Leader: 16, connected to: [16, 14]
Leader: 18, Added Edge: 87(14)
Leader: 18, connected to: [19, 17, 13, 18, 5, 4, 18, 5, 4, 15, 16, 14]
Leader: 11, connected to: [11, 3]
Leader: 14, Added Edge: 102(12)
Leader: 11, Added Edge: 31(7)
Leader: 3, Added Edge: 45(2)
Leader: 14, connected to: [19, 17, 13, 18, 5, 4, 18, 5, 4, 15, 16, 14, 12]
Leader: 3, connected to: [11, 3, 7, 2]
Leader: 11, connected to: [11, 3, 7]
Leader: 11, Added Edge: 31(11)
Leader: 2, Added Edge: 84(1)
Leader: 2, connected to: [11, 3, 7, 2, 1, 0]
Leader: 14, Added Edge: 102(14)
Leader: 6, Added Edge: 59(6)
Leader: 12, Added Edge: 141(10)
Leader: 12, connected to: [19, 17, 13, 18, 5, 4, 18, 5, 4, 15, 16, 14, 12, 10]
 */
