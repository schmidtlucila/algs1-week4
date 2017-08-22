import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;

/**
 * Created by lucila on 20/08/17.
 */
public class Solver {

    private Board[] sequenceOfBoards;
    private boolean solvable;

    public Solver(Board initial) {
        if (initial == null) throw new IllegalArgumentException();
        MinPQ<SearchNode> boardPQ = new MinPQ<>();
        MinPQ<SearchNode> twinPQ = new MinPQ<>();

        SearchNode searchNode = new SearchNode(0, initial, null);
        SearchNode twinSearchNode = new SearchNode(0, initial.twin(), null);
        while (!searchNode.board.isGoal() && !twinSearchNode.board.isGoal()) {
            updateMinPQ(searchNode, boardPQ);
            updateMinPQ(twinSearchNode, twinPQ);

            searchNode = boardPQ.delMin();
            twinSearchNode = twinPQ.delMin();
        }

        if (searchNode.board.isGoal()) {
            solvable = true;
            sequenceOfBoards = new Board[searchNode.moves + 1];
            for (int i = searchNode.moves; i >= 0; i--) {
                sequenceOfBoards[i] = searchNode.board;
                if (i != 0) {
                    searchNode = searchNode.previous;
                }
            }
        }
    }          // find a solution to the initial board (using the A* algorithm)

    private void updateMinPQ(SearchNode searchNode, MinPQ<SearchNode> boardPQ) {
        Iterable<Board> neighbors = searchNode.board.neighbors();
        for (Board newBoard : neighbors) {
            if (searchNode.previous == null || !searchNode.previous.board.equals(newBoard)) {
                boardPQ.insert(new SearchNode(searchNode.moves + 1, newBoard, searchNode));
            }
        }
    }


    public boolean isSolvable() {
        return solvable;
    }

    public int moves() {
        return solvable ? sequenceOfBoards.length - 1 : -1;
    }                   // min number of moves to solve initial board; -1 if unsolvable

    public Iterable<Board> solution() {
        if (!solvable) return null;
        return new Iterable<Board>() {
            @Override
            public Iterator<Board> iterator() {
                return new Iterator<Board>() {
                    private Board[] list = sequenceOfBoards;
                    private int current = 0;

                    @Override
                    public boolean hasNext() {
                        return current < list.length;
                    }

                    @Override
                    public Board next() {
                        Board next = list[current];
                        current++;
                        return next;
                    }
                };
            }
        };
    }     // sequence of boards in a shortest solution; null if unsolvable

    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

    private class SearchNode implements Comparable<SearchNode> {
        private int moves;
        private Board board;
        private SearchNode previous;

        public SearchNode(int moves, Board board, SearchNode previous) {
            this.moves = moves;
            this.board = board;
            this.previous = previous;
        }

        private int weight() {
            return board.manhattan() + moves;
        }

        @Override
        public int compareTo(SearchNode o) {
            if (this.weight() == o.weight()) return 0;
            if (this.weight() < o.weight()) return -1;
            return 1;
        }
    }


}


