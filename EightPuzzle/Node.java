import java.util.*;

public class Node {
    List<Integer> state;
    Node parent;
    String move;

    public Node(List<Integer> state, Node parent, String move) {
        this.state = state;
        this.parent = parent;
        this.move = move;
    }

    public String getState() {
        return state.toString().replace("[", "").replace("]", "").replace(",", "");
    }

    public Node getParent() {
        return parent;
    }

    public String getMove() {
        return move;
    }
}
