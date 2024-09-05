public class Main {
    public static void main (String[] args) {
        EightPuzzle puzzle = new EightPuzzle(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8});
        for (String cmdfile : args) {
            puzzle.cmdfile(cmdfile);
        }
    }
}