package ui;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;

public class BoardPrinter {
    // Sides: [30;100;1m
    // Blank White: [;107;1m
    // Blank Black: [;40;1m
    // White on White: [34;107;1m
    // White on Black: [34;40;1m
    // Black on White: [31;107;1m
    // Black on Black: [31;40;1m
    private static final String ESC = "\u001B";
    private static final String RESET = ESC + "[0m";
    private static final String SIDES = ESC + "[30;100;1m";
    private static final String BG_WHITE = ESC + "[;107;1m";
    private static final String BG_BLACK = ESC + "[;40;1m";
    private static final String WHITE_ON_WHITE = ESC + "[34;107;1m";
    private static final String WHITE_ON_BLACK = ESC + "[34;40;1m";
    private static final String BLACK_ON_WHITE = ESC + "[31;107;1m";
    private static final String BLACK_ON_BLACK = ESC + "[31;40;1m";
    private static final String EM_SP = "\u2003";
    private static final String MD_SP = "\u2002" + "\u2005";
    private static final String DUB_SP = MD_SP + MD_SP;
    private static final String SM_SP = "\u2002";
    private static final String NL = "\n";
    private static final String A_ROW = SIDES + MD_SP+" "+DUB_SP+"a"+DUB_SP+"b"+DUB_SP+"c"+DUB_SP+"d"+DUB_SP+
            "e"+DUB_SP+"f"+DUB_SP+"g"+DUB_SP+"h"+DUB_SP+" "+MD_SP + RESET + NL;
    private static final String H_ROW = SIDES + MD_SP+" "+DUB_SP+"h"+DUB_SP+"g"+DUB_SP+"f"+DUB_SP+"e"+DUB_SP+
            "d"+DUB_SP+"c"+DUB_SP+"b"+DUB_SP+"a"+DUB_SP+" "+MD_SP + RESET + NL;
    private static String boardString;

    public static void printBoardWhite(ChessBoard board){
        boardString = A_ROW;
        for(int r = 8; r >= 1; r--){
            boardString += SIDES + MD_SP + r + MD_SP;
            for(int c = 1; c <= 8; c++){
                if((r+c)%2 == 1)boardString += BG_WHITE;
                else boardString += BG_BLACK;
                ChessPiece piece = board.getPiece(new ChessPosition(r, c));
                if(piece == null) boardString += " " + EM_SP + SM_SP; // print empty tile
                else boardString += " " + piece + SM_SP; // print piece
            }
            boardString += SIDES + MD_SP + r + MD_SP + RESET + NL;
        }
        boardString += A_ROW;
        System.out.print(boardString);
    }

    public static void printBoardBlack(ChessBoard board){
        boardString = H_ROW;
        for(int r = 1; r <= 8; r++){
            boardString += SIDES + MD_SP + r + MD_SP;
            for(int c = 8; c >= 1; c--){
                if((r+c)%2 == 1)boardString += BG_WHITE;
                else boardString += BG_BLACK;
                ChessPiece piece = board.getPiece(new ChessPosition(r, c));
                if(piece == null) boardString += " " + EM_SP + SM_SP; // print empty tile
                else boardString += " " + piece + SM_SP; // print piece
            }
            boardString += SIDES + MD_SP + r + MD_SP + RESET + NL;
        }
        boardString += H_ROW;
        System.out.print(boardString);
    }
}
