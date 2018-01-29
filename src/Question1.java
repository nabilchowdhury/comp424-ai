import java.util.*;

class PuzzleGame {
    static final int[][] moves = {{-1,0}, {1,0}, {0,-1}, {0,1}};
    private final int rows, cols;
    private final int[][] board;

    /**
     * Random initializer constructor
     * @param r The number of rows in the board
     * @param c The number of columns in the board
     */
    public PuzzleGame(int r, int c) {
        rows = r;
        cols = c;
        board = new int[rows][cols];
        randomInitialize(board);
    }

    /**
     * Initialize puzzle with initial configuration
     * @param initialConfig The initial configuration (2d array)
     */
    public PuzzleGame(int[][] initialConfig) {
        if (initialConfig == null || initialConfig.length < 2 || initialConfig[0].length < 2) {
            throw new IllegalArgumentException("Please pass in a valid 2D configuration");
        }
        rows = initialConfig.length;
        cols = initialConfig[0].length;
        board = initialConfig;
    }

    /**
     * Generates a unique hash code for a particular configuration of the board
     * @param board The board configuration to hash
     * @return The hash code
     */
    private int hashBoard(int[][] board) {
        int hashCode = 0;
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                hashCode = hashCode * 10 + board[i][j];
            }
        }
        return hashCode;
    }

    /**
     * Randomly intialize the board
     * @param board
     */
    private void randomInitialize(int[][] board) {
        // Fill the board in order
        int val = 0;
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                board[i][j] = val++;
            }
        }

        // Do random swaps to randomize it
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < rows * cols; ++i) {
            int pi = random.nextInt(rows);
            int pj = random.nextInt(cols);
            int qi = random.nextInt(rows);
            int qj = random.nextInt(cols);
            swap(board, pi, pj, qi, qj);
        }
    }

    /**
     * Helper for swapping elements in 2d array
     */
    private void swap(int[][] arr, int xa, int ya, int xb, int yb) {
        int t = arr[xa][ya];
        arr[xa][ya] = arr[xb][yb];
        arr[xb][yb] = t;
    }

    /**
     * Pretty prints the board
     */
    public void print() {
        for (int[] row : board) {
            System.out.println(Arrays.toString(row));
        }
    }

    /**
     * Pretty prints the board
     */
    public void print(int[][] board) {
        for (int[] row : board) {
            System.out.println(Arrays.toString(row));
        }
    }

    /**
     * Perform BFS to get to goal state
     * @param goalState The final state of the board
     */
    public void breadthFirstSearch(int[][] goalState) {
        int goalHashCode = hashBoard(goalState);
        SearchTreeNode root = new SearchTreeNode(board);
        Queue<SearchTreeNode> queue = new LinkedList<>();
        Set<Integer> seen = new HashSet<>();
        queue.add(root);
        while(!queue.isEmpty()) {
            SearchTreeNode curState = queue.poll();
            int[][] curBoard = curState.val;
            int hashCode = hashBoard(curBoard);
            if (hashCode == goalHashCode) {
                curState.printSearchPath();
                return;
            }
            if (seen.contains(hashCode)) continue;
            seen.add(hashCode);

            int si = -1, sj = -1;
            for (int i = 0; i < rows; ++i) {
                for (int j = 0; j < cols; ++j) {
                    if (curBoard[i][j] == 0) {
                        si = i;
                        sj = j;
                        break;
                    }
                }
            }

            System.out.println(si + " " + sj);

            for (int[] move : moves) {
                int x = si + move[0];
                int y = sj + move[1];

                if (x >= 0 && x < rows && y >= 0 && y < cols) {
                    int[][] child = curBoard.clone();
                    swap(child, x, y, si, sj);
                    SearchTreeNode childState = new SearchTreeNode(child);
                    childState.parent = curState;
                    queue.add(childState);
                }
            }
        }

        for (int i : seen) {
            System.out.println(i);
        }

        System.out.println("No solution exists!");
    }

}

class SearchTreeNode {
    int[][] val;
    SearchTreeNode parent;
    SearchTreeNode(int[][] n) {
        val = n;
    }

    void printSearchPath() {
        if (parent == null) return;
        parent.printSearchPath();
        for (int[] row : val) {
            System.out.println(Arrays.toString(row));
        }
    }
}

public class Question1 {
    public static void main(String[] args) {
        int[][] initialState = {{1,4,2}, {5,3,0}};
        int[][] goalState = {{1,4,0}, {5,3,2}};
        PuzzleGame puzzle = new PuzzleGame(initialState);
        puzzle.breadthFirstSearch(goalState);
    }
}
