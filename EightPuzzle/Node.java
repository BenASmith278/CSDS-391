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

    public List<Integer> getState() {
        return state.stream().map(Integer::valueOf).toList();
    }

    public String getStateString() {
        return state.toString().replace("[", "").replace("]", "").replace(",", "");
    }

    public Node getParent() {
        return parent;
    }

    public String getMove() {
        return move;
    }

    public List<Node> expand(EightPuzzle puzzle) {
        List<Node> children = new ArrayList<>();
        puzzle.cmd("setState " + getStateString());

        if (puzzle.cmd("move left -q")) {
            children.add(new Node(new ArrayList<>(puzzle.getState()), this, "move left"));
            puzzle.cmd("move right");
        }

        if (puzzle.cmd("move right -q")) {
            children.add(new Node(new ArrayList<>(puzzle.getState()), this, "move right"));
            puzzle.cmd("move left");
        }

        if (puzzle.cmd("move up -q")) {
            children.add(new Node(new ArrayList<>(puzzle.getState()), this, "move up"));
            puzzle.cmd("move down");
        }

        if (puzzle.cmd("move down -q")) {
            children.add(new Node(new ArrayList<>(puzzle.getState()), this, "move down"));
            puzzle.cmd("move up");
        }

        return children;
    }
}
