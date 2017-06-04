import java.util.Random;


public class ZobristTable {

    private long[][] ztable;    // 30 x 13 table
    private long white;
    private long black;

    private int[][] squares = new int[][]{
            { 0, 1, 2, 3, 4 },
            { 5, 6, 7, 8, 9 },
            { 10, 11, 12, 13, 14},
            { 15, 16, 17, 18, 19},
            { 20, 21, 22, 23, 24},
            { 25, 26, 27, 28, 29}
    };

    private final char[][] startBoard = new char[][] {
        { 'k', 'q', 'b', 'n', 'r'},
        { 'p', 'p', 'p', 'p', 'p'},
        { '.', '.', '.', '.', '.'},
        { '.', '.', '.', '.', '.'},
        { 'P', 'P', 'P', 'P', 'P'},
        { 'R', 'N', 'B', 'Q', 'K'}
    };


    public ZobristTable() {

        Random rand = new Random();

        ztable = new long[30][13];   // 30 squares on the board and 13 total piece types

        for(int i = 0; i < 30; ++i) {
            for(int j = 0; j < 13; ++j)
                ztable[i][j] = rand.nextLong();
        }

        // Two random values for each side
        white = rand.nextLong();
        black = rand.nextLong();
    }


    /**
     * Computes a simple hash for a state. Only used for the
     * starting state because we use a more simplified computation
     * for the rest of the states based off of this one.
     * @return the hash of the starting board state.
     */
    public long startHash() {

        long hash = 0;

        for(int i = 0; i < 6; ++i) {
            for(int j = 0; j < 5; ++j) {
                int column = getColumn(this.startBoard[i][j]);
                hash ^= this.ztable[squares[i][j]][column];
            }
        }
        hash ^= black;    // white moves first
        return hash;
    }


    /*public long hash(Move m, long prevHash) {

        int sourceSquare = this.squares[m.fromRank][m.fromFile];
        int destSquare = this.squares[m.toRank][m.toFile];

        int sourceCol = getColumn(m.fromPiece.identity);
        int destCol = getColumn(m.toPiece.identity);

        long prevSource = this.ztable[sourceSquare][sourceCol];
        long prevDest = this.ztable[destSquare][destCol];

        long newSource = this.ztable[sourceSquare][12];
        long newDest = this.ztable[destSquare][sourceCol];

        return prevHash ^ newSource ^ prevSource ^ newDest ^ prevDest ^ white ^ black;
    }*/


    /**
     * Computes the Zobrist hash of a board state.
     * @param m, the move to get to the new state.
     * @param prevHash, the hash of the old state.
     * @return the hash of the new state.
     */
    public long hash(Move m, long prevHash) {

        int sourceSquare = this.squares[m.fromRank][m.fromFile];
        int destSquare = this.squares[m.toRank][m.toFile];

        int sourceCol = getColumn(m.fromPiece.identity);
        int destCol = getColumn(m.toPiece.identity);

        long prevSource = this.ztable[sourceSquare][sourceCol];
        long prevDest = this.ztable[destSquare][destCol];

        long newSource = this.ztable[sourceSquare][12];
        long newDest = this.ztable[destSquare][sourceCol];

        // change the destination if there is pawn promotion
        if(m.fromPiece.identity == 'p' && m.toRank == 5)
            newDest = this.ztable[destSquare][1];
        else if(m.fromPiece.identity == 'P' && m.toRank == 0)
            newDest = this.ztable[destSquare][7];

        return prevHash ^ newSource ^ prevSource ^ prevDest ^ newDest ^ white ^ black;
    }


    /*
     * Backup hash function that computes the hash using the value
     * of each individual cell. This is much slower than the preferred method.
     */
    public long hash2(State s) {

        long hash = 0;

        for(int i = 0; i < 6; ++i) {
            for(int j = 0; j < 5; ++j) {
                int column = getColumn(s.board[i][j]);
                hash ^= this.ztable[squares[i][j]][column];
            }
        }
        hash ^= s.turn;
        return hash;
    }


    /**
     * @return the column index of a type of piece.
     */
    private int getColumn(char p) throws UnknownError {

        switch (p) {
            case 'k':
                return 0;
            case 'q':
                return 1;
            case 'r':
                return 2;
            case 'b':
                return 3;
            case 'n':
                return 4;
            case 'p':
                return 5;
            case 'K':
                return 6;
            case 'Q':
                return 7;
            case 'R':
                return 8;
            case 'B':
                return 9;
            case 'N':
                return 10;
            case 'P':
                return 11;
            case '.':
                return 12;
            default:
                throw new UnknownError();
        }
    }
}
