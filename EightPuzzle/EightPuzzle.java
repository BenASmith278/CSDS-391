import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

public class EightPuzzle {
    private List<Integer> puzzleState;

    public static void main(String[] args) {
        EightPuzzle puzzle = new EightPuzzle(new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 });
        for (String cmdfile : args) {
            puzzle.cmdfile(cmdfile);
        }
    }

    public EightPuzzle(Integer[] state) {
        this.puzzleState = new ArrayList<Integer>();
        for (int value : state) {
            this.puzzleState.add(value);
        }
    }

    public boolean cmd(String command) {
        // there is definitely a way to use a switch case for this
        if (Pattern.matches("^setState ([0-8] ){8}[0-8]$", command)) {
            return setState(command.substring(9));
        } else if (Pattern.matches("^printState$", command)) {
            printState();
        } else if (Pattern.matches("^move (up|down|left|right)$", command)) {
            if (!(move(command.substring(5)))) {
                System.out.println("Error: invalid move");
                return false;
            }
            // move without printing
        } else if (Pattern.matches("move (up|down|left|right) -q", command)) {
            return move(command.substring(5, command.length() - 3));
        } else if (Pattern.matches("^scrambleState [0-9]+$", command)) {
            scrambleState(Integer.parseInt(command.substring(14)));
        } else if (Pattern.matches("^(#|//).*$", command)) {
            // do nothing
        } else if (Pattern.matches("^solve DFS$", command)) {
            return solveDFS("1000");
        } else if (Pattern.matches("^solve DFS maxnodes=[0-9]+$", command)) {
            return solveDFS(command.substring(19));
        } else if (Pattern.matches("^solve BFS", command)) {
            return solveBFS("1000");
        } else if (Pattern.matches("^solve BFS maxnodes=[0-9]+$", command)) {
            return solveBFS(command.substring(19));
        } else if (Pattern.matches("^solve A\\* (h1|h2)$", command)) {
            return solveAStar("1000", command.substring(9));
        } else if (Pattern.matches("^solve A\\* (h1|h2) maxnodes=[0-9]+$", command)) {
            return solveAStar(command.substring(21), command.substring(9));
        } else if (Pattern.matches("^heuristic (h1|h2)$", command)) {
            System.out.println("" + heuristic(new Node(puzzleState, null, ""), command.substring(10)));
        } else {
            System.out.println("Error: invalid command: " + command);
        }

        return true;
    }

    public void cmdfile(String fileName) {
        try {
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                System.out.println(line);
                cmd(line);
            }
            scanner.close();
        } catch (Exception e) {
            System.out.println("Error: invalid file: " + fileName);
        }
    }

    public List<Integer> getState() {
        return puzzleState.stream().map(Integer::valueOf).toList();
    }

    private boolean setState(String state) {
        String[] values = state.split(" ");
        Set<Integer> uniqueValues = new HashSet<Integer>();
        List<Integer> newState = new ArrayList<Integer>();
        for (String value : values) {
            int val = Integer.parseInt(value);
            newState.add(val);
            if (!(uniqueValues.add(val))) {
                System.out.println("Error: invalid puzzle state");
                return false;
            }
        }

        this.puzzleState = newState;
        return true;
    }

    private void printState() {
        String stateString = puzzleState.toString();
        System.out.println(
                stateString.substring(0, 9) + "\n" + stateString.substring(9, 18) + "\n" + stateString.substring(18));
    }

    private boolean move(String direction) {
        int zeroIndex = puzzleState.indexOf(0);
        int temp;
        switch (direction) {
            case "up":
                if (zeroIndex - 3 < 0)
                    return false;
                temp = puzzleState.get(zeroIndex - 3);
                puzzleState.set(zeroIndex, temp);
                puzzleState.set(zeroIndex - 3, 0);
                break;
            case "down":
                if (zeroIndex - 5 > 0)
                    return false;
                temp = puzzleState.get(zeroIndex + 3);
                puzzleState.set(zeroIndex, temp);
                puzzleState.set(zeroIndex + 3, 0);
                break;
            case "left":
                if (zeroIndex % 3 == 0)
                    return false;
                temp = puzzleState.get(zeroIndex - 1);
                puzzleState.set(zeroIndex, temp);
                puzzleState.set(zeroIndex - 1, 0);
                break;
            case "right":
                if (zeroIndex % 3 == 2)
                    return false;
                temp = puzzleState.get(zeroIndex + 1);
                puzzleState.set(zeroIndex, temp);
                puzzleState.set(zeroIndex + 1, 0);
                break;
            default:
                return false;
        }
        return true;
    }

    private void scrambleState(int n) {
        Map<Integer, String> directionsMap = new HashMap<Integer, String>();
        directionsMap.put(Integer.parseInt("0"), "left");
        directionsMap.put(Integer.parseInt("1"), "right");
        directionsMap.put(Integer.parseInt("2"), "up");
        directionsMap.put(Integer.parseInt("3"), "down");
        Random rand = new Random(123456789);
        setState("0 1 2 3 4 5 6 7 8");
        while (n > 0) {
            String randomDirection = directionsMap.get(rand.nextInt(4));
            if (move(randomDirection))
                n--;
        }
    }

    private boolean solveBFS(String maxNodes) {
        if (puzzleState.equals(new ArrayList<Integer>(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8)))) {
            printSolution(new Node(puzzleState, null, "start"), 0);
            return true;
        }
        int nodesCreated = 0;
        int maxNodesInt = Integer.parseInt(maxNodes);
        Queue<Node> frontier = new LinkedList<>();
        Set<String> reached = new HashSet<String>();
        frontier.add(new Node(puzzleState, null, "start"));
        reached.add(puzzleState.toString());

        while (nodesCreated < maxNodesInt && frontier.size() > 0) {
            for (Node child : frontier.poll().expand(this)) {
                String childState = child.getStateString();
                if (childState.equals("0 1 2 3 4 5 6 7 8")) {
                    setState(childState);
                    printSolution(child, nodesCreated);
                    return true;
                }
                if (reached.add(childState)) {
                    frontier.add(child);
                    nodesCreated++;
                }
            }
        }

        System.out.println("Error: maxnodes limit (" + maxNodes + ") reached");
        return false;
    }

    private boolean solveDFS(String maxNodes) {
        if (puzzleState.equals(new ArrayList<Integer>(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8)))) {
            printSolution(new Node(puzzleState, null, "start"), 0);
            return true;
        }
        int nodesCreated = 0;
        int maxNodesInt = Integer.parseInt(maxNodes);
        Stack<Node> frontier = new Stack<>();
        Set<String> reached = new HashSet<String>();
        frontier.push(new Node(puzzleState, null, "start"));
        reached.add(puzzleState.toString());

        while (nodesCreated < maxNodesInt && !(frontier.empty())) {
            for (Node child : frontier.pop().expand(this)) {
                String childState = child.getStateString();
                if (childState.equals("0 1 2 3 4 5 6 7 8")) {
                    setState(childState);
                    printSolution(child, nodesCreated);
                    return true;
                }
                if (reached.add(childState)) {
                    frontier.push(child);
                    nodesCreated++;
                }
            }
        }

        System.out.println("Error: maxnodes limit (" + maxNodes + ") reached");
        return false;
    }

    private void printSolution(Node currentNode, int nodesCreated) {
        Stack<String> moves = new Stack<>();
        while (currentNode.getParent() != null) {
            moves.push(currentNode.getMove());
            currentNode = currentNode.getParent();
        }
        System.out.println("Nodes created during search: " + nodesCreated);
        System.out.println("Solution length: " + moves.size());
        System.out.println("Move sequence:");
        while (!(moves.isEmpty())) {
            System.out.println(moves.pop());
        }
    }

    private int heuristic(Node node, String heuristic) {
        switch (heuristic) {
            case "h1":
                return new Node(puzzleState, null, "").heuristicH1();
            case "h2":
                return new Node(puzzleState, null, "").heuristicH2();
            default:
                break;
        }
        return 0;
    }

    private boolean solveAStar(String maxNodes, String heuristic) {
        // BFS priority queue with heuristic
        if (puzzleState.equals(new ArrayList<Integer>(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8)))) {
            printSolution(new Node(puzzleState, null, "start"), 0);
            return true;
        }
        int nodesCreated = 0;
        int maxNodesInt = Integer.parseInt(maxNodes);
        PriorityQueue<Node> frontier = new PriorityQueue<>(Node::compareTo);
        Set<String> reached = new HashSet<String>();
        frontier.add(new Node(puzzleState, null, "start", heuristic));
        reached.add(puzzleState.toString());

        while (nodesCreated < maxNodesInt && frontier.size() > 0) {
            for (Node child : frontier.poll().expand(this)) {
                String childState = child.getStateString();
                if (childState.equals("0 1 2 3 4 5 6 7 8")) {
                    setState(childState);
                    printSolution(child, nodesCreated);
                    return true;
                }
                if (reached.add(childState)) {
                    frontier.add(child);
                    nodesCreated++;
                }
            }
        }

        System.out.println("Error: maxnodes limit (" + maxNodes + ") reached");
        return false;
    }
}