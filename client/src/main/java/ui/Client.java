package ui;

import chess.*;

import java.util.Arrays;
import java.util.Scanner;

public class Client {
    public enum game_state {
        INIT, LOGGED_OUT, LOGGED_IN, PLAYING, OBSERVING
    }

    private game_state state;
    private ChessGame.TeamColor color;
    private String res;
    private Scanner scanner;

    public Client(){
        init();
    }

    private void init(){
        state = game_state.INIT;
        res = "";
        scanner = new Scanner(System.in);
    }

    public void loop(){
        while(!res.equals("quit")){
            tick();
        }
    }

    // Reads the input and acts based on it when logged out
    private String logged_out_eval(String line){
        try{
            String[] tokens = line.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "r", "register" -> {
                    state = game_state.LOGGED_IN;
                    login_help();
                    yield "register";
                }
                case "l", "login" -> {
                    state = game_state.LOGGED_IN;
                    login_help();
                    yield "login";
                }
                case "q", "quit" -> {
                    res = "quit";
                    yield "quit";
                }
                case "h", "help" -> {
                    logout_help();
                    yield "help";
                }
                default -> {
                    logout_help();
                    yield "bad command";
                }
            };
        } catch (Exception ex){
            System.err.print("logged_out_eval error");
            return ex.getMessage();
        }
    }

    private String logged_in_eval(String line){
        try{
            String[] tokens = line.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "c", "create" -> "create";
                case "li", "list" -> "list";
                case "j", "join" -> {
                    state = game_state.PLAYING;
                    playing_help();
                    yield "join";
                }
                case "o", "observe" -> {
                    state = game_state.OBSERVING;
                    playing_help();
                    yield "observe";
                }
                case "lo", "logout" -> {
                    state = game_state.LOGGED_OUT;
                    logout_help();
                    yield "logout";
                }
                case "q", "quit" -> {
                    res = "quit";
                    yield "quit";
                }
                case "h", "help" -> {
                    login_help();
                    yield "help";
                }
                default -> {
                    login_help();
                    yield "bad command";
                }
            };
        } catch (Exception ex){
            System.err.print("logged_out_eval error");
            return ex.getMessage();
        }
    }

    private String playing_eval(String line){
        try{
            String[] tokens = line.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "m", "move" -> "move";
                case "s", "show" -> "show";
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
            System.err.print("playing_eval error");
            return ex.getMessage();
        }
    }

    private String observing_eval(String line){
        try{
            String[] tokens = line.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "s", "show" -> "show";
                case "q", "quit" -> {
                    res = "quit";
                    yield "quit";
                }
                case "h", "help" -> {
                    observing_help();
                    yield "help";
                }
                default -> {
                    observing_help();
                    yield "bad command";
                }
            };
        } catch (Exception ex){
            System.err.print("observing_eval error");
            return ex.getMessage();
        }
    }

    public void tick() {
        switch(state){
            case INIT:
                System.out.println("Welcome to chess! Type \"Help\" to list commands.");
                state = game_state.LOGGED_OUT;
                logout_help();
            case LOGGED_OUT:
                System.out.print("[LOGGED OUT: Not playing] >>> ");
                System.out.println(logged_out_eval(scanner.nextLine()));
                break;
            case LOGGED_IN:
                System.out.print("[LOGGED IN: Not playing] >>> ");
                System.out.println(logged_in_eval(scanner.nextLine()));
                break;
            case PLAYING:
                System.out.printf("[PLAYING: %s] >>> ", color);
                print_board();
                System.out.println(playing_eval(scanner.nextLine()));
                break;
            case OBSERVING:
                System.out.print("[OBSERVING] >>> ");
                print_board();
                System.out.println(observing_eval(scanner.nextLine()));
                break;
            default:
                throw new IllegalStateException("No client state error");
        }
    }

    private void logout_help(){
        System.out.println(" \u001b[;;4mCommands:\u001b[;;0m");
        System.out.println("  \u001b[33;49;1m\"r\"/\"register\" <USERNAME> <PASSWORD> <EMAIL> \u001b[34;49;1m- to create an account");
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
        System.out.println("  \u001b[33;49;1m\"h\"/\"help\" \u001b[34;49;1m- with possible commands\u001b[;;0m");
    }

    private void playing_help(){
        System.out.println(" \u001b[;;4mCommands:\u001b[;;0m");
        System.out.println("  \u001b[33;49;1m\"m\"/\"move\" <POS1> <POS2> \u001b[34;49;1m- a piece");
        System.out.println("  \u001b[33;49;1m\"s\"/\"show\" \u001b[34;49;1m- the board");
        System.out.println("  \u001b[33;49;1m\"q\"/\"quit\" \u001b[34;49;1m- playing chess");
        System.out.println("  \u001b[33;49;1m\"h\"/\"help\" \u001b[34;49;1m- with possible commands\u001b[;;0m");
    }

    private void observing_help(){
        System.out.println(" \u001b[;;4mCommands:\u001b[;;0m");
        System.out.println("  \u001b[33;49;1m\"s\"/\"show\" \u001b[34;49;1m- the board");
        System.out.println("  \u001b[33;49;1m\"q\"/\"quit\" \u001b[34;49;1m- playing chess");
        System.out.println("  \u001b[33;49;1m\"h\"/\"help\" \u001b[34;49;1m- with possible commands\u001b[;;0m");
    }

    private void print_board(){

    }
}
