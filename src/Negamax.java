import java.util.Collections;
import java.util.List;
import java.util.Random;


public class Negamax {

    private State s = new State();

    private long seed = System.nanoTime();
    private TTable ttable = new TTable();

    private long timer_end;
    private int duration = 7900;   // milliseconds per turn

    private int INFINITY = Integer.MAX_VALUE;


    public void play() {

        boolean finished = false;

        do {
            // Start the timer for current player's turn
            this.timer_end = System.currentTimeMillis() + this.duration;

            s.printBoard();

            List<Move> moves = s.moveGen();   // generate moves for player and prioritizes them
            Collections.shuffle(moves, new Random(seed));
            List<Move> orderedMoves = s.bestMoves(moves);
            Move best = orderedMoves.get(0);

            // for each depth
            for(int d = 6; d < 41; d+=2) {

                System.out.println(d);

                int alpha = -INFINITY;
                int beta = INFINITY;
                Move depthBest = best;

                for(Move move: orderedMoves) {

                    s.movePiece(move);
                    int value = -negamax(s, d - 1, -beta, -alpha);
                    s.undoMove(move);

                    if (value > alpha) {
                        depthBest = move;
                        alpha = value;
                    }
                }

                // If we are out of time at this search depth
                if(System.currentTimeMillis() > this.timer_end)
                    break;

                best = depthBest;
            }

            s.movePiece(best);

            // check if a player won
            char status = s.checkFinalState();
            if (status != 'o') {
                if(status == '=')
                    System.out.println("Draw!");
                else if(status == 'B')
                    System.out.println("Black wins!");
                else if(status == 'W')
                    System.out.println("White wins!");
                finished = true;
            }

        } while(!finished);
    }


    public String online(String opponentMove) {

        // Opponent's turn
        if(opponentMove != null){
            Move oppMove = s.createMove(opponentMove);
            s.movePiece(oppMove);
            return null;
        }

        // Start the timer for current player's turn
        this.timer_end = System.currentTimeMillis() + this.duration;

        List<Move> moves = s.moveGen();   // generate moves for player and prioritizes them
        Collections.shuffle(moves, new Random(seed));     // shuffle moves
        List<Move> orderedMoves = s.bestMoves(moves);
        Move best = orderedMoves.get(0);

        // for each depth
        for(int d = 6; d < 41; d+=2) {

            int alpha = -INFINITY;
            int beta = INFINITY;
            Move depthBest = best;

            for (Move move : orderedMoves) {

                s.movePiece(move);
                int value = -negamax(s, d - 1, -beta, -alpha);
                s.undoMove(move);

                if (value > alpha) {
                    depthBest = move;
                    alpha = value;
                }
            }

            // If we are out of time at this search depth
            if (System.currentTimeMillis() > this.timer_end)
                break;

            best = depthBest;
        }

        s.movePiece(best);

        return best.move;
    }


    // Find the negamax value of a state using alpha-beta pruning.
    private int negamax(State s, int depth, int alpha, int beta) {

        if(System.currentTimeMillis() > this.timer_end)
            return 0;

        if((s.checkFinalState() != 'o') || (depth == 0))
            return s.evaluate();

        List<Move> moves = s.moveGen();   // generate moves for player and prioritizes them
        Collections.shuffle(moves, new Random(seed));     // shuffle moves
        List<Move> orderedMoves = s.bestMoves(moves);

        int bestValue = -INFINITY;

        for (Move move : orderedMoves) {
            s.movePiece(move);
            int value = -negamax(s, depth - 1, -beta, -alpha);
            bestValue = Integer.max(bestValue, value);
            alpha = Integer.max(alpha, value);
            s.undoMove(move);     // put the piece back

            if (alpha >= beta)
                break;
        }
        return bestValue;
    }



    char[][] board2 = new char[][] {
            { 'k', 'q', 'b', 'n', 'r'},
            { 'p', 'p', '.', 'p', 'p'},
            { '.', '.', 'p', '.', '.'},
            { '.', '.', 'P', '.', '.'},
            { 'P', 'P', '.', 'P', 'P'},
            { 'R', 'N', 'B', 'Q', 'K'}
    };


    // Find the negamax value of a state with alpha-beta pruning and the transposition table.
    private int Tnegamax(State s, int depth, int alpha, int beta) {

        if(System.currentTimeMillis() > this.timer_end)
            return 0;

        if((s.checkFinalState() != 'o') || (depth == 0))
            return s.evaluate();

        /*boolean match = Arrays.deepEquals(s.board, board2);

        if(match) {
            System.out.println(s.hash);
            s.printBoard();
        }*/

        TTableEntry T = ttable.lookup(s.hash);    // is the value of this state in the table?

        if((T != null) && ((T.alpha < T.value) || (T.value < T.beta)) && (T.depth >= depth))
            return T.value;

        List<Move> moves = s.moveGen();   // generate moves for player and prioritizes them
        Collections.shuffle(moves, new Random(seed));     // shuffle moves
        List<Move> orderedMoves = s.bestMoves(moves);

        int bestValue = -INFINITY;

        for (Move move : orderedMoves) {
            s.movePiece(move);
            int value = -Tnegamax(s, depth - 1, -beta, -alpha);
            bestValue = Integer.max(bestValue, value);
            alpha = Integer.max(alpha, value);
            s.undoMove(move);     // put the piece back

            if (alpha >= beta)
                break;
        }

        // store ttable entry
        T = new TTableEntry(s.hash, depth, alpha, beta, bestValue);
        ttable.store(T);

        return bestValue;
    }
}
