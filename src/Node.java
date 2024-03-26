import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * The node class for nodes in the Synch GHS MST algorithm
 * Can be run by a thread
 * Has a queue of messages it has been sent that it reads in FIFO
 * has a list of edges it is adjacent to
 */

public class Node implements Runnable{

    private int id;
    private BlockingQueue<Message> messages;
    private ArrayList<Edge> edges;
    private int leaderID;
    private int level;
    private int convergesAwaiting;
    private Node convergeTo;
    private Edge smallestSeen;
    private Node hasSmallest;
    private volatile ArrayList<Node> connectedTo;
    private ArrayList<Node> inGraph;

    /**
     * Creates a node for the algorithm
     *
     * @param id the ID given to this node
     * @param inGraph the graph the node is part of
     */
    public Node(int id, ArrayList<Node> inGraph){
        messages = new ArrayBlockingQueue<>(20);
        edges = new ArrayList<Edge>();
        this.id = id;
        this.inGraph = inGraph;
        connectedTo = new ArrayList<Node>();
        connectedTo.add(this);
    }

    /**
     *
     * @param mess the message that will be placed in the nodes queue
     */
    public synchronized void receiveMessage(Message mess){
        messages.add(mess);
    }

    /**
     *
     * @param e the edge that will be added to this node
     */
    public synchronized void addEdge (Edge e) {
        edges.add(e);
    }

    /**
     *
     * @param e the edge that will be removed from this node
     */
    public synchronized void removeEdge (Edge e) {
        edges.remove(e);
    }

    /**
     *
     * @return all edges the node is adjacent to
     */
    public ArrayList<Edge> getEdges () {
        return edges;
    }

    /**
     *
     * @return the id of the node
     */
    public int getID () {
        return id;
    }

    /**
     *
     * @return the id of the node as a string
     */
    @Override
    public String toString () {
        return ""+getID();
    }

    /**
     *
     * @return the queue of messages the node has
     */
    public BlockingQueue<Message> getMessages () {
        return messages;
    }

    /**
     * Completely overrides the queue of messages the node has
     *
     * @param messages
     */
    public synchronized void setMessages (BlockingQueue<Message> messages) {
        this.messages = messages;
    }


    /**
     * Sets itself up to have needed attributes to start the Synch GHS MST algorithm
     * Reads messages from its queue, and reacts according to the algorithm
     * Stops when the algorithm is done
     */
    @Override
    public void run() {

        //initialize itself to start MST
        for (Edge e: edges) {
            e.setInUse(false);
        }
        level = 0;
        leaderID = id;
        Message sending;

        //find MWOE for single node
        smallestSeen = edges.get(0);
        for (Edge e: edges) {
            if (e.getWeight() < smallestSeen.getWeight()) {
                smallestSeen = e;
            }
        }
        System.out.println("ID: " + id + " MWOE: " + smallestSeen);

        //request merge with another single node component
        sending = Message.createMergeMessage(this);
        smallestSeen.getNode().receiveMessage(sending);

        boolean running = true;
        while (running) {
            if (messages.peek() != null) {//read top message

                //take message if there is one to take
                Message receiving = null;
                try {
                    receiving = messages.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if (receiving != null) {//should make sure a message received before doing anything with it

                    //leader change
                    if (receiving.getType() == MessageType.LEADER_BROADCAST) {
                        leaderID = receiving.getLeaderID();
                        connectedTo = receiving.getConnectedTo();
                        //System.out.println(id + ", new leader: " + leaderID);
                        sending = Message.createLeaderBroadCast(this, leaderID, connectedTo);
                        for (Edge e: edges) {
                            if (e.isInUse() && receiving.getSender() != e.getNode()) {//send to all other nodes in component
                                e.getNode().receiveMessage(sending);
                            }
                        }
                    }

                    //shutdown braodcast
                    if (receiving.getType() == MessageType.SHUTDOWN_BROADCAST) {
                        for (Edge e: edges) {
                            e.getNode().receiveMessage(receiving);
                        }
                        running = false;
                    }

                    //MWOE broadcast
                    if (receiving.getType() == MessageType.MWOE_BROADCAST) {
                        sending = Message.createMWOEBroadCast(this);
                        convergeTo = receiving.getSender();
                        convergesAwaiting = 0;

                        //determine minimum edge connecting to this node
                        smallestSeen = edges.get(0);
                        for (Edge e: edges) {//first find largest, so we have one to compare to, need to do this because when searching for smallest, we want to only read nodes not in use
                            if (e.getWeight() > smallestSeen.getWeight()) {
                                smallestSeen = e;
                            }
                        }
                        for (Edge e: edges) {//now find smallest that's not in use
                            if (!e.isInUse() && !Main.in(connectedTo, e.getNode()) && e.getWeight() < smallestSeen.getWeight()) {
                                smallestSeen = e;
                            }
                        }
                        hasSmallest = this;

                        for (Edge e: edges) {
                            if (e.isInUse() && receiving.getSender() != e.getNode()) {//send to all other nodes in component
                                e.getNode().receiveMessage(sending);
                                convergesAwaiting++;
                            }
                        }

                        if (convergesAwaiting == 0) {//base case to start converging
                            sending = Message.createConvergeCast(smallestSeen, hasSmallest);
                            convergeTo.receiveMessage(sending);
                        }

                    }

                    //MWOE convergecast
                    if (receiving.getType() == MessageType.CONVERGECAST) {
                        convergesAwaiting--;
                        if (receiving.getSmallest().getWeight() < smallestSeen.getWeight()) {//replace the smallest seen if necessary
                            smallestSeen = receiving.getSmallest();
                            hasSmallest = receiving.getHasSmallest();
                        }
                        if (convergesAwaiting == 0) {
                            if (id == leaderID) {//if it's reached the leader, start merging
                                sending = Message.createMergeMessage(hasSmallest);//sending on behalf of the one with the MWOE
                                smallestSeen.getNode().receiveMessage(sending);
                                System.out.println(id + " send merge request to: " + smallestSeen.getNode());
                                System.out.println(smallestSeen + ", " + hasSmallest);
                            }else {
                                for (Edge e : edges) {
                                    if (e.getNode() == convergeTo) {//finding the node to converge back to
                                        //converge
                                        sending = Message.createConvergeCast(smallestSeen, hasSmallest);
                                        convergeTo.receiveMessage(sending);
                                    }
                                }
                            }
                        }
                    }

                    //merge with another component
                    if (receiving.getType() == MessageType.MERGE && smallestSeen.getNode() == receiving.getSender()) {//merge if both ends agree
                        smallestSeen.setInUse(true);
                        receiving.getSender().smallestSeen.setInUse(true);
                        if (this.getID() > smallestSeen.getNode().getID()) {
                            leaderID = this.getID();
                        } else {
                            leaderID = smallestSeen.getNode().getID();
                        }
                        System.out.println("Added: " + smallestSeen);

                        //when the algorithm is complete, start a shutdown broadcast
                        if (connectedTo.size() == inGraph.size()) {
                            //start a shutdown broadcast
                            System.out.println("here");
                            this.receiveMessage(Message.createShutdown());
                        }

                        if (id == leaderID) {
                            connectedTo.addAll(receiving.getSender().connectedTo);
                            System.out.println(id + " conto " + connectedTo);
                            this.receiveMessage(Message.createLeaderBroadCast(this, leaderID, connectedTo));//start broadcasting leader change
                            this.receiveMessage(Message.createMWOEBroadCast(this));//start broadcasting mwoe search
                        }

                    }else{//put back into queue so message is not lost when this node has to wait for another node
                        this.receiveMessage(receiving);
                    }

                }

            }
        }

    }

}
