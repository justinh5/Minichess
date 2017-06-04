import java.io.IOException;
import java.util.Scanner;


public class IMCSPlayer {

    Client c;                                      // IMCS client object
    Negamax n = new Negamax();                     // negamax search object
    Scanner scan = new Scanner(System.in);         // scanner for user input
    private String host = "imcs.svcs.cs.pdx.edu";  // IMCS host
    private String port = "3589";                  // IMCS port

    /**
     * Constructor sets up a connection to the IMCS client
     */
    public IMCSPlayer() {

        // Establish a new connection with the IMCS server
        try {
            this.c = new Client(host, port);
            System.out.println("Connected to the server!\n");
        }
        catch(IOException e) {
            System.out.println("Error connecting to server!\n" + e);
        }
    }


    /**
     * A menu of options for the user to choose from. Does not include all
     * commands from the original list of IMCS commands. The application
     * automatically closes the connection and quits after a match is finished.
     */
    public void menu() {

        // List all available options to choose from
        boolean go = true;
        String choice;

        while(go) {

            System.out.print("1) Login\n" +
                    "2) List available games\n" +
                    "3) Ratings\n" +
                    "4) Offer game\n" +
                    "5) Accept game\n" +
                    "6) Clean offers\n" +
                    "7) Quit\n\nSelect: ");

            choice = scan.nextLine();   // user's command

            switch(Character.getNumericValue(choice.charAt(0))) {
                case 1:
                    login();
                    break;
                case 2:
                    listGames();
                    break;
                case 3:
                    ratings();
                    break;
                case 4:
                    offerGame();
                    go = false;
                    break;
                case 5:
                    acceptGame();
                    go = false;
                    break;
                case 6:
                    clean();
                    break;
                case 7:
                    quit();
                    go = false;
                    break;
            }
        }
    }


    /**
     * Login to an IMCS account.
     */
    private void login() {
        // Get the username and password from the user
        System.out.println("Please input your username: ");
        String username = scan.nextLine();
        System.out.println("Please input your password: ");
        String password = scan.nextLine();

        try {
            c.login(username, password);
        }
        catch(IOException e) {
            System.out.println("Error logging in!\n" + e);
        }
    }


    /**
     * List all available games in IMCS.
     */
    private void listGames() {
        try {
            c.getGameList();
        }
        catch (IOException e) {
            System.out.println("Error with server response\n" + e);
        }
    }


    /**
     * List all best scores in IMCS.
     */
    private void ratings() {
        try {
            c.getRatingsList();
        }
        catch (IOException e) {
            System.out.println("Error with server response\n" + e);
        }
    }


    /**
     * Offer a game in IMCS. Must choose a color (B,W,?)
     */
    private void offerGame() {
        System.out.println("Choose a color (W, B, ?): ");
        char color = scan.nextLine().charAt(0);
        if(color == 'W' || color == 'B' || color == '?') {
            try {
                color = c.offerGameAndWait(color);
                playGame(color);
            }
            catch (IOException e) {
                System.out.println("Error with server response\n" + e);
            }
        }
    }


    /**
     * Accept a game in IMCS.
     */
    private void acceptGame() {
        System.out.println("Game ID: ");
        String ID = scan.nextLine();
        System.out.println("Choose a color (W, B, ?): ");
        char color = scan.nextLine().charAt(0);

        try {
            color = c.accept(ID, color);
            playGame(color);
        }
        catch (IOException e) {
            System.out.println("Error with server response\n" + e);
        }
    }


    /**
     * Clean all your outstanding match offers.
     */
    private void clean() {
        try {
            c.cleanOffers();
        }
        catch (IOException e) {
            System.out.println("Error with server response" + e);
        }
    }


    /**
     * Quit IMCS.
     */
    private void quit() {
        try {
            c.quit();
        }
        catch (IOException e) {
            System.out.println("Error with server response" + e);
        }
    }


    /**
     * Play a game as white or black until the match is finished.
     * @param color your color to play with.
     */
    private void playGame(char color) {

        boolean finished = false;
        String myMove = "";
        String opponentMove;

        while(!finished){

            if(color == 'W') {    // we play as white
                try {
                    if(myMove.isEmpty())
                        c.getMove();
                    myMove = n.online(null);
                    c.sendMove(myMove);
                    opponentMove = c.getMove();
                    if(opponentMove != null)
                        n.online(opponentMove);
                    else
                        finished = true;
                }
                catch (IOException e) {
                    System.out.println("Error with server response" + e);
                }
            }
            else {        // we play as black
                try {
                    opponentMove = c.getMove();
                    if (opponentMove != null)
                        n.online(opponentMove);
                    else
                        finished = true;
                    if(!finished) {
                        myMove = n.online(null);
                        c.sendMove(myMove);
                    }
                }
                catch (IOException e) {
                    System.out.println("Error with server response" + e);
                }
            }
        }
    }
}
