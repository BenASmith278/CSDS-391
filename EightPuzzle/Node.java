import java.util.*;

public class Node implements Comparable<Node> {
    private List<Integer> state;
    private Node parent;
    private Moves move;
    private String heuristic;

    public enum Moves {
        LEFT,
        RIGHT,
        UP,
        DOWN
    }

    public Node(List<Integer> state) {
        this.state = state;
        this.parent = null;
        this.move = null;
        this.heuristic = "";
    }

    public Node(List<Integer> state, Node parent, Moves move) {
        this.state = state;
        this.parent = parent;
        this.move = move;
        this.heuristic = "";
    }

    public Node(List<Integer> state, Node parent, Moves move, String heuristic) {
        this.state = state;
        this.parent = parent;
        this.move = move;
        this.heuristic = heuristic;
    }

    public int compareTo(Node node) {
        int n;
        switch (heuristic) {
            case "h1":
                n = Integer.valueOf(this.heuristicH1()).compareTo(Integer.valueOf(node.heuristicH1()));
                break;
            case "h2":
                n = Integer.valueOf(this.heuristicH2()).compareTo(Integer.valueOf(node.heuristicH2()));
                break;
            default: // no heuristic
                n = 0;
        }
        if (n == 0)
            n = this.move.compareTo(node.move);
        return n;
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
        String moveString;
        switch (move) {
            case Moves.LEFT:
                moveString = "move left";
                break;
            case Moves.RIGHT:
                moveString = "move right";
                break;
            case Moves.UP:
                moveString = "move up";
                break;
            case Moves.DOWN:
                moveString = "move down";
                break;
            default:
                moveString = "none";
        }
        return moveString;
    }

    public List<Node> expand(EightPuzzle puzzle) {
        List<Node> children = new ArrayList<>();
        puzzle.cmd("setState " + getStateString());

        if (puzzle.cmd("move left -q")) {
            children.add(new Node(new ArrayList<>(puzzle.getState()), this, Moves.LEFT, this.heuristic));
            puzzle.cmd("move right");
        }

        if (puzzle.cmd("move right -q")) {
            children.add(new Node(new ArrayList<>(puzzle.getState()), this, Moves.RIGHT, this.heuristic));
            puzzle.cmd("move left");
        }

        if (puzzle.cmd("move up -q")) {
            children.add(new Node(new ArrayList<>(puzzle.getState()), this, Moves.UP, this.heuristic));
            puzzle.cmd("move down");
        }

        if (puzzle.cmd("move down -q")) {
            children.add(new Node(new ArrayList<>(puzzle.getState()), this, Moves.DOWN, this.heuristic));
            puzzle.cmd("move up");
        }

        return children;
    }

    public int heuristicH1() {
        int misplacedTiles = 0;
        for (int i = 1; i < 9; i++) {
            if (state.get(i) != i) {
                misplacedTiles++;
            }
        }
        return misplacedTiles;
    }

    public int heuristicH2() {
        int totalDistance = 0;
        for (int i = 1; i < 9; i++) {
            int tileIndex = state.indexOf(i);
            // manhattan distance
            totalDistance = totalDistance + Math.abs((tileIndex % 3) - (i % 3)) + Math.abs((tileIndex / 3) - (i / 3));
        }
        return totalDistance;
    }
}
