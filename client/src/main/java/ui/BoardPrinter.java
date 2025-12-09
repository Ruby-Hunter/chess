package ui;

import chess.*;

import java.util.Collection;
import java.util.HashSet;

import static chess.ChessGame.TeamColor.WHITE;

public class BoardPrinter {
    private static final String ESC = "\u001B";
    private static final String RESET = ESC + "[0m";
    private static final String SIDES = ESC + "[30;100;1m";
    private static final String BG_WHITE = ESC + "[;107;1m";
    private static final String BG_BLACK = ESC + "[;40;1m";
    private static final String WHITE_BG_WHITE = ESC + "[31;107;1m";
    private static final String WHITE_BG_BLACK = ESC + "[31;40;1m";
    private static final String BLACK_BG_WHITE = ESC + "[34;107;1m";
    private static final String BLACK_BG_BLACK = ESC + "[34;40;1m";
    private static final String WHITE_BG_YELLOW = ESC + "[31;43;1m";
    private static final String WHITE_BG_GREEN = ESC + "[31;42;1m";
    private static final String BLACK_BG_YELLOW = ESC + "[34;43;1m";
    private static final String BLACK_BG_GREEN = ESC + "[34;42;1m";
    private static final String EM_SP = "\u2003"; // equal to chess piece character length
    private static final String MD_SP = "\u2002" + "\u2005"; // 75% an EM_SP
    private static final String DUB_SP = MD_SP + MD_SP; // 2 MD_SP's
    private static final String SM_SP = "\u2002"; // half an EM_SP
    private static final String NL = "\n";
    private static final String A_ROW = SIDES + MD_SP+" "+DUB_SP+"a"+DUB_SP+"b"+DUB_SP+"c"+DUB_SP+"d"+DUB_SP+
            "e"+DUB_SP+"f"+DUB_SP+"g"+DUB_SP+"h"+DUB_SP+" "+MD_SP + RESET + NL;
    private static final String H_ROW = SIDES + MD_SP+" "+DUB_SP+"h"+DUB_SP+"g"+DUB_SP+"f"+DUB_SP+"e"+DUB_SP+
            "d"+DUB_SP+"c"+DUB_SP+"b"+DUB_SP+"a"+DUB_SP+" "+MD_SP + RESET + NL;
    private static String boardString;
    private static ChessGame game;

    public static String printBoard(ChessGame newGame, ChessGame.TeamColor color){
        game = newGame;
        return color == WHITE ?
                printBoardWhite(game.getBoard()) : printBoardBlack(game.getBoard());
    }

    public static String printBoard(ChessGame.TeamColor color){
        return color == WHITE ?
                printBoardWhite(game.getBoard()) : printBoardBlack(game.getBoard());
    }

    public static String printMoves(ChessGame.TeamColor color, ChessPosition pos){
        Collection<ChessMove> moves = game.validMoves(pos);
        return color == WHITE ?
                printMovesWhite(game.getBoard(), moves, pos) : printMovesBlack(game.getBoard(), moves, pos);
    }

    public static String printBoardWhite(ChessBoard board){
        boardString = A_ROW;
        for(int r = 8; r >= 1; r--){
            boardString += SIDES + MD_SP + r + MD_SP;
            for(int c = 1; c <= 8; c++){
                ChessPiece piece = board.getPiece(new ChessPosition(r, c));
                if(piece == null) {
                    boardString += ((r+c)%2 == 1) ? BG_WHITE : BG_BLACK;
                    boardString += " " + EM_SP + SM_SP; // print empty tile
                }
                else {
                    if(piece.getTeamColor().equals(WHITE))
                        boardString += ((r+c)%2 == 1) ? WHITE_BG_WHITE : WHITE_BG_BLACK;
                    else
                        boardString += ((r+c)%2 == 1) ? BLACK_BG_WHITE : BLACK_BG_BLACK;
                    boardString += " " + piece + SM_SP; // print piece
                }
            }
            boardString += SIDES + MD_SP + r + MD_SP + RESET + NL;
        }
        boardString += A_ROW;
        return boardString;
    }

    public static String printBoardBlack(ChessBoard board){
        boardString = H_ROW;
        for(int r = 1; r <= 8; r++){
            boardString += SIDES + MD_SP + r + MD_SP;
            for(int c = 8; c >= 1; c--){
                ChessPiece piece = board.getPiece(new ChessPosition(r, c));
                if(piece == null) {
                    boardString += ((r+c)%2 == 1) ? BG_WHITE : BG_BLACK;
                    boardString += " " + EM_SP + SM_SP; // print empty tile
                }
                else {
                    if(piece.getTeamColor().equals(WHITE))
                        boardString += ((r+c)%2 == 1) ? WHITE_BG_WHITE : WHITE_BG_BLACK;
                    else
                        boardString += ((r+c)%2 == 1) ? BLACK_BG_WHITE : BLACK_BG_BLACK;
                    boardString += " " + piece + SM_SP; // print piece
                }
            }
            boardString += SIDES + MD_SP + r + MD_SP + RESET + NL;
        }
        boardString += H_ROW;
        return boardString;
    }

    public static String printMovesWhite(ChessBoard board, Collection<ChessMove> moves, ChessPosition pos){
        HashSet<ChessPosition> validSpots = new HashSet<>();
        moves.forEach(move -> {
            validSpots.add(move.getEndPosition());
        });
        boardString = A_ROW;
        for(int r = 8; r >= 1; r--){
            boardString += SIDES + MD_SP + r + MD_SP;
            for(int c = 1; c <= 8; c++){
                ChessPiece piece = board.getPiece(new ChessPosition(r, c));
                if(piece == null) {
                    if (validSpots.contains(new ChessPosition(r, c)))
                        boardString += WHITE_BG_YELLOW;
                    else
                        boardString += ((r+c)%2 == 1) ? BG_WHITE : BG_BLACK;
                    boardString += " " + EM_SP + SM_SP; // print empty tile
                }
                else {
                    if(piece.getTeamColor().equals(WHITE)) {
                        if (validSpots.contains(new ChessPosition(r, c)))
                            boardString += WHITE_BG_YELLOW;
                        else if(pos.equals(new ChessPosition(r, c)))
                            boardString += WHITE_BG_GREEN;
                        else
                            boardString += ((r + c) % 2 == 1) ? WHITE_BG_WHITE : WHITE_BG_BLACK;
                    }
                    else{
                        if (validSpots.contains(new ChessPosition(r, c)))
                            boardString += BLACK_BG_YELLOW;
                        else if(pos.equals(new ChessPosition(r, c)))
                            boardString += BLACK_BG_GREEN;
                        else
                            boardString += ((r + c) % 2 == 1) ? BLACK_BG_WHITE : BLACK_BG_BLACK;
                    }
                    boardString += " " + piece + SM_SP; // print piece
                }
            }
            boardString += SIDES + MD_SP + r + MD_SP + RESET + NL;
        }
        boardString += A_ROW;
        return boardString;
    }

    public static String printMovesBlack(ChessBoard board, Collection<ChessMove> moves, ChessPosition pos){
        HashSet<ChessPosition> validSpots = new HashSet<>();
        moves.forEach(move -> {
            validSpots.add(move.getEndPosition());
        });
        boardString = H_ROW;
        for(int r = 1; r <= 8; r++){
            boardString += SIDES + MD_SP + r + MD_SP;
            for(int c = 8; c >= 1; c--){
                ChessPiece piece = board.getPiece(new ChessPosition(r, c));
                if(piece == null) {
                    if (validSpots.contains(new ChessPosition(r, c)))
                        boardString += WHITE_BG_YELLOW;
                    else
                        boardString += ((r+c)%2 == 1) ? BG_WHITE : BG_BLACK;
                    boardString += " " + EM_SP + SM_SP; // print empty tile
                }
                else {
                    if(piece.getTeamColor().equals(WHITE)) {
                        if (validSpots.contains(new ChessPosition(r, c)))
                            boardString += WHITE_BG_YELLOW;
                        else if(pos.equals(new ChessPosition(r, c)))
                            boardString += WHITE_BG_GREEN;
                        else
                            boardString += ((r + c) % 2 == 1) ? WHITE_BG_WHITE : WHITE_BG_BLACK;
                    }
                    else{
                        if (validSpots.contains(new ChessPosition(r, c)))
                            boardString += BLACK_BG_YELLOW;
                        else if(pos.equals(new ChessPosition(r, c)))
                            boardString += BLACK_BG_GREEN;
                        else
                            boardString += ((r + c) % 2 == 1) ? BLACK_BG_WHITE : BLACK_BG_BLACK;
                    }
                    boardString += " " + piece + SM_SP; // print piece
                }
            }
            boardString += SIDES + MD_SP + r + MD_SP + RESET + NL;
        }
        boardString += H_ROW;
        return boardString;
    }
}
