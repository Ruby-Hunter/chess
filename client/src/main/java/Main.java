import chess.*;

import java.util.Scanner;

public class Main {
    public enum game_state {
        LOGGED_OUT, LOGGED_IN, IN_GAME
    }

    private game_state state;

    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
    }

    public void init(){
        state = game_state.LOGGED_OUT;
    }

    public void read_input(){
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
    }
}