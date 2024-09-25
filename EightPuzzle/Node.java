package EightPuzzle;

import java.util.*;

public class Node implements Comparable<Node> {
    private List<Integer> state;
    private List<Moves> moves;
    private String heuristic;

    public enum Moves {
        LEFT,
        RIGHT,
        UP,
        DOWN
    }

    public Node(List<Integer> state) {
        this.state = state;
        this.moves = new ArrayList<>();
        this.heuristic = "";
    }

    public Node(List<Integer> state, Node parent, Moves move) {
        this.state = state;
        this.heuristic = "";
        if (parent != null) {
            this.moves = parent.getMoves();
            this.moves.add(move);
        } else {
            this.moves = new ArrayList<>();
        }
    }

    public Node(List<Integer> state, Node parent, Moves move, String heuristic) {
        this.state = state;
        this.heuristic = heuristic;
        if (parent != null) {
            this.moves = parent.getMoves();
            this.moves.add(move);
        } else {
            this.moves = new ArrayList<>();
        }
    }

    public int compareTo(Node node) {
        int n;
        // calculate g(n) + h(n)
        switch (heuristic) {
            case "h1":
                n = Integer.valueOf(this.heuristicH1() + this.getPathCost())
                        .compareTo(Integer.valueOf(node.heuristicH1() + node.getPathCost()));
                break;
            case "h2":
                n = Integer.valueOf(this.heuristicH2() + this.getPathCost())
                        .compareTo(Integer.valueOf(node.heuristicH2() + node.getPathCost()));
                break;
            default: // no heuristic
                n = 0;
        }
        if (n == 0)
            n = this.moves.getLast().compareTo(node.moves.getLast());
        return n;
    }

    public List<Integer> getState() {
        return state.stream().map(Integer::valueOf).toList();
    }

    public String getStateString() {
        return state.toString().replace("[", "").replace("]", "").replace(",", "");
    }

    public List<Moves> getMoves() {
        return new ArrayList<>(moves);
    }

    public int getPathCost() {
        return this.moves.size();
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
            if (state.indexOf(i) != i) {
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

    @Override
    public boolean equals(Object e) {
        Objects.requireNonNull(e);
        if (e.getClass() != this.getClass())
            return false;
        else if (((Node) e).getStateString().equals(this.getStateString()))
            return true;
        return false;
    }
}
