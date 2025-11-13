import chess.*;

import java.util.Scanner;

public class Main {
    public enum game_state {
        INIT, LOGGED_OUT, LOGGED_IN, PLAYING, OBSERVING
    }

    private game_state state;
    private ChessGame.TeamColor color;

    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
    }

    public void init(){
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

    public void print_help(){

    }
}