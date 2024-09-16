import java.util.*;

public class Node implements Comparable<Node> {
    List<Integer> state;
    Node parent;
    String move;
    String heuristic;

    public Node(List<Integer> state, Node parent, String move) {
        this.state = state;
        this.parent = parent;
        this.move = move;
        this.heuristic = "";
    }

    public Node(List<Integer> state, Node parent, String move, String heuristic) {
        this.state = state;
        this.parent = parent;
        this.move = move;
        this.heuristic = heuristic;
    }

    public int compareTo(Node node) {
        switch (heuristic) {
            case "h1":
                return Integer.valueOf(this.heuristicH1()).compareTo(Integer.valueOf(node.heuristicH1()));
            case "h2":
                return Integer.valueOf(this.heuristicH2()).compareTo(Integer.valueOf(node.heuristicH2()));
            default:
                break;
        }
        
        return 0;
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
            children.add(new Node(new ArrayList<>(puzzle.getState()), this, "move left", this.heuristic));
            puzzle.cmd("move right");
        }

        if (puzzle.cmd("move right -q")) {
            children.add(new Node(new ArrayList<>(puzzle.getState()), this, "move right", this.heuristic));
            puzzle.cmd("move left");
        }

        if (puzzle.cmd("move up -q")) {
            children.add(new Node(new ArrayList<>(puzzle.getState()), this, "move up", this.heuristic));
            puzzle.cmd("move down");
        }

        if (puzzle.cmd("move down -q")) {
            children.add(new Node(new ArrayList<>(puzzle.getState()), this, "move down", this.heuristic));
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
