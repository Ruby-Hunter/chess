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
                    yield "register";
                }
                case "l", "login" -> {
                    state = game_state.LOGGED_IN;
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
                    yield "join";
                }
                case "o", "observe" -> {
                    state = game_state.OBSERVING;
                    yield "observe";
                }
                case "lo", "logout" -> {
                    state = game_state.LOGGED_OUT;
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

    public void tick(){
        switch(state){
            case INIT:
                System.out.println("Welcome to chess! Type \"Help\" to list commands.");
                state = game_state.LOGGED_OUT;
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
                break;
            case OBSERVING:
                System.out.print("[OBSERVING] >>> ");
                break;
            default:
                throw new IllegalStateException("No client state error");
        }
    }

    private void logout_help(){
        System.out.println(" Commands:");
        System.out.println("  \"r\"/\"register\" <USERNAME> <PASSWORD> <EMAIL> - to create an account");
        System.out.println("  \"l\"/\"login\" <USERNAME> <PASSWORD> - to play chess");
        System.out.println("  \"q\"/\"quit\" - playing chess");
        System.out.println("  \"h\"/\"help\" - with possible commands");
    }

    private void login_help(){
        System.out.println("  \"c\"/\"create\" <NAME> - a game");
        System.out.println("  \"li\"/\"list\" - games");
        System.out.println("  \"j\"/\"join\" <ID> [WHITE|BLACK] - a game");
        System.out.println("  \"o\"/\"observe\" <ID> - a game");
        System.out.println("  \"lo\"/\"logout\" - when you are done");
        System.out.println("  \"q\"/\"quit\" - playing chess");
        System.out.println("  \"h\"/\"help\" - with possible commands");
    }

    private void print_board(){

    }
}
