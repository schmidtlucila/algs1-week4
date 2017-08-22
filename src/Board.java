import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by lucila on 20/08/17.
 */
public class Board {

    private final int hamming;
    private final int manhattan;
    private final int[][] blocks;

    private int dimension;
    private int blankRow;
    private int blankCol;
    private final int BLANK_INTERNAL_VALUE;
    private static final int BLANK_EXTERNAL_VALUE = 0;

    public Board(int[][] blocks) {
        this.dimension = blocks.length;
        this.blocks = Arrays.copyOf(blocks, dimension);
        this.BLANK_INTERNAL_VALUE = dimension * dimension;
        int[] sortedValues = computeSortedValues();
        this.hamming = computeHamming(sortedValues);
        this.manhattan = computeManhattan(sortedValues);
    }

    private int[] computeSortedValues() {
        int[] values = new int[dimension * dimension];
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                int index = matrixToArrayIndex(i, j);
                if (blocks[i][j] == BLANK_EXTERNAL_VALUE) {
                    blocks[i][j] = BLANK_INTERNAL_VALUE;
                }
                values[index] = blocks[i][j];
            }
        }
        Arrays.sort(values);
        return values;
    }

    private int matrixToArrayIndex(int i, int j) {
        return i * dimension() + j;
    }

    private int computeManhattan(int[] sortedValues) {
        int result = 0;
        for (int i = 0; i < blocks.length; i++) {
            int[] row = blocks[i];
            for (int j = 0; j < row.length; j++) {
                if (row[j] == BLANK_INTERNAL_VALUE) {
                    blankRow = i;
                    blankCol = j;
                } else if (row[j] != goalValueIn(i, j, sortedValues)) {
                    int goalRow = goalRow(row[j]);
                    int goalCol = goalCol(row[j]);
                    result = result + abs(goalRow - i) + abs(goalCol - j);
                }
            }
        }
        return result;
    }

    private int abs(int number) {
        if (number < 0) return -number;
        return number;
    }

    private int computeHamming(int[] sortedValues) {
        int result = 0;
        for (int i = 0; i < blocks.length; i++) {
            int[] row = blocks[i];
            for (int j = 0; j < row.length; j++) {
                if (row[j] != goalValueIn(i, j, sortedValues) && row[j] != BLANK_INTERNAL_VALUE) {
                    result++;
                }
            }
        }
        return result;
    }

    private int goalRow(int value) {
        return (value - 1) / dimension();
    }

    private int goalCol(int value) {
        return (value - 1) % dimension();
    }

    private int goalValueIn(int i, int j, int[] sortedValues) {
        int index = matrixToArrayIndex(i, j);
        return sortedValues[index];
    }

    // (where blocks[i][j] = block in row i, column j)
    public int dimension() {
        return dimension;
    }

    public int hamming() {
        return hamming;
    }

    public int manhattan() {
        return manhattan;
    }


    public boolean isGoal() {
        return manhattan == 0 && hamming == 0;
    }

    public Board twin() {
        int[][] copy = copy();
        if (blankRow != 0) {
            copy[0][1] = blocks[0][0];
            copy[0][0] = blocks[0][1];
        } else {
            copy[1][1] = blocks[1][0];
            copy[1][0] = blocks[1][1];
        }

        return new Board(copy);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Board board = (Board) o;
        if (board.dimension() != this.dimension()) return false;
        for (int i = 0; i < board.dimension(); i++) {
            for (int j = 0; j < board.dimension(); j++) {
                if (board.blocks[i][j] != this.blocks[i][j]) return false;
            }
        }
        return true;
    }

    public Iterable<Board> neighbors() {
        if (blankRow == 0) return neighborsFirstRow();
        if (blankRow == dimension() - 1) return neighborsLastRow();
        return neighborsMiddleRow();
    }

    private Iterable<Board> neighborsFirstRow() {
        Board[] boards;
        if (blankCol == 0) {
            boards = new Board[]{blankToRight(), blankToDown()};
        } else if (blankCol == dimension() - 1) {
            boards = new Board[]{blankToLeft(), blankToDown()};
        } else {
            boards = new Board[]{blankToLeft(), blankToRight(), blankToDown()};
        }

        return iterables(boards);
    }

    private Iterable<Board> neighborsLastRow() {
        Board[] boards;
        if (blankCol == 0) {
            boards = new Board[]{blankToRight(), blankToUp()};
        } else if (blankCol == dimension() - 1) {
            boards = new Board[]{blankToLeft(), blankToUp()};
        } else {
            boards = new Board[]{blankToLeft(), blankToRight(), blankToUp()};
        }

        return iterables(boards);
    }

    private Iterable<Board> neighborsMiddleRow() {
        Board[] boards;
        if (blankCol == 0) {
            boards = new Board[]{blankToRight(), blankToDown(), blankToUp()};
        } else if (blankCol == dimension() - 1) {
            boards = new Board[]{blankToLeft(), blankToDown(), blankToUp()};
        } else {
            boards = new Board[]{blankToLeft(), blankToRight(), blankToDown(), blankToUp()};
        }

        return iterables(boards);
    }


    private Iterable<Board> iterables(Board[] boards) {
        return new Iterable<Board>() {
            @Override
            public Iterator<Board> iterator() {
                return new Iterator<Board>() {
                    private Board[] list = boards;
                    private int current = 0;

                    @Override
                    public boolean hasNext() {
                        return current < list.length;
                    }

                    @Override
                    public Board next() {
                        Board item = list[current];
                        current++;
                        return item;
                    }
                };
            }
        };
    }

    private Board blankToLeft() {
        return exchBlankWith(blankRow, blankCol - 1);
    }

    private Board blankToRight() {
        return exchBlankWith(blankRow, blankCol + 1);
    }

    private Board blankToUp() {
        return exchBlankWith(blankRow - 1, blankCol);
    }

    private Board blankToDown() {
        return exchBlankWith(blankRow + 1, blankCol);
    }

    private Board exchBlankWith(int row, int col) {
        int[][] copy = copy();
        copy[row][col] = blocks[blankRow][blankCol];
        copy[blankRow][blankCol] = blocks[row][col];
        return new Board(copy);
    }

    private int[][] copy() {
        int[][] copy = new int[dimension()][dimension()];
        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j < dimension(); j++) {
                copy[i][j] = blocks[i][j];
            }
        }
        return copy;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(dimension);
        builder.append(System.getProperty("line.separator"));

        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j < dimension(); j++) {
                builder.append(" ");
                builder.append(toString(blocks[i][j]));
            }
            if (i != dimension() - 1) {
                builder.append(System.getProperty("line.separator"));
            }
        }
        return builder.toString();
    }              // string representation of this board (in the output format specified below)

    private String toString(int value) {
        if (value == BLANK_INTERNAL_VALUE) return String.valueOf(BLANK_EXTERNAL_VALUE);
        return String.valueOf(value);
    }

    public static void main(String[] args) {

        System.out.println("lalsdkfjaf");

    }

}
