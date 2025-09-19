package chess;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

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
}