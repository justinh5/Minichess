public class Move {

    public String move;
    public int score;

    public Piece fromPiece;
    public Piece toPiece;

    public int fromRank;
    public int fromFile;
    public int toRank;
    public int toFile;

    public Move(String move) {
        this.move = move;
        this.fromRank = getRow(move.charAt(1));
        this.fromFile = getColumn(move.charAt(0));
        this.toRank = getRow(move.charAt(4));
        this.toFile = getColumn(move.charAt(3));
    }

    public Move(String move, Piece fp, Piece tp) {

        this.move = move;
        this.fromPiece = fp;  // the piece that is moving on the board
        this.toPiece = tp;    // the piece on the board in the next square

        this.fromRank = getRow(move.charAt(1));
        this.fromFile = getColumn(move.charAt(0));
        this.toRank = getRow(move.charAt(4));
        this.toFile = getColumn(move.charAt(3));
    }

    /**
     * Convert the rank to an integer index for the board array.
     * @param rank The rank in char format.
     * @return Index row on the board.
     */
    private int getRow(char rank) {
        switch(rank) {
            case '1':
                return 5;
            case '2':
                return 4;
            case '3':
                return 3;
            case '4':
                return 2;
            case '5':
                return 1;
            case '6':
                return 0;
            default:
                throw new IllegalStateException();
        }
    }

    /**
     * Convert the file to an integer index for the board array.
     * @param file The file in char format.
     * @return Index column on the board.
     */
    private int getColumn(char file) {
        switch(file) {
            case 'a':
                return 0;
            case 'b':
                return 1;
            case 'c':
                return 2;
            case 'd':
                return 3;
            case 'e':
                return 4;
            default:
                throw new IllegalStateException();
        }
    }
}
