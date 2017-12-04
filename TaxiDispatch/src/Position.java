public class Position{
    int row;
    int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public boolean equals(Position pos1) {
        if (this.row == pos1.row && this.col == pos1.col) {
            return true;
        }
        return false;
    }

    public static int dist(Position pos1, Position pos2) {
        return Math.abs(pos1.row - pos2.row) + Math.abs(pos1.col - pos2.col);
    }

    public String toString() {
        return "(" + this.row + ", " + this.col + ")";
    }

    public void update(int row, int col) {
        this.row = row;
        this.col = col;
    }
}
