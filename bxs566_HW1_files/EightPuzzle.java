import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

public class EightPuzzle {
    private List<Integer> state;

    public static void main (String[] args) {
        EightPuzzle puzzle = new EightPuzzle(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8});
        for (String cmdfile : args) {
            puzzle.cmdfile(cmdfile);
        }
    }

    public EightPuzzle(int[] state) {
        this.state = new ArrayList<Integer>();
        for (int value : state) {
            this.state.add(value);
        }
    }

    public void cmd(String command) {
        if (Pattern.matches("^setState ([0-8] ){8}[0-8]$", command)) {
            setState(command.substring(9));
        } else if (Pattern.matches("^printState$", command)) {
            printState();
        } else if (Pattern.matches("^move (up|down|left|right)$", command)) {
            if (!(move(command.substring(5)))) System.out.println("Error: invalid move");
        } else if (Pattern.matches("^scrambleState [0-9]+$", command)) {
            scrambleState(Integer.parseInt(command.substring(14)));
        } else if (Pattern.matches("^(#|//).*$", command)) {
            // do nothing
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

        this.state = newState;
    }

    private void printState() {
        String stateString = state.toString();
        System.out.println(
                stateString.substring(0, 9) + "\n" + stateString.substring(9, 18) + "\n" + stateString.substring(18));
    }

    private boolean move(String direction) {
        int zeroIndex = state.indexOf(0);
        int temp;
        switch (direction) {
            case "up":
                if (zeroIndex - 3 < 0) return false;
                temp = state.get(zeroIndex - 3);
                state.set(zeroIndex, temp);
                state.set(zeroIndex - 3, 0);
                break;
            case "down":
                if (zeroIndex - 5 > 0) return false;
                temp = state.get(zeroIndex + 3);
                state.set(zeroIndex, temp);
                state.set(zeroIndex + 3, 0);
                break;
            case "left":
                if (zeroIndex % 3 == 0) return false;
                temp = state.get(zeroIndex - 1);
                state.set(zeroIndex, temp);
                state.set(zeroIndex - 1, 0);
                break;
            case "right":
                if (zeroIndex % 3 == 2) return false;
                temp = state.get(zeroIndex + 1);
                state.set(zeroIndex, temp);
                state.set(zeroIndex + 1, 0);
                break;
            default:
                return false;
        }
        return true;
    }

    private void scrambleState(int n) {
        Map<Integer, String> moves = new HashMap<Integer, String>();
        moves.put(Integer.parseInt("0"), "up");
        moves.put(Integer.parseInt("1"), "down");
        moves.put(Integer.parseInt("2"), "left");
        moves.put(Integer.parseInt("3"), "right");
        Random rand = new Random(123456789);

        setState("0 1 2 3 4 5 6 7 8");
        while (n > 0) {
            String move = moves.get(rand.nextInt(4));
            if (move(move)) n--;
        }
    }
}