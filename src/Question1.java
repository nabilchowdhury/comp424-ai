import java.util.*;

class PuzzleGame {
    static final int MIN_DIM = 2;
    static final int[][] moves = {{-1,0}, {1,0}, {0,-1}, {0,1}};
    private final int rows, cols;
    private final int[][] board;

    /**
     * Random initializer constructor
     * @param r The number of rows in the board
     * @param c The number of columns in the board
     */
    public PuzzleGame(int r, int c) {
        rows = Math.max(r, MIN_DIM);
        cols = Math.max(c, MIN_DIM);
        board = new int[rows][cols];
        randomInitialize(board);
    }

    /**
     * Initialize puzzle with initial configuration
     * @param initialConfig The initial configuration (2d array)
     */
    public PuzzleGame(int[][] initialConfig) {
        if (initialConfig == null || initialConfig.length < MIN_DIM || initialConfig[0].length < MIN_DIM) {
            throw new IllegalArgumentException("Please pass in a valid 2D configuration");
        }
        rows = initialConfig.length;
        cols = initialConfig[0].length;
        board = initialConfig;
    }

    // HELPERS
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
     * Randomly initialize the board
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
    private void print(int[][] board) {
        for (int[] row : board) {
            System.out.println(Arrays.toString(row));
        }
    }

    /**
     * Deep copy the state
     * @param state The board state to be copied
     * @return A copy of state
     */
    private int[][] copyState(int[][] state) {
        if (state == null) return null;
        int[][] ret = new int[state.length][state[0].length];
        for (int i = 0; i < state.length; ++i) {
            for (int j = 0; j < state[0].length; ++j) ret[i][j] = state[i][j];
        }

        return ret;
    }

    /**
     * Perform BFS to get to goal state
     * @param goalState The final state of the board
     */
    public boolean breadthFirstSearch(int[][] goalState) {
        int goalHashCode = hashBoard(goalState); // The hash value of the goal state we want to check against
        SearchTreeNode root = new SearchTreeNode(board); // The node corresponding to the initial state of the board
        Queue<SearchTreeNode> queue = new LinkedList<>(); // BFS queue
        Set<Integer> seen = new HashSet<>(); // Store hash values

        queue.add(root);
        while(!queue.isEmpty()) {
            SearchTreeNode curState = queue.poll();
            int[][] curBoard = curState.val;
            int hashCode = hashBoard(curBoard);
            if (hashCode == goalHashCode) {
                System.out.println("Solution found, printing solution path...");
                curState.printSearchPath();
                return true;
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

            // Check all neighboring states and add them if valid
            PriorityQueue<SearchTreeNode> orderByLowestPiece = new PriorityQueue<>((SearchTreeNode o1, SearchTreeNode o2) -> o1.priority - o2.priority);

            for (int[] move : moves) {
                int x = si + move[0];
                int y = sj + move[1];

                if (x >= 0 && x < rows && y >= 0 && y < cols) {
                    int[][] child = copyState(curBoard);
                    swap(child, x, y, si, sj);
                    SearchTreeNode childState = new SearchTreeNode(child);
                    childState.priority = curBoard[x][y];
                    childState.parent = curState;
                    orderByLowestPiece.add(childState);
                }
            }

            // Now that the children are ordered by lowest piece, as required by the assignment, add them to the BFS queue
            while (!orderByLowestPiece.isEmpty()) queue.add(orderByLowestPiece.poll());

        }

        System.out.println("No solution exists!");
        return false;
    }

    /**
     * Perform uniform cost search with unit cost. The path will be exactly that of BFS since cost == 1 between adjacent states
     * @param goalState The final state of the board
     */
    public boolean uniformCostSearch(int[][] goalState) {
        int goalHashCode = hashBoard(goalState); // The hash value of the goal state we want to check against
        SearchTreeNode root = new SearchTreeNode(board); // The node corresponding to the initial state of the board
        PriorityQueue<SearchTreeNode> pQueue = new PriorityQueue<>(new Comparator<SearchTreeNode>() {
            @Override
            public int compare(SearchTreeNode o1, SearchTreeNode o2) {
                if (o1.distance == o2.distance) return o1.priority - o2.priority;
                return o1.distance - o2.distance;
            }
        });
        Set<Integer> seen = new HashSet<>(); // Store hash values

        pQueue.add(root);
        while(!pQueue.isEmpty()) {
            SearchTreeNode curState = pQueue.poll();
            int[][] curBoard = curState.val;
            int hashCode = hashBoard(curBoard);
            if (hashCode == goalHashCode) {
                System.out.println("Solution found, printing solution path...");
                curState.printSearchPath();
                return true;
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

            // Check all neighboring states and add them if valid
            PriorityQueue<SearchTreeNode> orderByLowestPiece = new PriorityQueue<>((SearchTreeNode o1, SearchTreeNode o2) -> o1.priority - o2.priority);

            for (int[] move : moves) {
                int x = si + move[0];
                int y = sj + move[1];

                if (x >= 0 && x < rows && y >= 0 && y < cols) {
                    int[][] child = copyState(curBoard);
                    swap(child, x, y, si, sj);
                    SearchTreeNode childState = new SearchTreeNode(child);
                    childState.priority = curBoard[x][y];
                    childState.distance = curState.distance + 1; // Unit cost
                    childState.parent = curState;
                    orderByLowestPiece.add(childState);
                }
            }

            // Now that the children are ordered by lowest value piece moved, as required by the assignment, add them to the DFS stack
            while (!orderByLowestPiece.isEmpty()) pQueue.add(orderByLowestPiece.poll());

        }

        System.out.println("No solution exists!");
        return false;
    }

    /**
     * Perform DFS to get to goal state
     * @param goalState The final state of the board
     */
    public boolean depthFirstSearch(int[][] goalState) {
        return depthFirstSearchHelper(goalState, -1);
    }

    /**
     * Run DFS but only up to a maximum depth.
     * @param goalState The final state of the board
     * @param maxDepth The maximum allowable depth of the search
     */
    public boolean depthLimitedSearch(int[][] goalState, int maxDepth) {
        return depthFirstSearchHelper(goalState, maxDepth);
    }


    /**
     * Repeatedly calls depthLimitedSearch with increasing depths from 0 to maxDepth.
     * @param goalState The final state of the board
     * @param maxDepth The maximum allowable depth of the search
     */
    public boolean iterativeDeepeningSearch(int[][] goalState, int maxDepth) {
        for (int depth = 0; depth < maxDepth; ++depth) {
            if (depthLimitedSearch(goalState, depth)) return true;
        }
        return false;
    }

    /**
     * Pretty prints the initial board configuration
     */
    public void print() {
        for (int[] row : board) {
            System.out.println(Arrays.toString(row));
        }
    }


    /**
     * A DFS helper that caters to both standard DFS and Depth Limited Search.
     * @param goalState The final state of the board
     * @param maxDepth the maximum allowable depth of the search
     */
    private boolean depthFirstSearchHelper(int[][] goalState, int maxDepth) {
        int goalHashCode = hashBoard(goalState); // The hash value of the goal state we want to check against
        SearchTreeNode root = new SearchTreeNode(board); // The node corresponding to the initial state of the board
        Stack<SearchTreeNode> stack = new Stack<>(); // DFS stack
        Set<Integer> seen = new HashSet<>(); // Store hash values

        stack.add(root);
        while(!stack.isEmpty()) {
            SearchTreeNode curState = stack.pop();
            int[][] curBoard = curState.val;
            int hashCode = hashBoard(curBoard);
            if (hashCode == goalHashCode) {
                System.out.println("Solution found, printing solution path...");
                curState.printSearchPath();
                return true;
            }
            if (seen.contains(hashCode) || (maxDepth >= 0 && curState.distance == maxDepth)) continue;
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

            // Check all neighboring states and add them if valid
            PriorityQueue<SearchTreeNode> orderByLowestPiece = new PriorityQueue<>((SearchTreeNode o1, SearchTreeNode o2) -> o2.priority - o1.priority);

            for (int[] move : moves) {
                int x = si + move[0];
                int y = sj + move[1];

                if (x >= 0 && x < rows && y >= 0 && y < cols) {
                    int[][] child = copyState(curBoard);
                    swap(child, x, y, si, sj);
                    SearchTreeNode childState = new SearchTreeNode(child);
                    childState.priority = curBoard[x][y];
                    childState.distance = maxDepth >= 0 ? curState.distance + 1 : 0;
                    childState.parent = curState;
                    orderByLowestPiece.add(childState);
                }
            }

            // Now that the children are ordered by lowest value piece moved, as required by the assignment, add them to the DFS stack
            while (!orderByLowestPiece.isEmpty()) stack.push(orderByLowestPiece.poll());

        }

        System.out.println("No solution exists!");
        return false;
    }

}

class SearchTreeNode {
    int[][] val;
    int priority;
    int distance;
    SearchTreeNode parent;

    SearchTreeNode(int[][] n) {
        val = n;
        priority = 0;
        distance = 0;
    }

    /**
     * Recurse up and print path in order from root -> leaf
     */
    void printSearchPath() {
        if (parent != null) parent.printSearchPath();
        for (int[] row : val) {
            System.out.println(Arrays.toString(row));
        }
        System.out.println();
    }
}

public class Question1 {
    public static void main(String[] args) {
        int[][] initialState = {{1,4,2}, {5,3,0}};
        int[][] goalState = {{0,1,2}, {5,4,3}};
        PuzzleGame puzzle = new PuzzleGame(initialState);
        puzzle.depthLimitedSearch(goalState, 10);
    }
}
