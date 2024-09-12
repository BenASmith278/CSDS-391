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

    public void cmd(String command) {
        // there is definitely a way to use a switch case for this
        if (Pattern.matches("^setState ([0-8] ){8}[0-8]$", command)) {
            setState(command.substring(9));
        } else if (Pattern.matches("^printState$", command)) {
            printState();
        } else if (Pattern.matches("^move (up|down|left|right)$", command)) {
            if (!(move(command.substring(5))))
                System.out.println("Error: invalid move");
        } else if (Pattern.matches("^scrambleState [0-9]+$", command)) {
            scrambleState(Integer.parseInt(command.substring(14)));
        } else if (Pattern.matches("^(#|//).*$", command)) {
            // do nothing
        } else if (Pattern.matches("^solve DFS$", command)) {
            solveDFS("1000");
        } else if (Pattern.matches("^solve DFS maxnodes=[0-9]+$", command)) {
            solveDFS(command.substring(19));
        } else if (Pattern.matches("^solve BFS", command)) {
            solveBFS("1000");
        } else if (Pattern.matches("^solve BFS maxnodes=[0-9]+$", command)) {
            solveBFS(command.substring(19));
        } else {
            System.out.println("Error: invalid command: " + command);
        }
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

    private void setState(String state) {
        String[] values = state.split(" ");
        Set<Integer> uniqueValues = new HashSet<Integer>();
        List<Integer> newState = new ArrayList<Integer>();
        for (String value : values) {
            int val = Integer.parseInt(value);
            newState.add(val);
            if (!(uniqueValues.add(val))) {
                System.out.println("Error: invalid puzzle state");
                return;
            }
        }

        this.puzzleState = newState;
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

    private void solveBFS(String maxNodes) {
        int nodesCreated = 0;
        int maxNodesInt = Integer.parseInt(maxNodes);
        Queue<Node> frontier = new LinkedList<>();
        frontier.add(new Node(puzzleState, null, "start"));

        while (nodesCreated < maxNodesInt) {
            Node currentNode = frontier.poll();
            String currentState = currentNode.getState();
            setState(currentState); // set the puzzle state to the node being evaluated

            if (currentState.equals("0 1 2 3 4 5 6 7 8")) {
                printSolution(currentNode, nodesCreated);
                return;
            }

            // prof liberatore would shoot me if he saw this
            if (move("left")) {
                frontier.add(new Node(new ArrayList<>(puzzleState), currentNode, "move left"));
                move("right");
                nodesCreated++;
            }

            if (move("right")) {
                frontier.add(new Node(new ArrayList<>(puzzleState), currentNode, "move right"));
                move("left");
                nodesCreated++;
            }

            if (move("up")) {
                frontier.add(new Node(new ArrayList<>(puzzleState), currentNode, "move up"));
                move("down");
                nodesCreated++;
            }

            if (move("down")) {
                frontier.add(new Node(new ArrayList<>(puzzleState), currentNode, "move down"));
                move("up");
                nodesCreated++;
            }
        }

        System.out.println("Error: maxnodes limit (" + maxNodes + ") reached");
        return;
    }

    private void solveDFS(String maxNodes) {
        int nodesCreated = 0;
        int maxNodesInt = Integer.parseInt(maxNodes);
        Stack<Node> frontier = new Stack<>();
        frontier.add(new Node(puzzleState, null, "start"));

        while (nodesCreated < maxNodesInt) {
            Node currentNode = frontier.pop();
            String currentState = currentNode.getState();
            setState(currentState); // set the puzzle state to the node being evaluated

            if (currentState.equals("0 1 2 3 4 5 6 7 8")) {
                printSolution(currentNode, nodesCreated);
                return;
            }

            // prof liberatore would shoot me if he saw this
            if (move("left")) {
                frontier.push(new Node(new ArrayList<>(puzzleState), currentNode, "move left"));
                move("right");
                nodesCreated++;
            }

            if (move("right")) {
                frontier.push(new Node(new ArrayList<>(puzzleState), currentNode, "move right"));
                move("left");
                nodesCreated++;
            }

            if (move("up")) {
                frontier.push(new Node(new ArrayList<>(puzzleState), currentNode, "move up"));
                move("down");
                nodesCreated++;
            }

            if (move("down")) {
                frontier.push(new Node(new ArrayList<>(puzzleState), currentNode, "move down"));
                move("up");
                nodesCreated++;
            }
        }

        System.out.println("Error: maxnodes limit (" + maxNodes + ") reached");
        return;
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
}