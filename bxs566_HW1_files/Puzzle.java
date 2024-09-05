import java.io.File;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.HashSet;

public class Puzzle {
    private int[][] state;

    public Puzzle(int[][] state) {
        this.state = state;
    }

    public void cmd(String command) {
        if (Pattern.matches("^setState ([0-8] ){8}[0-8]$", command)) {
            setState(command.substring(9));
        } else if (Pattern.matches("^printState$", command)) {
            printState();  
        } else if (Pattern.matches("^move (up|down|left|right)$", command)) {
            move(command.substring(5));
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
        for(String value : values) {
            int val = Integer.parseInt(value);
            if (!(uniqueValues.add(val)))
                System.out.println("Error: invalid puzzle state");
        }

        Integer[] newState = uniqueValues.toArray(new Integer[9]);
        for (int i = 0; i < 9; i++) {
            this.state[i / 3][i % 3] = newState[i];
        }
    }

    private void printState() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int val = this.state[i][j];
                if (val == 0) {
                    System.out.print("  ");
                } else {
                    System.out.print(val + " ");
                }
            }
            System.out.println();
        }
    }

    private void move(String direction) {
        switch(direction) {
            case "up":
                break;
            case "down":
                break;
            case "left":
                break;
            case "right":
                break;
            default:
                System.out.println("Error: invalid move direction");
        }
    }

    private void scrambleState(int n) {

    }
}