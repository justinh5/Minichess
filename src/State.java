import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.abs;


public class State {

    public char[][] board;   // the current state of the board

    private Piece o = new Piece('.', 0, 0);         // blank square piece
    private Piece k, q, b, n, r, p1, p2, p3, p4, p5;            // individual black pieces
    private Piece K, Q, B, N, R, P1, P2, P3, P4, P5;            // individual white pieces
    private List<Piece> blackPieces = new ArrayList<Piece>();   // list of black's pieces
    private List<Piece> whitePieces = new ArrayList<Piece>();   // list of white's pieces

    public char turn;         // player on move
    private int moveCount;    // the current move number

    private ZobristTable ztable = new ZobristTable();    // Zobrist table object
    public long hash;                                    // current hash of the state

    // All board coordinates
    private String[][] map = new String[][] {
            { "a6", "b6", "c6", "d6", "e6"},
            { "a5", "b5", "c5", "d5", "e5"},
            { "a4", "b4", "c4", "d4", "e4"},
            { "a3", "b3", "c3", "d3", "e3"},
            { "a2", "b2", "c2", "d2", "e2"},
            { "a1", "b1", "c1", "d1", "e1"}
    };


    /**
     * Constructor initializes starting board, move count, and piece lists.
     */
    public State() {
        this.turn = 'W';      // white always moves first
        this.moveCount = 1;

        this.k = new Piece('k', 0, 0); this.q = new Piece('q', 0, 1);
        this.b = new Piece('b', 0, 2); this.n = new Piece('n', 0, 3);
        this.r = new Piece('r', 0, 4); this.p1 = new Piece('p', 1, 0);
        this.p2 = new Piece('p', 1, 1); this.p3 = new Piece('p', 1, 2);
        this.p4 = new Piece('p', 1, 3); this.p5 = new Piece('p', 1, 4);

        this.K = new Piece('K', 5, 4); this.Q = new Piece('Q', 5, 3);
        this.B = new Piece('B', 5, 2); this.N = new Piece('N', 5, 1);
        this.R = new Piece('R', 5, 0); this.P1 = new Piece('P', 4, 0);
        this.P2 = new Piece('P', 4, 1); this.P3 = new Piece('P', 4, 2);
        this.P4 = new Piece('P', 4, 3); this.P5 = new Piece('P', 4, 4);

        this.blackPieces.addAll(Arrays.asList(this.k, this.q, this.b, this.n, this.r,
                this.p1, this.p2, this.p3, this.p4, this.p5));
        this.whitePieces.addAll(Arrays.asList(this.K, this.Q, this.B, this.N, this.R,
                this.P1, this.P2, this.P3, this.P4, this.P5));

        this.board = new char[][] {
                { 'k', 'q', 'b', 'n', 'r'},
                { 'p', 'p', 'p', 'p', 'p'},
                { '.', '.', '.', '.', '.'},
                { '.', '.', '.', '.', '.'},
                { 'P', 'P', 'P', 'P', 'P'},
                { 'R', 'N', 'B', 'Q', 'K'}
        };

        this.hash = ztable.startHash();
    }


    public State(char turn, char[][] setBoard) {

        this.turn = turn;
        this.board = new char[6][5];

        this.k = new Piece('k', 0, 0); this.q = new Piece('q', 0, 1);
        this.b = new Piece('b', 0, 2); this.n = new Piece('n', 0, 3);
        this.r = new Piece('r', 0, 4); this.p1 = new Piece('p', 1, 0);
        this.p2 = new Piece('p', 1, 1); this.p3 = new Piece('p', 1, 2);
        this.p4 = new Piece('p', 1, 3); this.p5 = new Piece('p', 1, 4);

        this.K = new Piece('K', 5, 4); this.Q = new Piece('Q', 5, 3);
        this.B = new Piece('B', 5, 2); this.N = new Piece('N', 5, 1);
        this.R = new Piece('R', 5, 0); this.P1 = new Piece('P', 4, 0);
        this.P2 = new Piece('P', 4, 1); this.P3 = new Piece('P', 4, 2);
        this.P4 = new Piece('P', 4, 3); this.P5 = new Piece('P', 4, 4);

        this.blackPieces.addAll(Arrays.asList(this.k, this.q, this.b, this.n, this.r,
                this.p1, this.p2, this.p3, this.p4, this.p5));
        this.whitePieces.addAll(Arrays.asList(this.K, this.Q, this.B, this.N, this.R,
                this.P1, this.P2, this.P3, this.P4, this.P5));

        for (Piece p : blackPieces)
            p.captured = true;
        for (Piece p : whitePieces)
            p.captured = true;

        for(int i = 0; i < 6; ++i) {
            for (int j = 0; j < 5; ++j) {
                this.board[i][j] = setBoard[i][j];
                if(this.board[i][j] != '.')
                    updatePiece(this.board[i][j], i, j);
            }
        }
    }


    public void updatePiece(char piece, int rank, int file) {

        List<Piece> pieces = Character.isLowerCase(piece) ? this.blackPieces : this.whitePieces;

        for (Piece p : pieces){
            if(piece == p.identity) {
                if(p.captured) {
                    p.rank = rank;
                    p.file = file;
                    p.captured = false;
                    return;
                }
                else if(piece == 'q') {
                    for (Piece p2 : this.blackPieces) {
                        if(p2.identity == 'p' && p2.captured) {
                            p2.identity = 'q';
                            p2.rank = rank;
                            p2.file = file;
                            p2.captured = false;
                            return;
                        }
                    }
                }
                else if(piece == 'Q') {
                    for (Piece p2 : this.whitePieces) {
                        if(p2.identity == 'P' && p2.captured) {
                            p2.identity = 'Q';
                            p2.rank = rank;
                            p2.file = file;
                            p2.captured = false;
                            return;
                        }
                    }
                }
            }
        }
    }


    /**
     * Move any piece on the board. Increase move count.
     * Promote pawns if they reach the opposite end of the board.
     * @param move The move object to manipulate state with.
     */
    public void movePiece(Move move) {

        //this.hash = ztable.hash(move, this.hash);

        // set capture to true
        move.toPiece.captured = true;

        move.fromPiece.rank = move.toRank;
        move.fromPiece.file = move.toFile;

        board[move.fromRank][move.fromFile] = '.';                   // make previous space empty
        board[move.toRank][move.toFile] = move.fromPiece.identity;   // move piece

        // promote pawn if opposite end of the board is reached
        if(move.fromPiece.identity == 'p' && move.toRank == 5) {
            board[move.toRank][move.toFile] = 'q';
            move.fromPiece.identity = 'q';
            move.fromPiece.promoted = moveCount;
        }
        else if(move.fromPiece.identity == 'P' && move.toRank == 0) {
            board[move.toRank][move.toFile] = 'Q';
            move.fromPiece.identity = 'Q';
            move.fromPiece.promoted = moveCount;
        }

        this.turn = this.turn == 'W' ? 'B' : 'W';   // switch turn
        ++this.moveCount;    // increase move count
    }


    /**
     * Undo a move on the board and return the new state.
     * @param move The move object to revert to previous state.
     */
    public void undoMove(Move move) {

        this.turn = this.turn == 'W' ? 'B' : 'W';   // switch turn
        --this.moveCount;

        // unpromote pawn if necessary
        if(move.fromPiece.promoted == moveCount) {
            if(move.fromPiece.identity == 'q')
                move.fromPiece.identity = 'p';
            else
                move.fromPiece.identity = 'P';
            move.fromPiece.promoted = 0;
        }

        // un-capture piece
        move.toPiece.captured = false;
        board[move.toRank][move.toFile] = move.toPiece.identity;
        board[move.fromRank][move.fromFile] = move.fromPiece.identity;

        move.fromPiece.rank = move.fromRank;
        move.fromPiece.file = move.fromFile;
        //this.hash = ztable.hash(move, this.hash);    // undo the hash
    }


    /**
     *  Types of capture scenarios when moving a piece.
     *  Pawns, as an example, can ONLY capture when they move
     *  diagonally, and cannot capture when they move forward.
     */
    public enum Capture {
        TRUE, FALSE, ONLY
    }


    /**
     * Generate a list moves for the current player's turn. Once a piece is found
     * on the board, the moves are found by symmetrically scanning all the legal
     * surrounding squares, including captures.
     * @return A list of moves for the current player.
     */
    public List<Move> moveGen() {

        List<Move> moves = new ArrayList<Move>();    // list of moves

        List<Piece> currentPlayerPieces = this.turn == 'W' ? this.whitePieces : this.blackPieces;

        for(Piece p : currentPlayerPieces) {
            if(!p.captured) {
                switch(Character.toLowerCase(p.identity)) {
                    case 'k':
                        moves.addAll(symmScan(p.file, p.rank, 0, 1, true, Capture.TRUE));
                        moves.addAll(symmScan(p.file, p.rank, 1, 1, true, Capture.TRUE));
                        break;
                    case 'q':
                        moves.addAll(symmScan(p.file, p.rank, 0, 1, false, Capture.TRUE));
                        moves.addAll(symmScan(p.file, p.rank, 1, 1, false, Capture.TRUE));
                        break;
                    case 'b':
                        moves.addAll(symmScan(p.file, p.rank, 0, 1, true, Capture.FALSE));
                        moves.addAll(symmScan(p.file, p.rank, 1, 1, false, Capture.TRUE));
                        break;
                    case 'n':
                        moves.addAll(symmScan(p.file, p.rank, 1, 2, true, Capture.TRUE));
                        moves.addAll(symmScan(p.file, p.rank, -1, 2, true, Capture.TRUE));
                        break;
                    case 'r':
                        moves.addAll(symmScan(p.file, p.rank, 0, 1, false, Capture.TRUE));
                        break;
                    case 'p':
                        int dir = this.turn == 'B' ? 1 :-1;  // set direction for black or white pawn
                        moves.addAll(moveScan(p.file, p.rank, -1, dir, true, Capture.ONLY));
                        moves.addAll(moveScan(p.file, p.rank, 1, dir, true, Capture.ONLY));
                        moves.addAll(moveScan(p.file, p.rank, 0, dir, true, Capture.FALSE));
                        break;
                }
            }
        }
        return moves;
    }


    /**
     * Symmetrically scan a position in 4 directions and return the list of moves found.
     * @param x0 Original x-coordinate on the board.
     * @param y0 Original y-coordinate on the board.
     * @param dx Change in x-direction.
     * @param dy Change in y-direction.
     * @param stopShort Stop short after traveling one square.
     * @param capture The capture type.
     */
    private List<Move> symmScan(int x0, int y0, int dx, int dy, boolean stopShort, Capture capture) {

        List<Move> moves = new ArrayList<Move>();

        for(int i = 0; i < 4; ++i) {
            moves.addAll(moveScan(x0, y0, dx, dy, stopShort, capture));
            int temp = dx;
            dx = dy;
            dy = temp;
            dy *= -1;
        }
        return moves;
    }


    /**
     * Finds all moves, traveling in one direction on the board.
     * @param x0 Original x-coordinate on the board.
     * @param y0 Original y-coordinate on the board.
     * @param dx Change in x-direction.
     * @param dy Change in y-direction.
     * @param stopShort Stop short after traveling one square.
     * @param capture The capture type.
     */
    private List<Move> moveScan(int x0, int y0, int dx, int dy, boolean stopShort, Capture capture) {

        int x = x0;
        int y = y0;
        char c = this.turn;                        // color of current player
        char oc = this.turn == 'W' ? 'B' : 'W';    // opponent color
        List<Move> moves = new ArrayList<Move>();
        Piece fromPiece = identifyPiece(this.turn, y0, x0);
        Piece toPiece;

        do {
            x += dx;
            y += dy;
            toPiece = o;    // default blank piece
            if(x < 0 || x > 4 || y < 0 || y > 5)   // out of bounds
                break;
            char piece = board[y][x];    // get the piece
            if(piece != '.') {           // there is a piece at x, y
                if((Character.isLowerCase(piece) && c == 'B') || (Character.isUpperCase(piece) && c == 'W')) // our own piece
                    break;
                if(capture == Capture.FALSE)
                    break;
                stopShort = true;     // cannot go past another piece on the board
                toPiece = identifyPiece(oc, y, x);
            }
            else if(capture == Capture.ONLY)  // if only a capture is allowed
                break;

            moves.add(new Move((map[y0][x0]+ "-" + map[y][x]), fromPiece, toPiece));

        } while (!stopShort);
        return moves;
    }

    /**
     * Scans the lists of white and black pieces to find the
     * piece that matches the given rank and file.
     * @return Reference to a piece object.
     */
    public Piece identifyPiece(char color, int rank, int file) {

        List<Piece> pieceList = color == 'W' ? whitePieces : blackPieces;

        for (Piece p : pieceList) {
            if(!p.captured && rank == p.rank && file == p.file)
                return p;
        }
        // the piece must be found or else the program is not working correctly
        throw new IllegalStateException();
    }


    /**
     * Used in tournaments to identify moves that opponent makes.
     * @param opponentMove The move the online opponent has chosen.
     * @return Opponent's move in the form of a move object.
     */
    public Move createMove(String opponentMove) {

        char c = this.turn;
        char op = this.turn == 'W' ? 'B': 'W';

        Move move = new Move(opponentMove);
        move.fromPiece = identifyPiece(c, move.fromRank, move.fromFile);
        if(this.board[move.toRank][move.toFile] != '.')
            move.toPiece = identifyPiece(op, move.toRank, move.toFile);
        else
            move.toPiece = o;

        return move;
    }


    /**
     * Evaluate the state for the side on move. A positive value indicates
     * an advantage for the side on move, and a negative value a disadvantage.
     * The total points for current player's pieces are subtracted from the
     * total of the opponent's pieces. Also makes heuristic guesses as to
     * which positions are better for pieces on the board.
     * @return score of evaluated state.
     */
    public int evaluate() {

        int black = 0;
        int white = 0;

        if(!this.k.captured) {
            black += 1000;
            if(this.k.rank != 0 || this.k.file != 0)   // king has been forced to move
                black -= 90;
        }
        if(!this.q.captured) {
            black += 900;
            if(this.q.rank != 0 || this.q.file != 1)
                black -= 5;
        }
        if(!this.r.captured) {
            black += 500;
            if(this.r.rank != 0 || this.r.file != 4)
                black += 50;
        }
        if(!this.b.captured) {
            black += 300;
            if(this.b.rank != 0 || this.b.file != 2)
                black += 50;
        }
        if(!this.n.captured) {
            black += 300;
            if(this.n.rank != 0 || this.n.file != 3)
                black += 50;
        }

        if(!this.K.captured) {
            white += 1000;
            if(this.K.rank != 5 || this.K.file != 4)   // king has been forced to move
                white -= 90;
        }
        if(!this.Q.captured) {
            white += 900;
            if(this.Q.rank != 5 || this.Q.file != 3)
                white -= 5;
        }
        if(!this.R.captured) {
            white += 500;
            if(this.R.rank != 5 || this.R.file != 0)
                white += 50;
        }
        if(!this.B.captured) {
            white += 300;
            if(this.B.rank != 5 || this.B.file != 2)
                white += 50;
        }
        if(!this.N.captured) {
            white += 300;
            if(this.N.rank != 5 || this.N.file != 1)
                white += 50;
        }

        for (Piece p : this.blackPieces) {
            if(!p.captured) {
                if(p.rank != 0 && p.rank != 5 && p.file != 0 && p.file != 4)
                    black += 10;
                if(p.identity == 'p') {
                    black += 100;
                    black += (this.p2.rank - 1) * 50;
                }
            }
        }

        for (Piece p : this.whitePieces) {
            if(!p.captured) {
                if(p.rank != 0 && p.rank != 5 && p.file != 0 && p.file != 4)
                    white += 10;
                if(p.identity == 'P') {
                    white += 100;
                    white += abs(this.P3.rank - 4) * 50;
                }
            }
        }

        return turn == 'W' ? white-black : black-white;
    }



    /**
     * Sorts a player's moves by score in descending order.
     * @param moves Randomly shuffled move list.
     * @return A list of the best sorted moves for a player.
     */
    public List<Move> bestMoves(List<Move> moves) {

        List<Move> sorted = new ArrayList<Move>();

        for (Move move : moves) {
            movePiece(move);
            move.score = -evaluate();       // negate opponent's best scores
            undoMove(move);
        }

        // Adds moves to the sorted moves list in descending order
        while(!moves.isEmpty()) {
            Move best = moves.get(0);
            for (Move move : moves) {
                if(move.score > best.score)
                    best = move;
            }
            sorted.add(best);
            moves.remove(best);
        }

        return sorted;
    }


    /**
     * Check if the board is in a final state, where one side
     * is missing a king or the maximum amount of moves has
     * been used up. Return different char codes to signal the
     * status of the game.
     * @return board status code.
     */
    public char checkFinalState() {

        if(this.k.captured)
            return 'W';
        if(this.K.captured)
            return 'B';

        if(moveCount == 40)    // the game is a draw
            return '=';
        return 'o';            // game isn't over yet
    }



    /**
     * Print a state of the board. Mostly for debugging.
     */
    void printBoard() {

        System.out.println(String.format("%d %s", moveCount, turn));
        for(int i = 0; i < 6; ++i)
            System.out.println(board[i]);
        System.out.println();
    }
}
