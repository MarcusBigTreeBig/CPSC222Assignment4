package package_marcus_herbert;

import java.util.ArrayList;

/**
 * package_marcus_herbert.Message object to be sent between nodes in the Synch GHS algorithm
 * Has a type of message and when created, contains the necessary information that could
 * be needed by the receiving node
 */

public class Message {

    private Node sender;
    private Node hasSmallest;
    private int leaderID;
    private Edge smallest;
    private MessageType type;
    private ArrayList<Node> connectedTo;
    private Message mess;

    private Message () {

    }

    /**
     * Creates a message used for requesting a merge to the
     * other component
     *
     * @param n the node that is part of the other component
     * @return the message
     */
    public static Message createMergeMessage (Node n) {
        Message m = new Message();
        m.type = MessageType.MERGE;
        m.sender = n;
        return m;
    }

    /**
     * Creates a message used to broadcast that the component will search for it's MWOE
     *
     * @param sender the node that sent the message
     * @return the message
     */
    public static Message createMWOEBroadCast (Node sender) {
        Message m = new Message();
        m.type = MessageType.MWOE_BROADCAST;
        m.sender = sender;
        return m;
    }

    /**
     * Creates a message for broadcasting the information of the new leader to the receiving node
     *
     * @param sender the node that sends the message
     * @param leaderID the new leader of the component
     * @param connectedTo all the nodes in the component
     * @return the message
     */
    public static Message createLeaderBroadCast (Node sender, int leaderID, ArrayList<Node> connectedTo) {
        Message m = new Message();
        m.type = MessageType.LEADER_BROADCAST;
        m.sender = sender;
        m.leaderID = leaderID;
        m.connectedTo = connectedTo;
        return m;
    }

    /**
     * Creates a message for converging the MWOE information to the leader
     *
     * @param smallest the smallest edge the sending node has seen
     * @param hasSmallest which node has the smallest edge
     * @return the message
     */
    public static Message createConvergeCast (Edge smallest, Node hasSmallest) {
        Message m = new Message();
        m.type = MessageType.CONVERGECAST;
        m.smallest = smallest;
        m.hasSmallest = hasSmallest;
        return m;
    }

    /**
     * Creates a message that tells the receiving node to stop running the algorithm
     *
     * @return the message
     */
    public static Message createShutdown () {
        Message m = new Message();
        m.type = MessageType.SHUTDOWN_BROADCAST;
        return m;
    }

    /**
     *
     * @return the smallest edge the sending node has seen
     */
    public Edge getSmallest() {
        return smallest;
    }

    /**
     *
     * @return the type of this message
     */
    public MessageType getType() {
        return type;
    }

    /**
     *
     * @return the new leader ID
     */
    public int getLeaderID() {
        return leaderID;
    }

    /**
     *
     * @return which node has the minimum weight edge
     */
    public Node getHasSmallest() {
        return hasSmallest;
    }

    /**
     *
     * @return the sender node of this message
     */
    public Node getSender() {
        return sender;
    }

    /**
     *
     * @return which nodes are in the component
     */
    public ArrayList<Node> getConnectedTo () {
        return connectedTo;
    }
}
