package ui;

import chess.ChessGame;
import datamodel.*;
import exception.UnauthorizedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import exception.*;

import java.util.Arrays;
import java.util.Scanner;

public class Client {
    public enum game_state {
        INIT, LOGGED_OUT, LOGGED_IN, PLAYING, OBSERVING
    }

    private game_state state;
    private ChessGame.TeamColor color;
    private String res;
    private final Scanner scanner;
    private final String url;
    ServerFacade facade;
    AuthData auth;

    public Client(String port){
        state = game_state.INIT;
        res = "";
        url = "http://localhost:" + port;
        scanner = new Scanner(System.in);
        facade = new ServerFacade(url);
    }

    // Loops the tick function
    public void loop(){
        while(!res.equals("quit")){
            tick();
        }
    }

    public void tick() {
        switch(state){
            case INIT:
                System.out.println("Welcome to chess! Type \"Help\" to list commands.");
                state = game_state.LOGGED_OUT;
                logout_help();
            case LOGGED_OUT:
                System.out.print("\n[LOGGED OUT: Not playing] >>> ");
                System.out.println(logged_out_eval(scanner.nextLine()));
                break;
            case LOGGED_IN:
                System.out.print("\n[LOGGED IN: Not playing] >>> ");
                System.out.println(logged_in_eval(scanner.nextLine()));
                break;
            case PLAYING:
                System.out.printf("\n[PLAYING: %s] >>> ", color);
                System.out.println(playing_eval(scanner.nextLine()));
                break;
            case OBSERVING:
                System.out.print("\n[OBSERVING] >>> ");
//                print_board_white();
                System.out.println(observing_eval(scanner.nextLine()));
                break;
            default:
                throw new IllegalStateException("No client state error");
        }
    }

    /*
    --------Eval Functions--------
     */
    // Reads the input and acts based on it when logged out
    private String logged_out_eval(String line){
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
                    state = game_state.LOGGED_IN;
                    login_help();
                    yield "\nRegistered";
                }
                case "l", "login" -> {
                    if(params.length < 2){
                        yield "Usage: login <USERNAME> <PASSWORD>";
                    }
                    auth = facade.login(new LoginData(params[0], params[1]));
                    state = game_state.LOGGED_IN;
                    login_help();
                    yield "\nLogged in";
                }
                case "q", "quit" -> {
                    res = "quit";
                    yield "Bye!";
                }
                case "h", "help" -> {
                    logout_help();
                    yield "";
                }
                default -> {
                    logout_help();
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

    private String logged_in_eval(String line){
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
                    else {
                        for(GameData game : gameList.games()){
                            System.out.printf("%d %s WhitePlayer: %s | BlackPlayer: %s\n",
                                    game.gameID(), game.gameName(), game.whiteUsername(), game.blackUsername());
                        }
                    }
                    yield "";
                }
                case "j", "join" -> {
                    if(params.length != 2){
                        yield "Usage: join <ID> [WHITE|BLACK]";
                    }
                    if(params[1].toUpperCase().equals("WHITE")){
                        color = ChessGame.TeamColor.WHITE;
                    } else if(params[1].toUpperCase().equals("BLACK")){
                        color = ChessGame.TeamColor.BLACK;
                    } else{
                        yield "Usage: join <ID> [WHITE|BLACK]";
                    }
                    facade.joinGame(new JoinRequest(auth.authToken(), new JoinData(params[1].toUpperCase(), Integer.parseInt(params[0]))));
                    state = game_state.PLAYING;
                    playing_help();
                    yield "\nJoined game " + params[0];
                }
                case "o", "observe" -> {
                    facade.listGames(auth.authToken());
                    state = game_state.OBSERVING;
                    observing_help();
                    yield "\nObserving";
                }
                case "lo", "logout" -> {
                    facade.logout(auth.authToken());
                    state = game_state.LOGGED_OUT;
                    logout_help();
                    yield "\nLogged out";
                }
                case "q", "quit" -> {
                    res = "quit";
                    yield "Bye!";
                }
                case "h", "help" -> {
                    login_help();
                    yield "\nHelp";
                }
                case "clear" -> {
                    facade.clear();
                    logout_help();
                    state = game_state.LOGGED_OUT;
                    yield "\nDatabase Cleared, logged out";
                }
                default -> {
                    login_help();
                    yield "\nBad command";
                }
            };
        } catch (BadRequestException ex){
            return "Fields not all complete";
        } catch (UnauthorizedException ex){
            return "Not authorized";
        } catch (AlreadyTakenException ex){
            return "Player already taken";
        } catch (Exception ex){
            return "logged_in_eval error";
        }
    }

    private String playing_eval(String line){
        try{
            String[] tokens = line.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "m", "move" -> {
                    yield "move";
                }
                case "s", "show" -> {
                    if(color == null || color == ChessGame.TeamColor.WHITE){
                        print_board_white();
                    } else{
                        print_board_black();
                    }
                    yield "show";
                }
                case "l", "leave" -> {
                    color = null;
                    state = game_state.LOGGED_IN;
                    yield "leave";
                }
                case "q", "quit" -> {
                    res = "quit";
                    yield "quit";
                }
                case "h", "help" -> {
                    playing_help();
                    yield "help";
                }
                default -> {
                    playing_help();
                    yield "bad command";
                }
            };
        } catch (Exception ex){
            return "playing_eval error";
        }
    }

    private String observing_eval(String line){
        try{
            String[] tokens = line.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "s", "show" -> {
                    print_board_white();
                    yield "show";
                }
                case "q", "quit" -> {
                    res = "quit";
                    yield "quit";
                }
                case "h", "help" -> {
                    observing_help();
                    yield "help";
                }
                case "l", "leave" -> {
                    state = game_state.LOGGED_IN;
                    yield "leave";
                }
                default -> {
                    observing_help();
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
    private void logout_help(){
        System.out.println(" \u001b[;;4mCommands:\u001b[;;0m");
        System.out.println("  \u001b[33;49;1m\"r\"/\"register\" <USERNAME> <EMAIL> <PASSWORD> \u001b[34;49;1m- to create an account");
        System.out.println("  \u001b[33;49;1m\"l\"/\"login\" <USERNAME> <PASSWORD> \u001b[34;49;1m- to play chess");
        System.out.println("  \u001b[33;49;1m\"q\"/\"quit\" \u001b[34;49;1m- playing chess");
        System.out.println("  \u001b[33;49;1m\"h\"/\"help\" \u001b[34;49;1m- with possible commands\u001b[;;0m");
    }

    private void login_help(){
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

    private void playing_help(){
        System.out.println(" \u001b[;;4mCommands:\u001b[;;0m");
        System.out.println("  \u001b[33;49;1m\"m\"/\"move\" <POS1> <POS2> \u001b[34;49;1m- a piece");
        System.out.println("  \u001b[33;49;1m\"s\"/\"show\" \u001b[34;49;1m- the board");
        System.out.println("  \u001b[33;49;1m\"l\"/\"leave\" \u001b[34;49;1m- the game");
        System.out.println("  \u001b[33;49;1m\"q\"/\"quit\" \u001b[34;49;1m- playing chess");
        System.out.println("  \u001b[33;49;1m\"h\"/\"help\" \u001b[34;49;1m- with possible commands\u001b[;;0m");
    }

    private void observing_help(){
        System.out.println(" \u001b[;;4mCommands:\u001b[;;0m");
        System.out.println("  \u001b[33;49;1m\"s\"/\"show\" \u001b[34;49;1m- the board");
        System.out.println("  \u001b[33;49;1m\"l\"/\"leave\" \u001b[34;49;1m- the game");
        System.out.println("  \u001b[33;49;1m\"q\"/\"quit\" \u001b[34;49;1m- playing chess");
        System.out.println("  \u001b[33;49;1m\"h\"/\"help\" \u001b[34;49;1m- with possible commands\u001b[;;0m");
    }

    /*
    --------Board Prints--------
     */
    // Sides: [30;100;1m
    // Blank White: [;107;1m
    // Blank Black: [;40;1m
    // White on White: [34;107;1m
    // White on Black: [34;40;1m
    // Black on White: [31;107;1m
    // Black on Black: [31;40;1m
    private void print_board_white(){
        System.out.println("\u001b[30;100;1m    a  b  c  d  e  f  g  h    \u001b[;;0m");
        System.out.println("\u001b[30;100;1m 8 \u001b[34;107;1m R \u001b[34;40;1m N \u001b[34;107;1m B " + //Black
                "\u001b[34;40;1m Q \u001b[34;107;1m K \u001b[34;40;1m B \u001b[34;107;1m N " +
                "\u001b[34;40;1m R \u001b[30;100;1m 8 \u001b[;;0m");
        System.out.println("\u001b[30;100;1m 7 \u001b[34;40;1m P \u001b[34;107;1m P \u001b[34;40;1m P " + //Black Pawns
                "\u001b[34;107;1m P \u001b[34;40;1m P \u001b[34;107;1m P \u001b[34;40;1m P " +
                "\u001b[34;107;1m P \u001b[30;100;1m 7 \u001b[;;0m");

        System.out.println("\u001b[30;100;1m 6 \u001B[;107;1m   \u001B[;40;1m   \u001B[;107;1m   \u001B[;40;1m   " +//W
                "\u001B[;107;1m   \u001B[;40;1m   \u001B[;107;1m   \u001B[;40;1m   \u001b[30;100;1m 6 \u001b[;;0m");
        System.out.println("\u001b[30;100;1m 5 \u001B[;40;1m   \u001B[;107;1m   \u001B[;40;1m   \u001B[;107;1m   " +//B
                "\u001B[;40;1m   \u001B[;107;1m   \u001B[;40;1m   \u001B[;107;1m   \u001b[30;100;1m 5 \u001b[;;0m");
        System.out.println("\u001b[30;100;1m 4 \u001B[;107;1m   \u001B[;40;1m   \u001B[;107;1m   \u001B[;40;1m   " +
                "\u001B[;107;1m   \u001B[;40;1m   \u001B[;107;1m   \u001B[;40;1m   \u001b[30;100;1m 4 \u001b[;;0m");
        System.out.println("\u001b[30;100;1m 3 \u001B[;40;1m   \u001B[;107;1m   \u001B[;40;1m   \u001B[;107;1m   " +
                "\u001B[;40;1m   \u001B[;107;1m   \u001B[;40;1m   \u001B[;107;1m   \u001b[30;100;1m 3 \u001b[;;0m");

        System.out.println("\u001b[30;100;1m 2 \u001b[31;107;1m P \u001b[31;40;1m P \u001b[31;107;1m P " + //White Pawns
                "\u001b[31;40;1m P \u001b[31;107;1m P \u001b[31;40;1m P \u001b[31;107;1m P " +
                "\u001b[31;40;1m P \u001b[30;100;1m 2 \u001b[;;0m");
        System.out.println("\u001b[30;100;1m 1 \u001b[31;40;1m R \u001b[31;107;1m N \u001b[31;40;1m B " + //White
                "\u001b[31;107;1m Q \u001b[31;40;1m K \u001b[31;107;1m B \u001b[31;40;1m N " +
                "\u001b[31;107;1m R \u001b[30;100;1m 1 \u001b[;;0m");
        System.out.println("\u001b[30;100;1m    a  b  c  d  e  f  g  h    \u001b[;;0m\n");
    }

    private void print_board_black(){
        System.out.println("\u001b[30;100;1m    h  g  f  e  d  c  b  a    \u001b[;;0m");
        System.out.println("\u001b[30;100;1m 1 \u001b[31;107;1m R \u001b[31;40;1m N \u001b[31;107;1m B " + //White
                "\u001b[31;40;1m K \u001b[31;107;1m Q \u001b[31;40;1m B \u001b[31;107;1m N " +
                "\u001b[31;40;1m R \u001b[30;100;1m 1 \u001b[;;0m");
        System.out.println("\u001b[30;100;1m 2 \u001b[31;40;1m P \u001b[31;107;1m P \u001b[31;40;1m P " + //White Pawns
                "\u001b[31;107;1m P \u001b[31;40;1m P \u001b[31;107;1m P \u001b[31;40;1m P " +
                "\u001b[31;107;1m P \u001b[30;100;1m 2 \u001b[;;0m");

        System.out.println("\u001b[30;100;1m 3 \u001B[;107;1m   \u001B[;40;1m   \u001B[;107;1m   \u001B[;40;1m   " +//W
                "\u001B[;107;1m   \u001B[;40;1m   \u001B[;107;1m   \u001B[;40;1m   \u001b[30;100;1m 3 \u001b[;;0m");
        System.out.println("\u001b[30;100;1m 4 \u001B[;40;1m   \u001B[;107;1m   \u001B[;40;1m   \u001B[;107;1m   " +//B
                "\u001B[;40;1m   \u001B[;107;1m   \u001B[;40;1m   \u001B[;107;1m   \u001b[30;100;1m 4 \u001b[;;0m");
        System.out.println("\u001b[30;100;1m 5 \u001B[;107;1m   \u001B[;40;1m   \u001B[;107;1m   \u001B[;40;1m   " +//W
                "\u001B[;107;1m   \u001B[;40;1m   \u001B[;107;1m   \u001B[;40;1m   \u001b[30;100;1m 5 \u001b[;;0m");
        System.out.println("\u001b[30;100;1m 6 \u001B[;40;1m   \u001B[;107;1m   \u001B[;40;1m   \u001B[;107;1m   " +//B
                "\u001B[;40;1m   \u001B[;107;1m   \u001B[;40;1m   \u001B[;107;1m   \u001b[30;100;1m 6 \u001b[;;0m");

        System.out.println("\u001b[30;100;1m 7 \u001b[34;107;1m P \u001b[34;40;1m P \u001b[34;107;1m P " + //Black Pawns
                "\u001b[34;40;1m P \u001b[34;107;1m P \u001b[34;40;1m P \u001b[34;107;1m P " +
                "\u001b[34;40;1m P \u001b[30;100;1m 7 \u001b[;;0m");
        System.out.println("\u001b[30;100;1m 8 \u001b[34;40;1m R \u001b[34;107;1m N \u001b[34;40;1m B " + //Black
                "\u001b[34;107;1m K \u001b[34;40;1m Q \u001b[34;107;1m B \u001b[34;40;1m N " +
                "\u001b[34;107;1m R \u001b[30;100;1m 8 \u001b[;;0m");
        System.out.println("\u001b[30;100;1m    h  g  f  e  d  c  b  a    \u001b[;;0m\n");
    }

    @Test
    void print_board_test(){
        print_board_white();
        print_board_black();
        Assertions.assertTrue(true);
    }
}
