package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ui.BoardPrinter;
import ui.Client;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ClientTests {
    @Test // tests printing out an empty board
    void printBoardTest(){
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        String result = BoardPrinter.printBoardWhite(board); result += "\n";
        result += BoardPrinter.printBoardBlack(board);
        System.out.print(result);
        Assertions.assertEquals("f886dab381bb451bbadd8210e055d77080a30f82ddc2c2048a090aa641241a0a", sha256(result));
    }

    @Test // tests printing out a board with a piece moved
    void printNewBoardTest(){
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        board.addPiece(new ChessPosition(1, 2), null);
        board.addPiece(new ChessPosition(3, 1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        String result = BoardPrinter.printBoardWhite(board); result += "\n";
        result += BoardPrinter.printBoardBlack(board);
        System.out.print(result);
        Assertions.assertEquals("0994af27a11830b945075f413f86ae445f45a8a781c3e9d3a382f8804f20a40d", sha256(result));
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());

            // Convert bytes to hex
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
