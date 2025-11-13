package ui;

import chess.*;

import java.util.Scanner;

public class Client {
    public enum game_state {
        INIT, LOGGED_OUT, LOGGED_IN, PLAYING, OBSERVING
    }

    private game_state state;
    private ChessGame.TeamColor color;

    public Client(){
        init();
    }

    private void init(){
        state = game_state.INIT;
    }

    public void read_input(){
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
    }

    public void tick(){
        switch(state){
            case INIT:
                System.out.println("Welcome to chess! Type \"Help\" to list commands.");
                state = game_state.LOGGED_OUT;
            case LOGGED_OUT:
                System.out.print("[LOGGED OUT: Not playing] >>> ");
                break;
            case LOGGED_IN:
                System.out.print("[LOGGED IN: Not playing] >>> ");
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

    public void login_help(){
        System.out.println("  \"c\"/\"create\" <NAME> - a game");
        System.out.println("  \"li\"/\"list\" - games");
        System.out.println("  \"j\"/\"join\" <ID> [WHITE|BLACK] - a game");
        System.out.println("  \"o\"/\"observe\" <ID> - a game");
        System.out.println("  \"lo\"/\"logout\" - when you are done");
        System.out.println("  \"q\"/\"quit\" - playing chess");
        System.out.println("  \"h\"/\"help\" - with possible commands");
    }

    public void logout_help(){
        System.out.println("  \"r\"/\"register\" <USERNAME> <PASSWORD> <EMAIL> - to create an account");
        System.out.println("  \"l\"/\"login\" <USERNAME> <PASSWORD> - to play chess");
        System.out.println("  \"q\"/\"quit\" - playing chess");
        System.out.println("  \"h\"/\"help\" - with possible commands");
    }
}
