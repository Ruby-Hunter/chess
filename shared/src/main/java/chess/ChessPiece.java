package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, abut you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    public ChessPiece(ChessPiece template){
        this.pieceColor = template.pieceColor;
        this.type = template.type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        PieceMovesCalculator calc;

        switch(type){
            case KING -> calc = new KingMovesCalculator();
            case QUEEN -> calc = new QueenMovesCalculator();
            case BISHOP -> calc = new BishopMovesCalculator();
            case KNIGHT -> calc = new KnightMovesCalculator();
            case ROOK -> calc = new RookMovesCalculator();
            case PAWN -> calc = new PawnMovesCalculator();
            default -> throw new IllegalStateException("Not a piece");
        }
        
        return calc.pieceMoves(board, myPosition);
    }

    /**
     * Chess piece Unicode characters
     */
    private enum ChessPieceType {
        WHITE_KING("♔"), WHITE_QUEEN("♕"), WHITE_ROOK("♖"),
        WHITE_BISHOP("♗"), WHITE_KNIGHT("♘"), WHITE_PAWN("♙"),
        BLACK_KING("♚"), BLACK_QUEEN("♛"), BLACK_ROOK("♜"),
        BLACK_BISHOP("♝"), BLACK_KNIGHT("♞"), BLACK_PAWN("♟");

        private final String unicodeChar;

        ChessPieceType(String unicodeChar) {
            this.unicodeChar = unicodeChar;
        }

        public String getUnicodeChar() {
            return unicodeChar;
        }
    }

    /**
     * @return Chess piece ascii character based on type and color
     */
    @Override
    public String toString() {
        if(pieceColor == ChessGame.TeamColor.WHITE){
            return switch(type){
                case KING -> ChessPieceType.WHITE_KING.getUnicodeChar();
                case QUEEN -> ChessPieceType.WHITE_QUEEN.getUnicodeChar();
                case BISHOP -> ChessPieceType.WHITE_BISHOP.getUnicodeChar();
                case KNIGHT -> ChessPieceType.WHITE_KNIGHT.getUnicodeChar();
                case ROOK -> ChessPieceType.WHITE_ROOK.getUnicodeChar();
                case PAWN -> ChessPieceType.WHITE_PAWN.getUnicodeChar();
            };
        } else{
            return switch(type){
                case KING -> ChessPieceType.BLACK_KING.getUnicodeChar();
                case QUEEN -> ChessPieceType.BLACK_QUEEN.getUnicodeChar();
                case BISHOP -> ChessPieceType.BLACK_BISHOP.getUnicodeChar();
                case KNIGHT -> ChessPieceType.BLACK_KNIGHT.getUnicodeChar();
                case ROOK -> ChessPieceType.BLACK_ROOK.getUnicodeChar();
                case PAWN -> ChessPieceType.BLACK_PAWN.getUnicodeChar();
            };
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}