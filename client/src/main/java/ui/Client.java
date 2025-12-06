package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import datamodel.*;
import exception.UnauthorizedException;
import jakarta.websocket.DeploymentException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import exception.*;
import websocket.commands.UserConnectCommand;
import websocket.commands.UserGameCommand;
import websocket.commands.UserMoveCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Scanner;

public class Client {
    public enum GameState {
        INIT, LOGGED_OUT, LOGGED_IN, PLAYING, OBSERVING
    }

    private GameState state;
    private ChessGame.TeamColor color;
    private String res;
    private final Scanner scanner;
    private final ServerFacade facade;
    private final String uriString;
    private WebSocketFacade wsFacade;
    private AuthData auth;
    private int gameID;
    private long lastTime;

    public Client(String port) {
        state = GameState.INIT;
        res = "";
        String url = "http://localhost:" + port;
        uriString = "ws://localhost:" + port + "/ws";
        scanner = new Scanner(System.in);
        facade = new ServerFacade(url);
        lastTime = System.currentTimeMillis();
    }

    // Loops the tick function
    public void loop(){
        while(!res.equals("quit")){
            long now = System.currentTimeMillis();
            if(now - lastTime >= 100) {
                lastTime = now;
                tick();
            }
        }
    }

    public void tick() {
        switch(state){
            case INIT:
                System.out.println("Welcome to chess! Type \"Help\" to list commands.");
                state = GameState.LOGGED_OUT;
                logoutHelp();
            case LOGGED_OUT:
                System.out.print("\n[LOGGED OUT: Not playing] >>> ");
                System.out.println(loggedOutEval(scanner.nextLine()));
                break;
            case LOGGED_IN:
                System.out.print("\n[LOGGED IN: Not playing] >>> ");
                System.out.println(loggedInEval(scanner.nextLine()));
                break;
            case PLAYING:
                System.out.printf("\n[PLAYING: %s] >>> ", color);
                System.out.println(playingEval(scanner.nextLine()));
                break;
            case OBSERVING:
                System.out.print("\n[OBSERVING] >>> ");
//                print_board_white();
                System.out.println(observingEval(scanner.nextLine()));
                break;
            default:
                throw new IllegalStateException("No client state error");
        }
        lastTime = System.currentTimeMillis();
    }

    /*
    --------Eval Functions--------
     */
    // Reads the input and acts based on it when logged out
    private String loggedOutEval(String line){
        try{
            String[] tokens = line.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "r", "register" -> {
                    if(params.length < 3){
                        yield "Usage: register <USERNAME> <EMAIL> <PASSWORD>";
                    }
                    auth = facade.register(new UserData(params[0], params[1], params[2]));
                    state = GameState.LOGGED_IN;
                    loginHelp();
                    yield "\nRegistered";
                }
                case "l", "login" -> {
                    if(params.length < 2){
                        yield "Usage: login <USERNAME> <PASSWORD>";
                    }
                    auth = facade.login(new LoginData(params[0], params[1]));
                    state = GameState.LOGGED_IN;
                    loginHelp();
                    yield "\nLogged in";
                }
                case "q", "quit" -> {
                    res = "quit";
                    yield "Bye!";
                }
                case "h", "help" -> {
                    logoutHelp();
                    yield "";
                }
                default -> {
                    logoutHelp();
                    yield "\nBad Command";
                }
            };
        } catch (BadRequestException ex){
            return "Fields not all complete";
        } catch (UnauthorizedException ex){
            return "Wrong password";
        } catch (AlreadyTakenException ex){
            return "Username already taken";
        } catch (Exception ex){
            return "logged_out_eval error";
        }
    }

    private String loggedInEval(String line){
        try{
            String[] tokens = line.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "c", "create" -> {
                    if(params.length != 1){
                        yield "Usage: create <NAME>";
                    }
                    int gameID = facade.createGame(auth.authToken(), params[0]);
                    System.out.printf("GameID: %d\n", gameID);
                    yield "\nGame created";
                }
                case "li", "list" -> {
                    ListGamesResponse gameList = facade.listGames(auth.authToken());
                    if(gameList.games().isEmpty()){
                        yield "\nNo games";
                    }
                    for(GameData game : gameList.games()){
                        System.out.printf("%d %s WhitePlayer: %s | BlackPlayer: %s\n",
                                game.gameID(), game.gameName(), game.whiteUsername(), game.blackUsername());
                    }
                    yield "";
                }
                case "j", "join" -> {
                    if(params.length != 2){
                        yield "Usage: join <ID> [WHITE|BLACK]";
                    }
                    String inputColor = params[1].toUpperCase();
                    if(inputColor.equals("WHITE")){
                        color = ChessGame.TeamColor.WHITE;
                    } else if(inputColor.equals("BLACK")){
                        color = ChessGame.TeamColor.BLACK;
                    } else{
                        yield "Usage: join <ID> [WHITE|BLACK]";
                    }
                    gameID = Integer.parseInt(params[0]);
                    facade.joinGame(new JoinRequest(auth.authToken(), new JoinData(inputColor, gameID)));
                    wsFacade = new WebSocketFacade(uriString);
                    wsFacade.send(new UserConnectCommand(auth.authToken(), gameID, color));
                    state = GameState.PLAYING;
                    playingHelp();
                    yield "\nJoined game " + gameID;
                }
                case "o", "observe" -> {
                    wsFacade = new WebSocketFacade(uriString);
                    state = GameState.OBSERVING;
                    observingHelp();
                    yield "\nObserving";
                }
                case "lo", "logout" -> {
                    facade.logout(auth.authToken());
                    state = GameState.LOGGED_OUT;
                    logoutHelp();
                    yield "\nLogged out";
                }
                case "q", "quit" -> {
                    facade.logout(auth.authToken());
                    res = "quit";
                    yield "Bye!";
                }
                case "h", "help" -> {
                    loginHelp();
                    yield "\nHelp";
                }
                case "clear" -> {
                    facade.clear();
                    logoutHelp();
                    state = GameState.LOGGED_OUT;
                    yield "\nDatabase Cleared, logged out";
                }
                default -> {
                    loginHelp();
                    yield "\nBad command";
                }
            };
        } catch (NumberFormatException e) {
            return "<ID> must be an int";
        } catch (BadRequestException ex){
            return "Fields not all complete";
        } catch (UnauthorizedException ex){
            return "Not authorized";
        } catch (AlreadyTakenException ex){
            return "Player already taken";
        } catch (URISyntaxException | DeploymentException | IOException ex){
            return "Websocket creation error";
        } catch (Exception ex){
            return "logged_in_eval error";
        }
    }

    private String playingEval(String line){
        try{
            String[] tokens = line.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "m", "move" -> {
                    if(params.length != 2) {
                        yield "Usage: move <POS1> <POS2>";
                    }
                    if((params[0].length() != 2) || (params[1].length() != 2)){
                        yield "<POS> must be a <CHAR-INT>. Ex: <A3>";
                    }
                    int oldCol = params[0].toUpperCase().charAt(0) - 64;
                    int oldRow = params[0].charAt(1) - 48;
                    int newCol = params[1].toUpperCase().charAt(0) - 64;
                    int newRow = params[1].charAt(1) - 48;
                    if((oldRow < 1 || oldRow > 8) || (oldCol < 1 || oldCol > 8))
                        yield "<POS1> must be a valid position on chess board";
                    if((newRow < 1 || newRow > 8) || (newCol < 1 || newCol > 8))
                        yield "<POS2> must be a valid position on chess board";
                    ChessPosition oldPos = new ChessPosition(oldRow, oldCol);
                    ChessPosition newPos = new ChessPosition(newRow, newCol);
                    wsFacade.send(new UserMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, auth.authToken(), gameID,
                            new ChessMove(oldPos, newPos, null)));
                    yield "move";
                }
                case "s", "show" -> {
                    yield BoardPrinter.printBoard(color);
                }
                case "l", "leave" -> {
                    wsFacade.send(new UserGameCommand(UserGameCommand.CommandType.LEAVE, auth.authToken(), gameID));
                    color = null;
                    state = GameState.LOGGED_IN;
                    yield "leave";
                }
                case "q", "quit" -> {
                    res = "quit";
                    yield "quit";
                }
                case "h", "help" -> {
                    playingHelp();
                    yield "help";
                }
                default -> {
                    playingHelp();
                    yield "bad command";
                }
            };
        } catch (NumberFormatException e) {
            return "<POS> must be a <CHAR-INT>. Ex: <A3>";
        } catch (Exception ex){
            return "playing_eval error: " + ex.getMessage();
        }
    }

    private String observingEval(String line){
        try{
            String[] tokens = line.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if(params.length > 0){
                return "Too many arguments";
            }
            return switch (cmd) {
                case "s", "show" -> {
                    BoardPrinter.printBoard(ChessGame.TeamColor.WHITE);
                    yield "show";
                }
                case "q", "quit" -> {
                    res = "quit";
                    yield "quit";
                }
                case "h", "help" -> {
                    observingHelp();
                    yield "help";
                }
                case "l", "leave" -> {
                    state = GameState.LOGGED_IN;
                    yield "leave";
                }
                default -> {
                    observingHelp();
                    yield "bad command";
                }
            };
        } catch (Exception ex){
            return "observing_eval error";
        }
    }

    /*
    --------Prints--------
    */
    // Underline: [;;4m
    // None: [;;0m
    // Orange: [33;49;1m
    // Blue: [34;49;1m
    private void logoutHelp(){
        System.out.println(" \u001b[;;4mCommands:\u001b[;;0m");
        System.out.println("  \u001b[33;49;1m\"r\"/\"register\" <USERNAME> <EMAIL> <PASSWORD> \u001b[34;49;1m- to create an account");
        System.out.println("  \u001b[33;49;1m\"l\"/\"login\" <USERNAME> <PASSWORD> \u001b[34;49;1m- to play chess");
        System.out.println("  \u001b[33;49;1m\"q\"/\"quit\" \u001b[34;49;1m- playing chess");
        System.out.println("  \u001b[33;49;1m\"h\"/\"help\" \u001b[34;49;1m- with possible commands\u001b[;;0m");
    }

    private void loginHelp(){
        System.out.println(" \u001b[;;4mCommands:\u001b[;;0m");
        System.out.println("  \u001b[33;49;1m\"c\"/\"create\" <NAME> \u001b[34;49;1m- a game");
        System.out.println("  \u001b[33;49;1m\"li\"/\"list\" \u001b[34;49;1m- games");
        System.out.println("  \u001b[33;49;1m\"j\"/\"join\" <ID> [WHITE|BLACK] \u001b[34;49;1m- a game");
        System.out.println("  \u001b[33;49;1m\"o\"/\"observe\" <ID> \u001b[34;49;1m- a game");
        System.out.println("  \u001b[33;49;1m\"lo\"/\"logout\" \u001b[34;49;1m- when you are done");
        System.out.println("  \u001b[33;49;1m\"q\"/\"quit\" \u001b[34;49;1m- playing chess");
        System.out.println("  \u001b[33;49;1m\"clear\" \u001b[34;49;1m- the database");
        System.out.println("  \u001b[33;49;1m\"h\"/\"help\" \u001b[34;49;1m- with possible commands\u001b[;;0m");
    }

    private void playingHelp(){
        System.out.println(" \u001b[;;4mCommands:\u001b[;;0m");
        System.out.println("  \u001b[33;49;1m\"m\"/\"move\" <POS1> <POS2> \u001b[34;49;1m- a piece");
        System.out.println("  \u001b[33;49;1m\"s\"/\"show\" \u001b[34;49;1m- the board");
        System.out.println("  \u001b[33;49;1m\"l\"/\"leave\" \u001b[34;49;1m- the game");
        System.out.println("  \u001b[33;49;1m\"q\"/\"quit\" \u001b[34;49;1m- playing chess");
        System.out.println("  \u001b[33;49;1m\"h\"/\"help\" \u001b[34;49;1m- with possible commands\u001b[;;0m");
    }

    private void observingHelp(){
        System.out.println(" \u001b[;;4mCommands:\u001b[;;0m");
        System.out.println("  \u001b[33;49;1m\"s\"/\"show\" \u001b[34;49;1m- the board");
        System.out.println("  \u001b[33;49;1m\"l\"/\"leave\" \u001b[34;49;1m- the game");
        System.out.println("  \u001b[33;49;1m\"q\"/\"quit\" \u001b[34;49;1m- playing chess");
        System.out.println("  \u001b[33;49;1m\"h\"/\"help\" \u001b[34;49;1m- with possible commands\u001b[;;0m");
    }
}
