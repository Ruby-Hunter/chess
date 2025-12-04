package ui;

import chess.ChessBoard;

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
    private static final String A_ROW = SIDES + "    a  b  c  d  e  f  g  h    " + RESET;
    private static String boardString;

    public static void printWhiteBoard(ChessBoard board){
        boardString = "";
        boardString += A_ROW;
        System.out.print(boardString);
    }

    public static void printBlackBoard(ChessBoard board){
        boardString = "";

        System.out.print(boardString);
    }
}
