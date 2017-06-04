import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


public class StateTest {

    BufferedReader br;

    @Test
    public void moveGen() throws Exception {

        File folder = new File("test/genmoves-tests");    // the test samples directory
        File[] listOfFiles = folder.listFiles();                    // list of all the files in directory

        // for each file in the samples directory
        for (int i = 0; i < listOfFiles.length; ++i) {

            File file = listOfFiles[i];     // name of the file

            // read .in files before .out files
            if (file.isFile() && file.getName().endsWith(".in")) {

                br = new BufferedReader(new FileReader(file));
                String line;

                String move = br.readLine();    // read in the player on move
                char turn = move.charAt(move.length()-1);

                char[][] board = new char[6][5];

                // read in the board
                for(int j = 0; j < 6; ++j) {
                    line = br.readLine();
                    for(int k = 0; k < 5; ++k)
                        board[j][k] = line.charAt(k);
                }

                State s = new State(turn, board);   // create a new state with the required board

                List<Move> moves = s.moveGen();    // generate moves for player on move
                List<String> moveStrings = new ArrayList<String>();   // extract the move strings from Move object
                for (Move m : moves)
                    moveStrings.add(m.move);

                // now read in correct answers from the corresponding .out file
                br = new BufferedReader(new FileReader(listOfFiles[i+1]));

                List<Move> correctMoves = new ArrayList<Move>();
                List<String> correctStrings = new ArrayList<String>(); // extract the move strings from Move object

                while ((line = br.readLine())!= null)
                    correctMoves.add(new Move(line));
                br.close();

                for (Move m : correctMoves)
                    correctStrings.add(m.move);

                // check that all our generated moves are exactly like the correct moves
                if(moveStrings.containsAll(correctStrings) && correctStrings.containsAll(moveStrings))
                    System.out.println(file + " -> passed");
                else
                    System.out.println(file + " -> failed");
            }
        }
    }

    @Test
    public void evaluate() throws Exception {

    }

}