public class Piece {

    public char identity;       // type of piece on the board
    public int rank;            // rank by array index
    public int file;            // file by array index
    public boolean captured;    // is this piece off the board?
    public int promoted;        // movecount when pawn was promoted

    public Piece(char ident, int r, int f) {
        this.identity = ident;
        this.rank = r;
        this.file = f;
        this.captured = false;
        this.promoted = 0;
    }
}
