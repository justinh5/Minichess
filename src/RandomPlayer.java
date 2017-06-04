import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class RandomPlayer {

    public void play() {

        State s = new State();    // start state

        boolean finished = false;

        do {
            s.printBoard();

            List<Move> moves = s.bestMoves(s.moveGen());   // generate moves for player and prioritizes them

            int randomMove = ThreadLocalRandom.current().nextInt(0, moves.size());

            s.movePiece(moves.get(randomMove));     // next state

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

}
