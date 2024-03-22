import java.util.ArrayList;
import java.util.Random;

public class Main {
    public static void main(String[] args) {

        int numberOfNodes = 5;
        ArrayList<Node> nodes = new ArrayList<>();
        Random rand = new Random();

        //create nodes
        for (int i = 0; i < numberOfNodes; i++) {
            nodes.add(new Node(i));
        }

        //create random array of weights
        ArrayList<ArrayList<Node>> pow = possibleEdges(nodes);
        int[] weights = new int[pow.size()];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = i;
        }
        int temp;
        for (int i = 0; i < weights.length; i++) {
            temp = weights[0];
            weights[0] = weights[i];
            weights[i] = temp;
        }

        //randomly link nodes
        int k = 0;
        for (ArrayList<Node> pair: pow) {
            if (true) {//should have something more random, right now creating complete graph with different weights
                pair.get(1).addEdge(new Edge(pair.get(0), weights[k]));
                pair.get(0).addEdge(new Edge(pair.get(1), weights[k]));
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

}
