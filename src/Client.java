// Copyright (c) 2012 Bart Massey <bart@cs.pdx.edu>
// Copyright (c) 2017 Markus Ebner <markus-ebner@web.de>
// Licensed under the "MIT License"
// Please see the file COPYING at http://github.com/BartMassey/imcs

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Provides a quite complete interface to the Internet MiniChess Server.
 * <p>
 * See the <a href="http://wiki.cs.pdx.edu/minichess">MiniChess Page</a> for information.
 * The basic workflow is to create a client object (which conntects to the IMCS).
 * Login with your credentials, and then either join a game with the accept() method
 * or use the offerAndWait() method to offer an own game.
 *
 * The methods getMove() and sendMove() are used during a match, to receive the move
 * the opponent did, and send the own move to the opponent, respectively.
 * <p>
 */
public class Client {
    BufferedReader in;
    PrintStream out;

    /**
     * Enum of all commands that the IMCS supports.
     * The fields are exactly called like the commands but uppercase.
     * Thus, the enum.toString().toLowerCase() value is good to be sent over the line.
     */
    enum IMCSCommands {
        HELP,
        QUIT,
        ME,
        REGISTER,
        PASSWORD,
        LIST,
        RATINGS,
        OFFER,
        ACCEPT,
        CLEAN,
        RERATE
    }



    /* ######################## */
    /* # LIFE-TIME MANAGEMENT # */
    /* ######################## */

    /**
     * Create a new client connected to IMCS and logged on.
     * @param server  hostname or IP address of IMCS server, usually "imcs.svcs.cs.pdx.edu"
     * @param port  server port number, usually 3589
     * @throws IOException when the NetworkStream was unexpectedly closed.
     */
    public Client(String server, int port) throws IOException {
        Socket s = new Socket(server, port);
        InputStreamReader isr = new InputStreamReader(s.getInputStream());
        in = new BufferedReader(isr);
        out = new PrintStream(s.getOutputStream(), true);
        IMCSResponse versionResponse = awaitResponse();
        versionResponse.assertHasCode(100);
        if (!versionResponse.message.equals("imcs 2.5"))
            throw new Error("client: imcs version mismatch");
    }

    /**
     * Create a new client connected to IMCS and logged on.
     * @param server  hostname or IP address of IMCS server, usually "imcs.svcs.cs.pdx.edu"
     * @param portStr  server port number string, usually "3589"
     * @throws IOException when the NetworkStream was unexpectedly closed.
     */
    public Client(String server, String portStr) throws IOException {
        this(server, Integer.parseInt(portStr));
    }

    /**
     * Closes the connection to the server. Do not use the
     * object after this.
     */
    private void close() throws IOException {
        in.close();
        out.close();
    }







    /* ############################ */
    /* # GENERAL PROTOCOL METHODS # */
    /* ############################ */

    /**
     * Wait for a response on the line.
     * A response has the format:  [000] [Message]
     * @return Response, containing the response line split up in code and message.
     * @throws IOException when the NetworkStream was unexpectedly closed.
     */
    private IMCSResponse awaitResponse() throws IOException {
        IMCSResponse response = null;
        do {
            response = IMCSResponse.parse(in.readLine());
            if(response == null)
                throw new IOException("Broken pipe");
        } while(response == null);
        return response;
    }

    /**
     * Read a complete listing (after the response line) into a String array.
     * @return A list of items in the listing.
     * @throws IOException when the NetworkStream was unexpectedly closed.
     */
    private String awaitListing() throws IOException {
        String result = "";
        while(true) {
            String line = in.readLine();
            if(line == null)
                throw new IOException("Broken pipe");
            if(line.charAt(0) == '.')
                break;
            result += line + "\n";
        }
        return result;
    }

    /**
     * Send the given command with the given arguments to the IMCS.
     * @param command Command to send to the IMCS.
     * @param cmdArgs Arguments for the command that should be sent to the IMCS.
     */
    private void sendCommand(IMCSCommands command, Object... cmdArgs) {
        String cmdLine = command.toString().toLowerCase();
        for(Object cmdArg : cmdArgs)
            cmdLine += " " + cmdArg.toString();
        out.println(cmdLine);
        out.flush();
    }





    /* ################### */
    /* # COMMAND METHODS # */
    /* ################### */

    /**
     * Try to login to the IMCS with the given username and password.
     * @param username Username to use for logging into the IMCS.
     * @param password Password to use for logging into the IMCS.
     * @throws IOException when the NetworkStream was unexpectedly closed.
     * @throws RuntimeException When the required response code does not match the one received.
     */
    public void login(String username, String password) throws IOException, RuntimeException {
        sendCommand(IMCSCommands.ME, username, password);
        IMCSResponse response = awaitResponse();
        response.assertHasCode(201, 400);
        System.out.println(response.message + "\n");
    }


    /**
     * Get a list of all games known to the IMCS.
     * @return A list of all games known to the IMCS.
     * @throws IOException when the NetworkStream was unexpectedly closed.
     * @throws RuntimeException When the required response code does not match the one received.
     */
    public void getGameList() throws IOException, RuntimeException {
        sendCommand(IMCSCommands.LIST);
        IMCSResponse response = awaitResponse();
        response.assertHasCode(211);
        String list = awaitListing();
        System.out.println(list);
    }

    public void getRatingsList() throws IOException, RuntimeException {
        sendCommand(IMCSCommands.RATINGS);
        IMCSResponse response = awaitResponse();
        response.assertHasCode(212);
        String ratings = awaitListing();
        System.out.println(ratings);
    }


    public void cleanOffers() throws IOException, RuntimeException {
        sendCommand(IMCSCommands.CLEAN);
        IMCSResponse response = awaitResponse();
        response.assertHasCode(204, 406);
        System.out.println(response.message + "\n");
    }


    /**
     * Exit IMCS and close the connection
     * @throws IOException when the NetworkStream was unexpectedly closed.
     * @throws RuntimeException When the required response code does not match the one received.
     */
    public void quit() throws IOException, RuntimeException {
        sendCommand(IMCSCommands.QUIT);
        IMCSResponse response = awaitResponse();
        response.assertHasCode(200);
        System.out.println("\n" + response.message + "\n");
        close();
    }


    /**
     * Offer a default match where the Server selects the player.
     * @param player Player to play with in the offered game.
     * @throws IOException when the NetworkStream was unexpectedly closed.
     * @throws RuntimeException When the required response code does not match the one received.
     */
    public char offerGameAndWait(char player) throws IOException, RuntimeException {
        sendCommand(IMCSCommands.OFFER, player);
        IMCSResponse response = awaitResponse();
        response.assertHasCode(103); // game started
        System.out.println(response.message);
        response = awaitResponse();
        System.out.println(response.message);
        if(response.code == 105)
            return 'W';
        else if(response.code == 106)
            return 'B';
        throw new IOException("offer: unknown response code");
    }


    /**
     * Accept an offered match with the given gameId, requesting the given player.
     * @param gameId Id of the offered game to join.
     * @param player Player (either B or W) to request to play with in the game (may fail if already taken).
     * @throws IOException when the NetworkStream was unexpectedly closed.
     * @throws RuntimeException When the required response code does not match the one received.
     */
    public char accept(String gameId, char player) throws IOException, RuntimeException {
        sendCommand(IMCSCommands.ACCEPT, gameId, player);
        IMCSResponse response = awaitResponse();
        response.assertHasCode(105, 106); // game started
        System.out.println(response.message);
        if(response.code == 105)
            return 'W';
        else if(response.code == 106)
            return 'B';
        throw new IOException("accept: unknown response code");
    }




    /* ################ */
    /* # GAME METHODS # */
    /* ################ */

    /**
     * Get a move string from the IMCS server. Blocks
     * until move is received.
     * @return  opponent move string
     */
    public String getMove() throws IOException {
        String line;
        String oppMove = "";
        char ch;
        while (true) {
            line = in.readLine();
            if (line == null)
                return null;
            System.out.println(line);
            if (line.length() == 0)
                continue;
            ch = line.charAt(0);
            if(ch == '!')
                oppMove = line.substring(2);
            else if(ch == '=')
                return null;
            else if(ch == '?')
                return oppMove;
        }
    }

    /**
     * Send a move to the server.
     * @param moveStr  move string to send
     */
    public void sendMove(String moveStr) throws IOException {
        out.println(moveStr);
        out.flush();
    }

}